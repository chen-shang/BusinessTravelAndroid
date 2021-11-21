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
import com.business.travel.app.dal.dao.ItemSortDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.dal.entity.ItemSort;
import com.business.travel.app.databinding.ActivityEditConsumptionItemBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.SplitUtil;

/**
 * @author chenshang
 */
public class EditConsumptionItemActivity extends BaseActivity<ActivityEditConsumptionItemBinding> {
	List<ConsumptionItem> consumptionItemList = new ArrayList<>();
	private EditConsumptionItemRecyclerViewAdapter editConsumptionItemRecyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editConsumptionItemRecyclerViewAdapter = new EditConsumptionItemRecyclerViewAdapter(consumptionItemList, this);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionItemRecyclerViewAdapter);

		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(consumptionItemList, editConsumptionItemRecyclerViewAdapter));

		//支出按钮的背景
		GradientDrawable gradientDrawableExpense = (GradientDrawable)viewBinding.UIConsumerItemTextViewExpense.getBackground();
		//收入按钮的背景
		GradientDrawable gradientDrawableIncome = (GradientDrawable)viewBinding.UIConsumerItemTextViewIncome.getBackground();
		viewBinding.UIConsumerItemTextViewExpense.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.teal_800));

			refreshConsumptionItem(ConsumptionTypeEnum.SPENDING);
		});

		viewBinding.UIConsumerItemTextViewIncome.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.teal_800));

			refreshConsumptionItem(ConsumptionTypeEnum.INCOME);
		});

		//返回按钮点击后
		viewBinding.UIEditConsumptionActivityImageButtonBack.setOnClickListener(v -> this.finish());

		viewBinding.button4.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddConsumptionItemActivity.class);
			startActivity(intent);
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshConsumptionItem(ConsumptionTypeEnum.SPENDING);
	}

	private void refreshConsumptionItem(ConsumptionTypeEnum consumptionTypeEnum) {
		ItemSortDao itemSortDao = AppDatabase.getInstance(this).itemSortDao();
		ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		//先获取排序
		ItemSort itemSort = itemSortDao.selectOneByType(consumptionTypeEnum.name());
		List<ConsumptionItem> newConsumptionItemList = new ArrayList<>();
		if (itemSort == null) {
			newConsumptionItemList = consumptionItemDao.selectByType(consumptionTypeEnum.name());
		} else {
			String sortIds = itemSort.getSortIds();
			List<Long> idList = SplitUtil.trimToLongList(sortIds);
			newConsumptionItemList = consumptionItemDao.selectByIds(idList);
		}
		//todo
		//if (CollectionUtils.isEmpty(newConsumptionItemList)) {
		//	return;
		//}
		consumptionItemList.clear();
		consumptionItemList.addAll(newConsumptionItemList);
		mock();
		editConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
		LogToast.infoShow("消费类型" + consumptionTypeEnum.getMsg());
	}

	private void mock() {
		for (int i = 0; i < 10; i++) {
			ConsumptionItem consumptionItem = new ConsumptionItem();
			consumptionItem.setId(0L);
			consumptionItem.setName(i + "");
			consumptionItem.setIconPath("");
			consumptionItem.setIconName("");
			consumptionItem.setConsumptionType(ConsumptionTypeEnum.INCOME.name());
			consumptionItem.setCreateTime("");
			consumptionItem.setModifyTime("");
			consumptionItemList.add(consumptionItem);
		}
	}
}