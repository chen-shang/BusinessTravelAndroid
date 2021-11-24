package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.databinding.ActivityEditConsumptionItemBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;

/**
 * @author chenshang
 */
public class EditConsumptionItemActivity extends BaseActivity<ActivityEditConsumptionItemBinding> {
	List<ConsumptionItem> consumptionItemList = new ArrayList<>();
	private EditConsumptionItemRecyclerViewAdapter editConsumptionItemRecyclerViewAdapter;

	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editConsumptionItemRecyclerViewAdapter = new EditConsumptionItemRecyclerViewAdapter(consumptionItemList, this);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionItemRecyclerViewAdapter);

		ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();

		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setSwipeItemMenuEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(consumptionItemList, editConsumptionItemRecyclerViewAdapter)
						.onItemMove((consumptionItems, fromPosition, toPosition) -> {
							//更新排序 todo 优化

							for (int i = 0; i < consumptionItemList.size(); i++) {
								ConsumptionItem consumptionItem = consumptionItemList.get(i);
								consumptionItem.setSortId((long)i);
								consumptionItemDao.update(consumptionItem);
							}
						}).onItemDismiss((consumptionItems, integer) -> {
							consumptionItems.setIsDeleted(DeleteEnum.DELETE.getCode());
							consumptionItemDao.softDelete(consumptionItems);

							//数据库删除该元素
							for (int i = 0; i < consumptionItemList.size(); i++) {
								ConsumptionItem consumptionItem = consumptionItemList.get(i);
								consumptionItem.setSortId((long)i);
								consumptionItemDao.update(consumptionItem);
							}
						})
		);

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

		//注册返回按钮操作事件
		registerEditConsumptionActivityImageButtonBack();

		//注册添加按钮操作事件
		registerConsumerItemButtonAddItem();
	}

	/**
	 * 注册添加按钮操作事件
	 */
	private void registerConsumerItemButtonAddItem() {
		viewBinding.UIConsumerItemButtonAddItem.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddConsumptionItemActivity.class);
			intent.putExtra("consumptionType", consumptionType.name());
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
	protected void onStart() {
		super.onStart();
		refreshConsumptionItem(ConsumptionTypeEnum.SPENDING);
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
		ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		List<ConsumptionItem> newConsumptionItemList = consumptionItemDao.selectByType(consumptionTypeEnum.name());
		consumptionItemList.clear();
		consumptionItemList.addAll(newConsumptionItemList);
		editConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
	}
}