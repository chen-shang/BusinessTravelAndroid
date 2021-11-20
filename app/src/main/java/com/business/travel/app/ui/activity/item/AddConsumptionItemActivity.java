package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.databinding.ActivityAddConsumptionItemBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.ItemIconInfo;
import com.business.travel.app.ui.activity.bill.ItemIconRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class AddConsumptionItemActivity extends BaseActivity<ActivityAddConsumptionItemBinding> {

	private final List<String> typeList = new ArrayList<>();
	private final List<ImageIconInfo> iconList = new ArrayList<>();

	private final List<ItemIconInfo> itemIconInfos = new ArrayList<>();
	private Adapter adapter;

	@SneakyThrows
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CompletableFutureUtil.runAsync(() -> {
			refreshIconItem("1");
		}).get(20, TimeUnit.SECONDS);
		mock();
		Objects.requireNonNull(getSupportActionBar()).hide();
		viewBinding.SwipeRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
		adapter = new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_consumer_icon_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				if (CollectionUtils.isEmpty(itemIconInfos)) {
					return;
				}
				final ItemIconInfo itemIconInfo = itemIconInfos.get(position);
				final String path = itemIconInfo.getPath();
				final TextView textView = holder.itemView.findViewById(R.id.path);
				textView.setText(path);

				final List<ImageIconInfo> iconDownloadUrl = itemIconInfo.getImageIconInfos();
				final SwipeRecyclerView swipeRecyclerView = holder.itemView.findViewById(R.id.SwipeRecyclerView2);
				LayoutManager layoutManager = new GridLayoutManager(AddConsumptionItemActivity.this, 5);
				swipeRecyclerView.setLayoutManager(layoutManager);
				ItemIconRecyclerViewAdapter billRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, iconDownloadUrl, AddConsumptionItemActivity.this);
				swipeRecyclerView.setAdapter(billRecyclerViewAdapter);
			}

			@Override
			public int getItemCount() {
				return typeList.size();
			}
		};
		viewBinding.SwipeRecyclerView1.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void mock() {
		for (int i = 0; i < 10; i++) {
			typeList.add(i + "");
		}
		Arrays.stream(IconEnum.values()).forEach(iconEnum -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			//imageIconInfo.setResourceId(iconEnum.getResourceId());
			imageIconInfo.setIconName(iconEnum.getDescription());
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		});
	}

	public void refreshIconItem(String type) {
		//先查询某类型下的目录
		List<GiteeContent> v5ReposOwnerRepoGiteeContentsSpend = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出");
		List<String> dirList = v5ReposOwnerRepoGiteeContentsSpend.stream()
				.filter(item -> "dir".equals(item.getType()))
				.map(GiteeContent::getName)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(dirList)) {
			return;
		}

		final List<ItemIconInfo> svg1 = dirList.stream().map(dir -> {
			final List<GiteeContent> v5ReposOwnerRepoContents = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出/" + dir);
			final List<ImageIconInfo> svg = v5ReposOwnerRepoContents.stream()
					.filter(item -> "file".equals(item.getType()))
					.filter(item -> item.getName().endsWith("svg"))
					.map(item -> {
						ImageIconInfo imageIconInfo = new ImageIconInfo();
						//imageIconInfo.setResourceId(0);
						imageIconInfo.setIconDownloadUrl(item.getDownloadUrl());
						imageIconInfo.setSelected(false);
						return imageIconInfo;
					}).collect(Collectors.toList());
			ItemIconInfo itemIconInfo = new ItemIconInfo();
			itemIconInfo.setPath(dir);
			itemIconInfo.setImageIconInfos(svg);
			return itemIconInfo;
		}).collect(Collectors.toList());

		itemIconInfos.clear();
		itemIconInfos.addAll(svg1);
	}
}