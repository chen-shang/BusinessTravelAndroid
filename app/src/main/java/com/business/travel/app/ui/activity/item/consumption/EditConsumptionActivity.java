package com.business.travel.app.ui.activity.item.consumption;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.databinding.ActivityEditConsumptionBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.converter.ConsumptionConverter;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.ui.activity.item.AddItemActivity;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

/**
 * @author chenshang
 */
public class EditConsumptionActivity extends BaseActivity<ActivityEditConsumptionBinding> {
	private final ConsumptionService consumptionService = new ConsumptionService(this);
	/**
	 * 消费项图标信息列表
	 */
	List<ImageIconInfo> consumptionImageIconList = new ArrayList<>();
	/**
	 * 消费项图标列表适配器
	 */
	private EditItemRecyclerViewAdapter editConsumptionRecyclerViewAdapter;
	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		//注册下拉列表事件
		registerSwipeRecyclerView();
		//注册收入支出按钮事件
		registerButton();
		//注册返回按钮操作事件
		registerEditConsumptionActivityImageButtonBack();
		//注册添加按钮操作事件
		registerConsumerItemButtonAddItem();
	}

	private void registerButton() {
		//支出按钮的背景
		GradientDrawable gradientDrawableExpense = (GradientDrawable)viewBinding.UIConsumerItemTextViewExpense.getBackground();
		//收入按钮的背景
		GradientDrawable gradientDrawableIncome = (GradientDrawable)viewBinding.UIConsumerItemTextViewIncome.getBackground();
		viewBinding.UIConsumerItemTextViewExpense.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.teal_800));
			this.consumptionType = ConsumptionTypeEnum.SPENDING;
			refreshConsumptionItem(ConsumptionTypeEnum.SPENDING);
		});

		viewBinding.UIConsumerItemTextViewIncome.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.teal_800));
			this.consumptionType = ConsumptionTypeEnum.INCOME;
			refreshConsumptionItem(ConsumptionTypeEnum.INCOME);
		});
	}

	private void registerSwipeRecyclerView() {
		ConsumptionDao consumptionDao = AppDatabase.getInstance(this).consumptionDao();
		editConsumptionRecyclerViewAdapter = new EditItemRecyclerViewAdapter(consumptionImageIconList, this);

		//设置布局
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);

		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(consumptionImageIconList, editConsumptionRecyclerViewAdapter)
						//当移动之后
						.onItemMove((consumptionItems, fromPosition, toPosition) -> {
							for (int i = fromPosition; i <= toPosition; i++) {
								ImageIconInfo imageIconInfo = consumptionImageIconList.get(i);
								System.out.println("我执行了 fromPosition:" + fromPosition + ",toPosition:" + toPosition + "imageIconInfo:" + imageIconInfo.getId() + "->" + i);
								consumptionService.updateMemberSort(imageIconInfo.getId(), (long)i);
							}
						})
		);

		//添加分隔线
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.addItemDecoration(new DefaultItemDecoration(Color.GRAY));
		//添加删除按钮
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(this).setImage(R.drawable.ic_base_delete).setHeight(LayoutParams.WRAP_CONTENT).setWidth(LayoutParams.WRAP_CONTENT);
			rightMenu.addMenuItem(deleteItem);//设置右边的侧滑
		});
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
			// 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
			menuBridge.closeMenu();
			// 左侧还是右侧菜单：0左1右
			int direction = menuBridge.getDirection();
			// 菜单在Item中的Position：
			int menuPosition = menuBridge.getPosition();
			//被删除的item
			ImageIconInfo imageIconInfo = consumptionImageIconList.get(adapterPosition);

			//先删除该元素
			Consumption consumption = new Consumption();
			consumption.setId(imageIconInfo.getId());
			consumption.setIsDeleted(DeleteEnum.DELETE.getCode());
			consumptionDao.softDelete(consumption);

			//移除元素
			consumptionImageIconList.remove(adapterPosition);
			editConsumptionRecyclerViewAdapter.notifyDataSetChanged();
			//然后改元素后面的排序需要更新
			for (int i = adapterPosition; i < consumptionImageIconList.size(); i++) {
				extracted(imageIconInfo, i);
			}
		});

		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionRecyclerViewAdapter);
	}

	private void extracted(ImageIconInfo imageIconInfo, int i) {
		ConsumptionDao consumptionDao = AppDatabase.getInstance(this).consumptionDao();
		Consumption consumption = new Consumption();
		consumption.setId(imageIconInfo.getId());
		consumption.setSortId((long)i);
		consumptionDao.update(consumption);
	}

	/**
	 * 注册添加按钮操作事件
	 */
	private void registerConsumerItemButtonAddItem() {
		viewBinding.UIConsumerItemButtonAddItem.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("consumptionType", consumptionType.name());
			intent.putExtra("itemType", ItemTypeEnum.CONSUMPTION.name());
			startActivity(intent);
		});
	}

	/**
	 * 注册返回按钮操作事件
	 */
	private void registerEditConsumptionActivityImageButtonBack() {
		//返回按钮点击后
		viewBinding.UIEditConsumptionActivityImageButtonBack.setOnClickListener(v -> {
			//记得保存一下顺序
			this.finish();
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshConsumptionItem(consumptionType);
	}

	private void refreshConsumptionItem(ConsumptionTypeEnum consumptionTypeEnum) {
		if (consumptionTypeEnum == null) {
			return;
		}

		List<Consumption> consumptionList = consumptionService.queryConsumptionItemByType(consumptionTypeEnum);
		final List<ImageIconInfo> newImage = consumptionList.stream().map(consumptionItem -> {
			final ImageIconInfo imageIconInfo = ConsumptionConverter.INSTANCE.convertImageIconInfo(consumptionItem);
			imageIconInfo.setItemType(ItemTypeEnum.CONSUMPTION.name());
			return imageIconInfo;
		}).collect(Collectors.toList());

		this.consumptionImageIconList.clear();
		this.consumptionImageIconList.addAll(newImage);
		editConsumptionRecyclerViewAdapter.notifyDataSetChanged();
	}
}