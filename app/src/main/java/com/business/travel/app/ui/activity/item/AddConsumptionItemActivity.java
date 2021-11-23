package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.databinding.ActivityAddConsumptionItemBinding;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.ItemIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加消费项页
 */
public class AddConsumptionItemActivity extends BaseActivity<ActivityAddConsumptionItemBinding> {

	private final List<ItemIconInfo> itemIconInfoList = new ArrayList<>();
	private AddConsumptionItemRecyclerViewAdapter addConsumptionItemRecyclerViewAdapter;
	/**
	 * 最后被选中的icon的ViewImageView
	 */
	@Setter
	@Getter
	private ImageView lastSelectedImageView;

	/**
	 * 最后被选中的icon的ImageIconInfo
	 */
	@Setter
	@Getter
	private ImageIconInfo lastSelectedImageIcon;

	/**
	 * 当前显示的是消费项的图标还是同行人的图标
	 */
	private ItemTypeEnum itemTypeEnum = ItemTypeEnum.CONSUMPTION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		//消费项icon分类和列表
		viewBinding.UIAddConsumptionItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		addConsumptionItemRecyclerViewAdapter = new AddConsumptionItemRecyclerViewAdapter(itemIconInfoList, this);
		viewBinding.UIAddConsumptionItemSwipeRecyclerView.setAdapter(addConsumptionItemRecyclerViewAdapter);

		//返回按钮被点击后
		viewBinding.UIAddConsumptionItemImageButtonReturn.setOnClickListener(v -> {
			finish();
		});

		final String consumptionType = getIntent().getStringExtra("consumptionType");
		//保存按钮被点击后
		final ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		viewBinding.UIAddConsumptionItemTextViewSave.setOnClickListener(v -> {
			final String iconDownloadUrl = lastSelectedImageIcon.getIconDownloadUrl();
			String name = viewBinding.UIAddConsumptionItemEditTextName.getText().toString();
			ConsumptionItem consumptionItem = new ConsumptionItem();
			consumptionItem.setName(name);
			consumptionItem.setIconDownloadUrl(lastSelectedImageIcon.getIconDownloadUrl());
			consumptionItem.setIconName(lastSelectedImageIcon.getName());
			consumptionItem.setConsumptionType(consumptionType);
			consumptionItem.setCreateTime(DateTimeUtil.format(new Date()));
			consumptionItem.setModifyTime(DateTimeUtil.format(new Date()));
			//先查询最大的sortId
			Long maxSortId = consumptionItemDao.selectMaxSortIdByType(consumptionType);
			maxSortId = Optional.ofNullable(maxSortId).orElse(0L);
			consumptionItem.setSortId(maxSortId + 1);
			consumptionItem.setIsDeleted(1);
			consumptionItemDao.insert(consumptionItem);
			LogToast.infoShow(iconDownloadUrl + ":" + name);
			finish();
		});
	}

	@SneakyThrows
	@Override
	protected void onStart() {
		super.onStart();
		CompletableFutureUtil
				.runAsync(() -> refreshIconItem(itemTypeEnum.name()))
				.whenComplete((unused, throwable) -> addConsumptionItemRecyclerViewAdapter.notifyDataSetChanged());
	}

	public void refreshIconItem(String type) {
		String baseDir = "/icon/" + type;
		//先查询某类型下的目录
		List<GiteeContent> v5ReposOwnerRepoGiteeContentsSpend = BusinessTravelResourceApi.getV5ReposOwnerRepoContents(baseDir);
		List<String> dirList = v5ReposOwnerRepoGiteeContentsSpend.stream()
				.filter(item -> "dir".equals(item.getType()))
				.map(GiteeContent::getName)
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(dirList)) {
			return;
		}

		final List<ItemIconInfo> itemIconInfoList = dirList.stream()
				.map(dir -> BusinessTravelResourceApi.getV5ReposOwnerRepoContents(baseDir + "/" + dir))
				.filter(CollectionUtils::isNotEmpty)
				.map(v5ReposOwnerRepoContents -> {
					List<ImageIconInfo> svg = v5ReposOwnerRepoContents.stream()
							.filter(item -> "file".equals(item.getType()))
							.filter(item -> item.getName().endsWith("svg"))
							.map(this::convertToImageIconInfo)
							.collect(Collectors.toList());
					String path = v5ReposOwnerRepoContents.get(0).getPath();
					//todo path name
					ItemIconInfo itemIconInfo = new ItemIconInfo();
					itemIconInfo.setPath(path);
					itemIconInfo.setImageIconInfos(svg);
					return itemIconInfo;
				}).collect(Collectors.toList());

		this.itemIconInfoList.clear();
		this.itemIconInfoList.addAll(itemIconInfoList);
	}

	@NotNull
	private ImageIconInfo convertToImageIconInfo(GiteeContent item) {
		ImageIconInfo imageIconInfo = new ImageIconInfo();
		imageIconInfo.setIconDownloadUrl(item.getDownloadUrl());
		imageIconInfo.setName(item.getName());
		imageIconInfo.setSelected(false);
		return imageIconInfo;
	}
}