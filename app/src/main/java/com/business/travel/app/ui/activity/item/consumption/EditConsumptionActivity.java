package com.business.travel.app.ui.activity.item.consumption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.databinding.ActivityEditConsumptionBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.converter.ConsumptionConverter;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.ui.activity.item.AddItemActivity;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.ImageIconUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.app.view.EmptyHeaderView;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import org.apache.commons.lang3.StringUtils;

import static com.yanzhenjie.recyclerview.SwipeRecyclerView.RIGHT_DIRECTION;

/**
 * @author chenshang
 */
public class EditConsumptionActivity extends ColorStatusBarActivity<ActivityEditConsumptionBinding> {
	/**
	 * 消费项图标信息列表
	 */
	private final List<ImageIconInfo> imageIconInfoList = new ArrayList<>();
	/**
	 * 消费项图标列表适配器
	 */
	private EditItemRecyclerViewAdapter editItemRecyclerViewAdapter;
	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private EmptyHeaderView emptyHeaderView;
	//注入service
	private ConsumptionService consumptionService;

	@Override
	protected void inject() {
		consumptionService = new ConsumptionService(this);
		emptyHeaderView = new EmptyHeaderView(getLayoutInflater());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//获取当前是什么类型
		registerConsumptionType();
		//注册下拉列表事件
		registerSwipeRecyclerView();
		//注册收入支出按钮事件
		registerButton();
		//注册添加按钮操作事件
		registerConsumptionButtonAddItem();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Try.of(() -> refreshConsumptionItem(consumptionType));
	}

	private void registerConsumptionType() {
		String consumptionType = getIntent().getStringExtra("consumptionType");
		if (StringUtils.isBlank(consumptionType)) {
			return;
		}
		this.consumptionType = ConsumptionTypeEnum.valueOf(consumptionType);
	}

	private void registerButton() {

		if (ConsumptionTypeEnum.INCOME == consumptionType) {
			highlightIncome();
		} else {
			highlightSpending();
		}

		viewBinding.UIConsumerItemTextViewExpense.setOnClickListener(v -> {
			highlightSpending();
			this.consumptionType = ConsumptionTypeEnum.SPENDING;
			refreshConsumptionItem(ConsumptionTypeEnum.SPENDING);
		});

		viewBinding.UIConsumerItemTextViewIncome.setOnClickListener(v -> {
			highlightIncome();
			this.consumptionType = ConsumptionTypeEnum.INCOME;
			refreshConsumptionItem(ConsumptionTypeEnum.INCOME);
		});
	}

	/**
	 * 高亮收入
	 */
	private void highlightIncome() {
		GradientDrawable gradientDrawableExpense = (GradientDrawable)viewBinding.UIConsumerItemTextViewExpense.getBackground();
		GradientDrawable gradientDrawableIncome = (GradientDrawable)viewBinding.UIConsumerItemTextViewIncome.getBackground();
		viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.red_2));
		gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.white));

		viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.white));
		gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.red_2));
	}

	/**
	 * 高亮支出
	 */
	private void highlightSpending() {
		GradientDrawable gradientDrawableExpense = (GradientDrawable)viewBinding.UIConsumerItemTextViewExpense.getBackground();
		GradientDrawable gradientDrawableIncome = (GradientDrawable)viewBinding.UIConsumerItemTextViewIncome.getBackground();
		viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.red_2));
		gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.white));

		viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.white));
		gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.red_2));
	}

	private void registerSwipeRecyclerView() {
		editItemRecyclerViewAdapter = new EditItemRecyclerViewAdapter(imageIconInfoList, this);

		//设置布局
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);

		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		//当移动之后
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(imageIconInfoList, editItemRecyclerViewAdapter).onItemMove((itemList, fromPosition, toPosition) -> {
			IntStream.rangeClosed(0, toPosition).forEachOrdered(i -> consumptionService.updateMemberSort(itemList.get(i).getId(), (long)i));
		}));

		//添加分隔线
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.addItemDecoration(new DefaultItemDecoration(ColorUtils.getColor(R.color.black_300)));
		//添加删除按钮
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(this).setBackgroundColor(ColorUtils.getColor(R.color.red_0)).setImage(R.drawable.ic_base_delete_white).setHeight(LayoutParams.MATCH_PARENT).setWidth(150);
			rightMenu.addMenuItem(deleteItem);
		});
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
			// 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
			menuBridge.closeMenu();
			// 左侧还是右侧菜单：0左1右
			int direction = menuBridge.getDirection();
			// 菜单在Item中的Position：
			int menuPosition = menuBridge.getPosition();
			//被删除的item
			ImageIconInfo imageIconInfo = imageIconInfoList.get(adapterPosition);
			if (direction == RIGHT_DIRECTION && menuPosition == 0) {
				//移除元素
				imageIconInfoList.remove(adapterPosition);
				editItemRecyclerViewAdapter.notifyItemRemoved(adapterPosition);
				editItemRecyclerViewAdapter.notifyItemRangeChanged(adapterPosition, imageIconInfoList.size() - adapterPosition);

				//先删除该元素
				consumptionService.softDeleteConsumption(imageIconInfo.getId());
				if (CollectionUtils.isEmpty(imageIconInfoList)) {
					showEmptyHeader();
				}
			}
		});
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(editItemRecyclerViewAdapter);
	}

	/**
	 * 注册添加按钮操作事件
	 */
	private void registerConsumptionButtonAddItem() {
		viewBinding.DragFloatActionButton.setOnClickListener(v -> {
			LogUtils.i("跳转到添加图标页面 itemType:" + ItemTypeEnum.CONSUMPTION.name() + ",consumptionType:" + consumptionType.name());
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("itemType", ItemTypeEnum.CONSUMPTION.name());
			intent.putExtra("consumptionType", consumptionType.name());
			startActivity(intent);
		});
	}

	private void refreshConsumptionItem(ConsumptionTypeEnum consumptionTypeEnum) {
		if (consumptionTypeEnum == null) {
			return;
		}

		List<Consumption> consumptionList = consumptionService.queryConsumptionItemByType(consumptionTypeEnum);
		if (CollectionUtils.isEmpty(consumptionList)) {
			showEmptyHeader();
			return;
		}

		emptyHeaderView.removeFrom(viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem);

		List<ImageIconInfo> newImage = consumptionList.stream().map(consumptionItem -> {
			ImageIconInfo imageIconInfo = ConsumptionConverter.INSTANCE.convertImageIconInfo(consumptionItem);
			imageIconInfo.setItemType(ItemTypeEnum.CONSUMPTION.name());
			return imageIconInfo;
		}).collect(Collectors.toList());

		//先尝试比较一下list 是否改变
		if (!ImageIconUtil.dataChange(newImage, imageIconInfoList)) {
			return;
		}

		this.imageIconInfoList.clear();
		this.imageIconInfoList.addAll(newImage);
		editItemRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void showEmptyHeader() {
		imageIconInfoList.clear();
		editItemRecyclerViewAdapter.notifyItemRangeChanged(0, imageIconInfoList.size());
		emptyHeaderView.addTo(viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem);
	}
}