package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.dao.MemberDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.databinding.ActivityAddItemBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加消费项页 或 添加人员页面
 */
public class AddItemActivity extends BaseActivity<ActivityAddItemBinding> {

	/**
	 * 缓存一下对应图标的目录信息
	 * 前提用户倾向于在这个页面停留时间较长,且图标文件万年不变
	 */
	private static final LoadingCache<String, List<GiteeContent>> CACHE = CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, List<GiteeContent>>() {
		@Override
		public List<GiteeContent> load(@NotNull String path) {
			return BusinessTravelResourceApi.getV5ReposOwnerRepoContents(path).stream().filter(item -> "dir".equals(item.getType())).collect(Collectors.toList());
		}
	});
	/**
	 * 图标大类列表
	 */
	private static final List<GiteeContent> iconTypeList = new ArrayList<>();
	private AddItemRecyclerViewAdapter addItemRecyclerViewAdapter;
	/**
	 * 当前显示的是消费项的图标还是人员的图标
	 */
	private ItemTypeEnum itemTypeEnum = ItemTypeEnum.CONSUMPTION;
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

	@Override
	protected void inject() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//icon分类和列表
		SwipeRecyclerView swipeRecyclerView = viewBinding.UIAddItemActivitySwipeRecyclerView;
		registerSwipeRecyclerView(swipeRecyclerView);

		//返回按钮被点击后
		ImageButton returnButton = viewBinding.UIAddItemActivityImageButtonReturn;
		registerReturnButton(returnButton);

		//itemTypeEnum 跳过来的是什么类型
		final String itemType = getIntent().getStringExtra("itemType");
		if (StringUtils.isNotBlank(itemType)) {
			itemTypeEnum = ItemTypeEnum.valueOf(itemType);
		}

		//是添加人员还是添加消费项
		TextView headerText = viewBinding.UIAddItemActivityTextViewHeader;
		registerHeaderText(headerText);

		TextView saveButton = viewBinding.UIAddItemActivityTextViewSave;
		registerSaveButton(saveButton);

	}

	private void registerSaveButton(TextView saveButton) {
		saveButton.setOnClickListener(v -> {
			if (itemTypeEnum == ItemTypeEnum.CONSUMPTION) {
				saveConsumption();
			} else if (itemTypeEnum == ItemTypeEnum.MEMBER) {
				saveMember();
			}
			finish();
		});
	}

	private void saveMember() {
		MemberDao memberDao = AppDatabase.getInstance(this).memberDao();
		Member member = new Member();

		String name = viewBinding.UIAddItemActivityEditTextName.getText().toString();
		member.setName(name);
		member.setGender(0);
		member.setIconDownloadUrl(lastSelectedImageIcon.getIconDownloadUrl());
		member.setIconName(lastSelectedImageIcon.getName());
		//先查询最大的sortId
		Long maxSortId = Optional.ofNullable(memberDao.selectMaxSort()).orElse(0L);
		member.setSortId(maxSortId);
		member.setCreateTime(DateTimeUtil.format(new Date()));
		memberDao.insert(member);
	}

	private void saveConsumption() {
		String consumptionType = getIntent().getStringExtra("consumptionType");
		if (StringUtils.isBlank(consumptionType)) {
			throw new IllegalArgumentException("未知消费项类型");
		}
		ConsumptionTypeEnum consumptionTypeEnum = ConsumptionTypeEnum.valueOf(consumptionType);

		ConsumptionDao consumptionDao = AppDatabase.getInstance(this).consumptionDao();

		String name = viewBinding.UIAddItemActivityEditTextName.getText().toString();
		Consumption consumption = new Consumption();
		consumption.setName(name);
		consumption.setIconDownloadUrl(lastSelectedImageIcon.getIconDownloadUrl());
		consumption.setIconName(lastSelectedImageIcon.getName());
		consumption.setConsumptionType(consumptionTypeEnum.name());
		consumption.setCreateTime(DateTimeUtil.format(new Date()));
		consumption.setModifyTime(DateTimeUtil.format(new Date()));
		//先查询最大的sortId
		Long maxSortId = Optional.ofNullable(consumptionDao.selectMaxSortIdByType(consumptionType)).orElse(0L);
		consumption.setSortId(maxSortId);
		consumptionDao.insert(consumption);
	}

	private void registerHeaderText(TextView headerText) {
		if (itemTypeEnum == ItemTypeEnum.CONSUMPTION) {
			headerText.setText("添加类别");
		} else if (itemTypeEnum == ItemTypeEnum.MEMBER) {
			headerText.setText("添加人员");
		}

	}

	private void registerReturnButton(ImageButton returnButton) {
		returnButton.setOnClickListener(v -> finish());
	}

	private void registerSwipeRecyclerView(SwipeRecyclerView swipeRecyclerView) {
		swipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		addItemRecyclerViewAdapter = new AddItemRecyclerViewAdapter(iconTypeList, this);
		swipeRecyclerView.setAdapter(addItemRecyclerViewAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshData(itemTypeEnum);
	}

	private void refreshData(ItemTypeEnum itemTypeEnum) {
		try {
			//如果5秒钟,拿不回数据,说明网络不好
			String path = "/icon/" + itemTypeEnum.name();
			FutureUtil.supplyAsync(() -> getIconTypeListFromCache(path))
					.thenApply(list -> list.stream().sorted(Comparator.comparing(GiteeContent::getItemSort)).collect(Collectors.toList()))
					.thenAccept(list -> {
						iconTypeList.clear();
						iconTypeList.addAll(list);
					}).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			LogToast.errorShow("网络环境较差,请稍后重试");
		}
		addItemRecyclerViewAdapter.notifyDataSetChanged();
	}

	private List<GiteeContent> getIconTypeListFromCache(String path) {
		try {
			return CACHE.get(path);
		} catch (ExecutionException e) {
			LogToast.errorShow("网络环境较差,请稍后重试");
		}
		return Collections.emptyList();
	}
}