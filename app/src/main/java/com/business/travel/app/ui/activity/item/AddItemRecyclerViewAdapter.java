package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.business.travel.app.ui.activity.item.AddItemRecyclerViewAdapter.AddItemRecyclerViewAdapterViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

public class AddItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<AddItemRecyclerViewAdapterViewAdapterViewHolder, GiteeContent> {
	private static final LoadingCache<String, List<GiteeContent>> cache =
			CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, List<GiteeContent>>() {
				@Override
				public List<GiteeContent> load(String path) {
					return BusinessTravelResourceApi.getV5ReposOwnerRepoContents(path).stream()
							.filter(item -> "file".equals(item.getType()))
							.filter(item -> item.getName().endsWith("svg"))
							.collect(Collectors.toList());
				}
			});

	public AddItemRecyclerViewAdapter(List<GiteeContent> itemIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
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
	public AddItemRecyclerViewAdapterViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_item_recycler_view_adapter, parent, false);
		return new AddItemRecyclerViewAdapterViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddItemRecyclerViewAdapterViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		//先获取到类型,也就是文件夹的名字
		GiteeContent giteeContent = dataList.get(position);
		final String name = giteeContent.getName();
		holder.iconPathTextView.setText(name);

		//然后开始处理每一项下面的图标
		List<ImageIconInfo> imageIconInfoList = new ArrayList<>();
		String path = giteeContent.getPath();
		//接下来是对icon的处理
		LayoutManager layoutManager = new GridLayoutManager(activity, 5);
		holder.imageIconInfoRecyclerView.setLayoutManager(layoutManager);
		AddItemRecyclerViewInnerAdapter billRecyclerViewAdapter = new AddItemRecyclerViewInnerAdapter(imageIconInfoList, activity);
		holder.imageIconInfoRecyclerView.setAdapter(billRecyclerViewAdapter);

		try {
			FutureUtil.supplyAsync(() -> getFromCache(path))
					.thenApply(giteeContents -> giteeContents.stream()
							.sorted(Comparator.comparingInt(GiteeContent::getItemSort))
							.map(this::convertImageIconInfo)
							.collect(Collectors.toList())
					).thenAccept(item -> {
						imageIconInfoList.clear();
						imageIconInfoList.addAll(item);
					}).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			LogToast.errorShow("网络环境较差,请稍后重试");
		}
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	@NotNull
	private ImageIconInfo convertImageIconInfo(GiteeContent item) {
		ImageIconInfo itemIconInfo = new ImageIconInfo();
		itemIconInfo.setName(item.getName());
		itemIconInfo.setIconDownloadUrl(item.getDownloadUrl());
		itemIconInfo.setSelected(false);
		return itemIconInfo;
	}

	@SuppressLint("NonConstantResourceId")
	static class AddItemRecyclerViewAdapterViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_AddItemRecyclerViewAdapter_TextView_IconPath)
		public TextView iconPathTextView;
		@BindView(R.id.UI_AddItemRecyclerViewAdapter_SwipeRecyclerView)
		public SwipeRecyclerView imageIconInfoRecyclerView;

		public AddItemRecyclerViewAdapterViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
