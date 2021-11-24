package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
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
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

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
		ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		editConsumptionItemRecyclerViewAdapter = new EditConsumptionItemRecyclerViewAdapter(consumptionItemList, this);

		//设置布局
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);

		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(consumptionItemList, editConsumptionItemRecyclerViewAdapter)
						//当移动之后
						.onItemMove((consumptionItems, fromPosition, toPosition) -> {
							for (int i = fromPosition; i < toPosition; i++) {
								//更新排序
								ConsumptionItem consumptionItem = consumptionItemList.get(i);
								consumptionItem.setSortId((long)i);
								consumptionItemDao.update(consumptionItem);
							}
						})
		);

		//添加分隔线
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.addItemDecoration(new DefaultItemDecoration(Color.GRAY));
		//添加删除按钮
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(this)
					.setImage(R.drawable.icon_error)
					.setHeight(LayoutParams.WRAP_CONTENT)//设置高，这里使用match_parent，就是与item的高相同
					.setWidth(LayoutParams.WRAP_CONTENT);//设置宽
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
			ConsumptionItem consumptionItem = consumptionItemList.get(adapterPosition);

			//先删除该元素
			consumptionItem.setIsDeleted(DeleteEnum.DELETE.getCode());
			consumptionItemDao.softDelete(consumptionItem);

			//移除元素
			consumptionItemList.remove(adapterPosition);
			editConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
			//然后改元素后面的排序需要更新
			for (int i = adapterPosition; i < consumptionItemList.size(); i++) {
				consumptionItem = consumptionItemList.get(i);
				consumptionItem.setSortId((long)i);
				consumptionItemDao.update(consumptionItem);
			}
		});

		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionItemRecyclerViewAdapter);
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