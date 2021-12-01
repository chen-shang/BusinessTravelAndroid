package com.business.travel.app.ui.activity.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.business.travel.app.R;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.item.consumption.EditConsumptionActivity;
import com.business.travel.app.ui.activity.item.member.EditMemberActivity;
import com.business.travel.app.ui.activity.master.fragment.BillFragment;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.google.common.base.Preconditions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

	/**
	 * 消费项图标信息
	 */
	private final List<ImageIconInfo> consumptionImageIconList = new ArrayList<>();
	/**
	 * 人员图标信息
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();

	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;
	/**
	 * 选中的日期
	 */
	@Setter
	private String selectedDate = DateTimeUtil.format(new Date(), "yyyy-MM-dd");

	private BillService billService;
	private ProjectService projectService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	@Override
	protected void inject() {
		memberService = new MemberService(this);
		billService = new BillService(this);
		projectService = new ProjectService(this);
		consumptionService = new ConsumptionService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//注册消费项列表分页、点击事件
		registerConsumptionPageView();
		//注册人员列表分页、点击事件
		registerMemberPageView();
		//注册键盘点击事件
		registerKeyboard();
		//注册支出/收入按钮点击事件
		registerConsumptionType();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//每次进来该页面的时候都需要刷新一下数据
		refreshData();
	}

	private void registerConsumptionPageView() {
		registerPageViewCommonProperty(viewBinding.UIAddBillActivityGridViewPagerBillIcon)
				// 设置数据总数量
				.setDataAllCount(consumptionImageIconList.size())
				// 设置每页行数 // 设置每页列数
				.setRowCount(3).setColumnCount(5)
				// 设置是否显示指示器
				.setPointIsShow(true)
				// 设置背景图片(此时设置的背景色无效，以背景图片为主)
				.setBackgroundImageLoader(bgImageView -> {})
				// 数据绑定
				.setImageTextLoaderInterface((imageView, textView, position) -> {
					// 自己进行数据的绑定，灵活度更高，不受任何限制
					bind(imageView, textView, consumptionImageIconList.get(position), EditConsumptionActivity.class);
				});
	}

	private void registerMemberPageView() {
		registerPageViewCommonProperty(viewBinding.UIAddBillActivityGridViewPagerMemberIcon)
				// 设置数据总数量
				.setDataAllCount(memberIconList.size())
				// 设置每页行数 // 设置每页列数
				.setRowCount(2).setColumnCount(5)
				// 数据绑定
				.setImageTextLoaderInterface((imageView, textView, position) -> {
					// 自己进行数据的绑定，灵活度更高，不受任何限制
					bind(imageView, textView, memberIconList.get(position), EditMemberActivity.class);
				});
	}

	private void bind(ImageView imageView, TextView textView, ImageIconInfo imageIconInfo, Class<?> cls) {
		boolean isEditImageButton = ItemIconEnum.ItemIconEdit.getIconDownloadUrl().equals(imageIconInfo.getIconDownloadUrl());
		//默认未选中状态
		imageView.setBackgroundResource(imageIconInfo.isSelected() ? R.drawable.corners_shape_select : R.drawable.corners_shape_unselect);
		imageView.setImageResource(R.drawable.ic_base_placeholder);
		ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView);

		textView.setText(imageIconInfo.getName());

		//int unSelectColor = ContextCompat.getColor(getApplicationContext(), R.color.black_100);
		//int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.teal_800);

		imageView.setOnClickListener(v -> {
			//如果是编辑按钮
			if (isEditImageButton) {
				Intent intent = new Intent(this, cls);
				intent.putExtra("consumptionType", consumptionType.name());
				this.startActivity(intent);
				return;
			}

			if (imageIconInfo.isSelected()) {
				imageIconInfo.setSelected(false);
				//否则,改变选中颜色
				v.setBackgroundResource(R.drawable.corners_shape_unselect);
				//textView.setTextColor(unSelectColor);
				return;
			}

			if (!imageIconInfo.isSelected()) {
				imageIconInfo.setSelected(true);
				//否则,改变选中颜色
				v.setBackgroundResource(R.drawable.corners_shape_select);
				//textView.setTextColor(selectColor);
				return;
			}
		});
	}

	/**
	 * 初始化PageView公共属性
	 */
	private GridViewPager registerPageViewCommonProperty(GridViewPager gridViewPager) {
		return gridViewPager
				// 设置背景色，默认白色
				.setGridViewPagerBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white))
				// 设置item的纵向间距 // 设置上边距 // 设置下边距
				.setVerticalSpacing(10).setPagerMarginTop(10).setPagerMarginBottom(10)
				// 设置图片宽度 // 设置图片高度
				.setImageWidth(35).setImageHeight(35)
				// 设置文字与图片的间距
				.setTextImgMargin(5)
				// 设置文字大小
				.setTextSize(15)
				// 设置无限循环
				.setPageLoop(false)
				// 设置指示器与page的间距 // 设置指示器与底部的间距
				.setPointMarginPage(5).setPointMarginBottom(5)
				// 设置指示器的item宽度 // 设置指示器的item高度
				.setPointChildWidth(5).setPointChildHeight(5)
				// 设置指示器的item的间距
				.setPointChildMargin(5)
				// 指示器的item是否为圆形，默认圆形直径取宽高的最小值
				.setPointIsCircle(true)
				// 设置文字颜色
				.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black_100))
				// 指示器item未选中的颜色
				.setPointNormalColor(ContextCompat.getColor(getBaseContext(), R.color.black_100))
				// 指示器item选中的颜色
				.setPointSelectColor(ContextCompat.getColor(getBaseContext(), R.color.teal_800))
				// 设置背景图片(此时设置的背景色无效，以背景图片为主)
				.setBackgroundImageLoader(bgImageView -> {})
				// Item点击
				.setGridItemClickListener(position -> {

				})
				// 设置Item长按
				.setGridItemLongClickListener(position -> {

				});
	}

	private void registerConsumptionType() {
		viewBinding.UIAddBillActivityTextViewPayType.setOnClickListener(v -> {
			consumptionType = changeConsumptionType(consumptionType);
			viewBinding.UIAddBillActivityTextViewPayType.setText(consumptionType.getMsg());
			refreshConsumptionIcon(consumptionType);
		});
	}

	private ConsumptionTypeEnum refreshConsumptionType() {
		String payType = viewBinding.UIAddBillActivityTextViewPayType.getText().toString();
		if (ConsumptionTypeEnum.INCOME.getMsg().equals(payType)) {
			return ConsumptionTypeEnum.INCOME;
		} else if (ConsumptionTypeEnum.SPENDING.getMsg().equals(payType)) {
			return ConsumptionTypeEnum.SPENDING;
		} else {
			throw new IllegalArgumentException("未知的消费项类型标识:" + payType);
		}
	}

	private ConsumptionTypeEnum changeConsumptionType(ConsumptionTypeEnum consumptionType) {
		if (ConsumptionTypeEnum.SPENDING == consumptionType) {
			return ConsumptionTypeEnum.INCOME;
		} else if (ConsumptionTypeEnum.INCOME == consumptionType) {
			return ConsumptionTypeEnum.SPENDING;
		} else {
			throw new IllegalArgumentException("未知的消费项类型标识");
		}
	}

	private void registerKeyboard() {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setLayoutManager(gridLayoutManager);
		KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter(new ArrayList<>(), this).onSaveClick(v -> {
			try {
				//当键盘保存按钮点击之后
				saveBill();
				//6.账单创建完成后跳转到 DashboardFragment
				this.finish();
			} catch (Exception e) {
				LogToast.errorShow(e.getMessage());
			}
		}).onReRecordClick(v -> {
			try {
				//当键盘保存按钮点击之后
				saveBill();
				//初始化数据
				this.clear();
			} catch (Exception e) {
				LogToast.errorShow(e.getMessage());
			}
		});
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);
	}

	private void clear() {
		consumptionImageIconList.forEach(item -> item.setSelected(false));
		memberIconList.forEach(item -> item.setSelected(false));
		viewBinding.UIAddBillActivityGridViewPagerBillIcon.setDataAllCount(consumptionImageIconList.size()).show();
		viewBinding.UIAddBillActivityGridViewPagerMemberIcon.setDataAllCount(memberIconList.size()).show();
		viewBinding.UIAddBillActivityTextViewAmount.setText(null);
		viewBinding.UIAddBillActivityEditTextRemark.setText(null);
	}

	public void saveBill() {
		AppDatabase.getInstance(this).runInTransaction(() -> {
			//参数校验
			String projectName = viewBinding.UIAddBillActivityTextViewProjectName.getText().toString();
			//如果不存在则新增项目
			Project project = projectService.createIfNotExist(projectName);
			if (project == null) {
				throw new IllegalArgumentException("请输入合法的项目名称");
			}
			//如果存在则在当前项目下创建账单
			createBillWithProject(project);
			//更新返回页的数据
			BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
			billFragment.setSelectedProjectId(project.getId());
			LogToast.infoShow("记账成功");
		});
	}

	private void createBillWithProject(Project project) {
		//3. 日期、备注、金额
		String amount = viewBinding.UIAddBillActivityTextViewAmount.getText().toString().trim();
		Preconditions.checkArgument(StringUtils.isNotBlank(amount), "请输入消费金额");
		//1. 选中的消费项 id 的列表
		String consumerItemList = consumptionImageIconList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getId)
				.map(String::valueOf)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(","));
		Preconditions.checkArgument(StringUtils.isNotBlank(consumerItemList), "请选择消费项");
		//2. 选中的人员 的列表
		String memberItemList = memberIconList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getId)
				.map(String::valueOf)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(","));
		Preconditions.checkArgument(StringUtils.isNotBlank(memberItemList), "请选择消费人员");

		String remark = viewBinding.UIAddBillActivityEditTextRemark.getText().toString().trim();
		Bill bill = new Bill();
		bill.setConsumptionIds(consumerItemList);
		bill.setProjectId(project.getId());
		//消费金额
		bill.setAmount(new BigDecimal(amount).multiply(new BigDecimal(100)).longValue());
		//消费日期
		bill.setConsumeDate(selectedDate);
		bill.setMemberIds(memberItemList);
		bill.setCreateTime(DateTimeUtil.format(new Date()));
		bill.setModifyTime(DateTimeUtil.format(new Date()));
		bill.setConsumptionType(consumptionType.name());
		bill.setRemark(remark);
		String iconDownloadUrl = consumptionImageIconList.stream().filter(ImageIconInfo::isSelected).findFirst().map(ImageIconInfo::getIconDownloadUrl).orElse("");
		bill.setIconDownloadUrl(iconDownloadUrl);
		billService.creatBill(bill);
	}

	private void refreshData() {
		//启动的时候刷新当前页面的标题
		refreshProjectName();
		//当前是支出还是收入
		consumptionType = refreshConsumptionType();
		//刷新消费项列表
		refreshConsumptionIcon(consumptionType);
		//刷新人员列表
		refreshMemberIcon();
	}

	private void refreshProjectName() {
		Long selectedProjectId = ((BillFragment)MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment()).getSelectedProjectId();
		if (selectedProjectId == null) {
			return;
		}
		Project project = projectService.queryById(selectedProjectId);
		if (project == null) {
			return;
		}
		viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
	}

	public void refreshConsumptionIcon(ConsumptionTypeEnum consumptionType) {
		List<ImageIconInfo> imageIconInfos = consumptionService.queryAllConsumptionIconInfo(consumptionType);

		//添加编辑按钮编辑按钮永远在最后
		ImageIconInfo imageIconInfo = new ImageIconInfo();
		imageIconInfo.setName(ItemIconEnum.ItemIconEdit.getName());
		imageIconInfo.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		imageIconInfo.setSelected(false);
		imageIconInfos.add(imageIconInfo);

		consumptionImageIconList.clear();
		consumptionImageIconList.addAll(imageIconInfos);
		viewBinding.UIAddBillActivityGridViewPagerBillIcon.setDataAllCount(consumptionImageIconList.size()).show();
	}

	private void refreshMemberIcon() {
		List<ImageIconInfo> newLeastMemberIconList = memberService.queryAllMembersIconInfo();
		memberIconList.clear();
		memberIconList.addAll(newLeastMemberIconList);

		//最后在添加一个编辑按钮
		ImageIconInfo editImageIcon = new ImageIconInfo();
		editImageIcon.setName(ItemIconEnum.ItemIconEdit.getName());
		editImageIcon.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		memberIconList.add(editImageIcon);
		viewBinding.UIAddBillActivityGridViewPagerMemberIcon.setDataAllCount(memberIconList.size()).show();
	}
}