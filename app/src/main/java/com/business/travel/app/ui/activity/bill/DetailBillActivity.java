package com.business.travel.app.ui.activity.bill;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.app.view.BottomIconListPopupView;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.business.travel.vo.enums.WeekEnum;
import com.google.common.base.Preconditions;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.impl.BottomListPopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import org.apache.commons.lang3.StringUtils;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * 账单详情页面
 */
public class DetailBillActivity extends ColorStatusBarActivity<ActivityDetailBillBinding> {

	/**
	 * 默认展示6列
	 */
	private static final int COLUMN_COUNT = 6;
	/**
	 * 成员图标
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	/**
	 * 消费项图标列表
	 */
	private final List<ImageIconInfo> consumptionIconList = new ArrayList<>();
	/**
	 * 当前被选中的账单信息
	 */
	private Long selectBillId;
	private ConsumptionTypeEnum consumptionTypeEnum;

	//注入service
	private BillService billService;
	private ProjectService projectService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	/**
	 * 日历选框
	 */
	private DatePickerDialog datePickerDialog;

	@Override
	protected void inject() {
		billService = new BillService(this);
		memberService = new MemberService(this);
		consumptionService = new ConsumptionService(this);
		projectService = new ProjectService(this);
		selectBillId = getIntent().getLongExtra("selectBillId", -1);

		LocalDateTime now = DateTimeUtil.now();
		datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {}, now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//注册消费项列表
		registerPageView(viewBinding.GridViewPagerConsumptionIconList, consumptionIconList, ItemTypeEnum.CONSUMPTION);
		//注册人员列表
		registerPageView(viewBinding.GridViewPagerMemberIconList, memberIconList, ItemTypeEnum.MEMBER);

		//注册更新消费时间事件
		registerUpdateConsumeDate(viewBinding.time);

		//注册更新备注事件
		registerUpdateRemark(viewBinding.remark);

		//注册更新消费金额事件
		registerUpdateAmount(viewBinding.amount);

		//注册更新项目归属事件
		registerUpdateProjectName();

		//注册删除账单事件
		registerDeleteBill();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Try.of(this::refreshData);
	}

	private void refreshData() {
		//参数检查
		Preconditions.checkArgument(selectBillId > 0, "请选择账单");
		Bill bill = billService.queryBillById(selectBillId);
		Preconditions.checkArgument(bill != null, "未查询到账单 " + selectBillId);
		//业务逻辑
		showBillDetail(bill);
	}

	private void registerDeleteBill() {
		ConfirmPopupView confirmPopupView = new Builder(this).asConfirm("", "是否要删除该账目", () -> {
			//1. 数据库删除
			billService.deleteBillById(selectBillId);
			//2. 关闭当前页面
			this.finish();
		});
		viewBinding.topTitleBar.contentBarRightIcon.setOnClickListener(v -> confirmPopupView.show());
	}

	private void registerUpdateProjectName() {
		Builder builder = new Builder(this).maxHeight(ScreenUtils.getScreenHeight() / 2).popupAnimation(PopupAnimation.ScrollAlphaFromTop);
		String[] data = projectService.queryAllProjectName();
		BottomListPopupView bottomListPopupView = builder.asBottomList("差旅项目", data, null, -1, (position, text) -> {
			viewBinding.projectName.setText(text);

			Bill record = new Bill();
			Project project = projectService.queryByName(text);
			record.setProjectId(project.getId());
			billService.updateBill(selectBillId, record);
		});
		viewBinding.projectName.setOnClickListener(v -> {
			int checkedPosition = getIndexOf(data, viewBinding.projectName.getText().toString());
			bottomListPopupView.setCheckedPosition(checkedPosition).show();
		});
	}

	private int getIndexOf(String[] data, String targetName) {
		for (int i = 0; i < data.length; i++) {
			if (targetName.equals(data[i])) {
				return i;
			}
		}
		return -1;
	}

	private void registerUpdateAmount(EditText editeText) {
		editeText.setTextColor(viewBinding.projectName.getCurrentTextColor());
		editeText.setOnFocusChangeListener((v, hasFocus) -> Try.of(() -> {
			//失去焦点的时候保存
			if (!hasFocus) {
				String s = ((EditText)v).getText().toString();
				Bill record = new Bill();
				record.setAmount(MoneyUtil.toFen(s));
				billService.updateBill(selectBillId, record);
			}
		}));
	}

	private void registerUpdateRemark(EditText remark) {
		remark.setTextColor(viewBinding.projectName.getCurrentTextColor());
		remark.setOnFocusChangeListener((v, hasFocus) -> Try.of(() -> {
			//失去焦点的时候保存
			if (!hasFocus) {
				String s = ((EditText)v).getText().toString();
				Bill record = new Bill();
				record.setRemark(s);
				billService.updateBill(selectBillId, record);
			}
		}));
	}

	private void registerUpdateConsumeDate(View view) {
		//日历弹框
		datePickerDialog.setOnDateSetListener((v, year, month, dayOfMonth) -> {
			LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
			//消费时间
			String date = DateTimeUtil.format(localDate, "yyyy-MM-dd");
			Bill record = new Bill();
			record.setConsumeDate(DateTimeUtil.timestamp(date, "yyyy-MM-dd"));
			billService.updateBill(selectBillId, record);
			//周几
			WeekEnum weekEnum = WeekEnum.ofCode(localDate.getDayOfWeek().getValue());
			viewBinding.time.setText(date + " " + weekEnum.getMsg());
		});

		//点击事件
		view.setOnClickListener(v -> {
			refreshDatePickerDialogDate((TextView)v);
			datePickerDialog.show();
		});
	}

	private void refreshDatePickerDialogDate(TextView v) {
		String time = v.getText().toString();
		String date = SplitUtil.trimToStringList(time, " ").get(0);
		LocalDate localDate = DateTimeUtil.parseLocalDate(date);
		datePickerDialog.updateDate(localDate.getYear(), localDate.getMonth().ordinal(), localDate.getDayOfMonth());
	}

	/**
	 * 注册图标列表
	 *
	 * @param gridViewPager
	 * @param dataList
	 */
	private void registerPageView(GridViewPager gridViewPager, List<ImageIconInfo> dataList, ItemTypeEnum itemType) {
		GridViewPagerUtil.registerPageViewCommonProperty(gridViewPager)
		                 // 设置数据总数量
		                 .setDataAllCount(dataList.size())
		                 // 设置每页行数
		                 .setRowCount(1)
		                 // 设置每页列数
		                 .setColumnCount(COLUMN_COUNT)
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 bind(imageView, textView, dataList.get(position),itemType);
		                 });
	}

	/**
	 * 展示账单详情
	 *
	 * @param bill
	 */
	@SuppressLint("SetTextI18n")
	private void showBillDetail(@NotNull Bill bill) {
		//消费项列表
		String consumptionIds = bill.getConsumptionIds();
		showConsumption(consumptionIds);

		//人员列表
		String memberIds = bill.getMemberIds();
		showMember(memberIds);

		//消费金额
		viewBinding.amount.setText(MoneyUtil.toYuanString(bill.getAmount()));

		//消费时间
		String date = DateTimeUtil.format(bill.getConsumeDate(), "yyyy-MM-dd");
		//周几
		WeekEnum weekEnum = WeekEnum.ofCode(DateTimeUtil.toLocalDateTime(bill.getConsumeDate()).getDayOfWeek().getValue());
		viewBinding.time.setText(date + " " + weekEnum.getMsg());
		//消费类型
		String consumptionType = bill.getConsumptionType();
		//全局
		consumptionTypeEnum = ConsumptionTypeEnum.valueOf(consumptionType);
		viewBinding.consumerType.setText(consumptionTypeEnum.getMsg());

		//备注
		viewBinding.remark.setText(bill.getRemark());

		//项目名称
		Long projectId = bill.getProjectId();
		String projectName = projectService.queryById(projectId).getName();
		viewBinding.projectName.setText(projectName);
	}

	/**
	 * 显示列表
	 *
	 * @param consumptionIds
	 */
	private void showConsumption(String consumptionIds) {
		if (StringUtils.isBlank(consumptionIds)) {
			LogUtils.w("该账单没有消费项 id:" + selectBillId);
			return;
		}
		List<Long> consumptionIdList = SplitUtil.trimToLongList(consumptionIds);
		if (CollectionUtils.isEmpty(consumptionIdList)) {
			return;
		}
		List<ImageIconInfo> consumptions = consumptionService.queryByIds(consumptionIdList);
		if (CollectionUtils.isEmpty(consumptions)) {
			LogUtils.w("没有查询到消费项 id:" + selectBillId + ",consumptionIdList:" + consumptionIdList);
			return;
		}

		//更新消费项目图标数据列表
		consumptionIconList.clear();
		consumptionIconList.addAll(consumptions);
		//更新数据显示
		viewBinding.GridViewPagerConsumptionIconList
				//图标总数量
				.setDataAllCount(consumptionIconList.size())
				//更新展示
				.show();
	}

	/**
	 * 显示列表
	 *
	 * @param memberIds
	 */
	private void showMember(String memberIds) {
		if (StringUtils.isBlank(memberIds)) {
			LogUtils.w("该账单没有消费成员 id:" + selectBillId);
			return;
		}
		List<Long> memberIdList = SplitUtil.trimToLongList(memberIds);
		if (CollectionUtils.isEmpty(memberIdList)) {
			return;
		}
		List<ImageIconInfo> members = memberService.queryByIds(memberIdList);
		if (CollectionUtils.isEmpty(members)) {
			LogUtils.w("没有查询到消费项 id:" + selectBillId + ",memberIdList:" + memberIdList);
			return;
		}

		//更新消费项目图标数据列表
		memberIconList.clear();
		memberIconList.addAll(members);
		viewBinding.GridViewPagerMemberIconList
				//图标总数量
				.setDataAllCount(memberIconList.size())
				//更新展示
				.show();
	}

	private void bind(ImageView imageView, TextView textView, ImageIconInfo imageIconInfo, ItemTypeEnum itemTypeEnum) {
		textView.setText(imageIconInfo.getName());

		imageView.setBackgroundResource(R.drawable.corners_shape_select);
		imageView.setImageResource(R.drawable.ic_base_placeholder);
		ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView);

		Builder builder = new Builder(this).maxHeight(ScreenUtils.getScreenHeight() * 2 / 3).popupAnimation(PopupAnimation.ScrollAlphaFromTop);
		switch (itemTypeEnum) {
			case CONSUMPTION:
				imageView.setOnClickListener(v -> {
					List<ImageIconInfo> result = genPopupConsumptionImageIcon();
					builder.asCustom(new BottomIconListPopupView(this, result).onConfirm(() -> {
						String collect = result.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).collect(joining(","));
						Bill bill = new Bill();
						bill.setConsumptionIds(collect);
						billService.updateBill(selectBillId, bill);

						//跟新图标
						refreshData();
					})).show();
				});
				break;
			case MEMBER:
				imageView.setOnClickListener(v -> {
					List<ImageIconInfo> result = genPopupMemberImageIcon();
					builder.asCustom(new BottomIconListPopupView(this, result).onConfirm(() -> {
						String collect = result.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).collect(joining(","));
						Bill bill = new Bill();
						bill.setMemberIds(collect);
						billService.updateBill(selectBillId, bill);

						refreshData();
					})).show();
				});
				break;
		}
	}

	private List<ImageIconInfo> genPopupMemberImageIcon() {
		//先查询对应的图标
		List<ImageIconInfo> imageIconInfos = memberService.queryAllMembersIconInfo();
		//处理一下是否被选中
		Map<Long, ImageIconInfo> collect = imageIconInfos.stream().collect(toMap(ImageIconInfo::getId, item -> item));

		memberIconList.forEach(iconInfo -> {
			ImageIconInfo imageIconSelect = collect.get(iconInfo.getId());
			if (imageIconSelect != null) {
				imageIconSelect.setSelected(true);
			} else {
				imageIconInfos.add(iconInfo);
			}
		});

		return imageIconInfos.stream().sorted((o1, o2) -> {
			//排序,选中的排前面
			int i1 = o1.isSelected() ? 0 : 1;
			int i2 = o2.isSelected() ? 0 : 1;
			return i1 - i2;
		}).collect(Collectors.toList());
	}

	private List<ImageIconInfo> genPopupConsumptionImageIcon() {
		//先查询对应的图标
		List<ImageIconInfo> imageIconInfos = consumptionService.queryAllConsumptionIconInfo(consumptionTypeEnum);
		//处理一下是否被选中
		Map<Long, ImageIconInfo> collect = imageIconInfos.stream().collect(toMap(ImageIconInfo::getId, item -> item));

		consumptionIconList.forEach(iconInfo -> {
			ImageIconInfo imageIconSelect = collect.get(iconInfo.getId());
			if (imageIconSelect != null) {
				imageIconSelect.setSelected(true);
			} else {
				imageIconInfos.add(iconInfo);
			}
		});

		return imageIconInfos.stream().sorted((o1, o2) -> {
			//排序,选中的排前面
			int i1 = o1.isSelected() ? 0 : 1;
			int i2 = o2.isSelected() ? 0 : 1;
			return i1 - i2;
		}).collect(Collectors.toList());
	}
}