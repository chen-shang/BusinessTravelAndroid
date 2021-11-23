package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.databinding.ActivityAddConsumptionItemBinding;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenshang
 * 添加消费项页
 */
public class AddConsumptionItemActivity extends BaseActivity<ActivityAddConsumptionItemBinding> {

	/**
	 * 缓存一下对应图标的目录信息
	 * 前提用户倾向于在这个页面停留时间较长,且图标文件万年不变
	 */
	private static final LoadingCache<String, List<GiteeContent>> CACHE = CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, List<GiteeContent>>() {
		@Override
		public List<GiteeContent> load(String path) {
			return BusinessTravelResourceApi.getV5ReposOwnerRepoContents(path).stream().filter(item -> "dir".equals(item.getType())).collect(Collectors.toList());
		}
	});
	/**
	 * 图标大类列表
	 */
	private final List<GiteeContent> iconTypeList = new ArrayList<>();
	/**
	 * 当前显示的是消费项的图标还是同行人的图标
	 */
	private ItemTypeEnum itemTypeEnum = ItemTypeEnum.CONSUMPTION;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		//消费项icon分类和列表
		viewBinding.UIAddConsumptionItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		addConsumptionItemRecyclerViewAdapter = new AddConsumptionItemRecyclerViewAdapter(iconTypeList, this);
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
			finish();
		});

		//itemTypeEnum 跳过来的是什么类型 TODO
	}

	@Override
	protected void onStart() {
		super.onStart();
		//todo
		refreshData();
	}

	private void refreshData() {
		try {
			//如果5秒钟,拿不回数据,说明网络剧不好
			CompletableFutureUtil.supplyAsync(() -> {
				String path = "/icon/" + itemTypeEnum.name();
				return getIconTypeListFromCache(path);
			}).thenAccept(list -> {
				iconTypeList.clear();
				iconTypeList.addAll(list);
			}).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			LogToast.errorShow("网络环境较差,请稍后重试");
		}
		addConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
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