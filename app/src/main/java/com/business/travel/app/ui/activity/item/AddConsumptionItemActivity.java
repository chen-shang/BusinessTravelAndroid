package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.databinding.ActivityAddConsumptionItemBinding;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ItemIconInfo;
import com.business.travel.app.model.ItemIconInfo.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import lombok.SneakyThrows;

/**
 * @author chenshang
 */
public class AddConsumptionItemActivity extends BaseActivity<ActivityAddConsumptionItemBinding> {

	private final List<ItemIconInfo> itemIconInfoList = new ArrayList<>();

	private AddConsumptionItemRecyclerViewAdapter addConsumptionItemRecyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		viewBinding.SwipeRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
		addConsumptionItemRecyclerViewAdapter = new AddConsumptionItemRecyclerViewAdapter(itemIconInfoList, this);
		viewBinding.SwipeRecyclerView1.setAdapter(addConsumptionItemRecyclerViewAdapter);

		viewBinding.retn.setOnClickListener(v -> {
			finish();
		});
	}

	@SneakyThrows
	@Override
	protected void onStart() {
		super.onStart();
		CompletableFutureUtil.runAsync(() -> refreshIconItem("1")).get(20, TimeUnit.SECONDS);
		addConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
	}

	public void refreshIconItem(String type) {
		//先查询某类型下的目录
		List<GiteeContent> v5ReposOwnerRepoGiteeContentsSpend = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/支出");
		List<String> dirList = v5ReposOwnerRepoGiteeContentsSpend.stream()
				.filter(item -> "dir".equals(item.getType()))
				.map(GiteeContent::getName)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(dirList)) {
			return;
		}

		final List<ItemIconInfo> svg1 = dirList.stream().map(dir -> {
			final List<GiteeContent> v5ReposOwnerRepoContents = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/支出/" + dir);
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

		itemIconInfoList.clear();
		itemIconInfoList.addAll(svg1);
	}
}