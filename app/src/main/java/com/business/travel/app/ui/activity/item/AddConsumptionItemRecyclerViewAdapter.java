package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.item.AddConsumptionItemRecyclerViewAdapter.AddConsumptionItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.business.travel.app.utils.LogToast;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

public class AddConsumptionItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<AddConsumptionItemRecyclerViewAdapterViewHolder, GiteeContent> {
	private static final LoadingCache<String, List<GiteeContent>> cache =
			CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, List<GiteeContent>>() {
				@Override
				public List<GiteeContent> load(String path) {
					return BusinessTravelResourceApi.getV5ReposOwnerRepoContents(path);
				}
			});

	public AddConsumptionItemRecyclerViewAdapter(List<GiteeContent> itemIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(itemIconInfos, baseActivity);
	}

	private List<GiteeContent> getFromCache(String path) {
		try {
			return cache.get(path);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@NonNull
	@NotNull
	@Override
	public AddConsumptionItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_consumption_icon_item, parent, false);
		return new AddConsumptionItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddConsumptionItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		GiteeContent giteeContent = dataList.get(position);

		final String name = giteeContent.getName();
		holder.iconPathTextView.setText(name);

		List<ImageIconInfo> imageIconInfoList = new ArrayList<>();
		String path = giteeContent.getPath();

		//接下来是对icon的处理
		LayoutManager layoutManager = new GridLayoutManager(activity, 5);
		holder.imageIconInfoRecyclerView.setLayoutManager(layoutManager);
		AddConsumptionItemIconRecyclerViewAdapter billRecyclerViewAdapter = new AddConsumptionItemIconRecyclerViewAdapter(imageIconInfoList, activity);
		holder.imageIconInfoRecyclerView.setAdapter(billRecyclerViewAdapter);

		try {
			CompletableFutureUtil.supplyAsync(() -> getFromCache(path))
					.thenApply(giteeContents ->
							           giteeContents.stream()
									           .filter(item -> "file".equals(item.getType()))
									           .filter(item -> item.getName().endsWith("svg"))
									           .map(item -> {
										           ImageIconInfo itemIconInfo = new ImageIconInfo();
										           itemIconInfo.setName(item.getName());
										           itemIconInfo.setIconDownloadUrl(item.getDownloadUrl());
										           itemIconInfo.setSelected(false);
										           return itemIconInfo;
									           }).collect(Collectors.toList())
					).thenAccept(item -> {
						imageIconInfoList.clear();
						imageIconInfoList.addAll(item);
					}).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			LogToast.errorShow("网络环境较差,请稍后重试");
		}
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	@SuppressLint("NonConstantResourceId")
	static class AddConsumptionItemRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_AddConsumptionItem_TextView_IconPath)
		public TextView iconPathTextView;
		@BindView(R.id.UI_AddConsumptionItem_SwipeRecyclerView)
		public SwipeRecyclerView imageIconInfoRecyclerView;

		public AddConsumptionItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
