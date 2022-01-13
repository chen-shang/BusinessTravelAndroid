package com.business.travel.app.ui.activity.bill;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.BillAddModel;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.bill.fragment.BillFragment;
import com.business.travel.app.ui.activity.item.consumption.EditConsumptionActivity;
import com.business.travel.app.ui.activity.item.member.EditMemberActivity;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.google.common.base.Preconditions;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.impl.AttachListPopupView;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends ColorStatusBarActivity<ActivityAddBillBinding> {

	/**
	 * 消费项图标信息
	 */
	private final List<ImageIconInfo> consumptionImageIconList = new ArrayList<>();
	/**
	 * 人员图标信息
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	//各种service
	private BillService billService;
	private ProjectService projectService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	/**
	 * 新建的时候可以指定日期、类型
	 */
	@NotNull
	private BillAddModel billAddModel;

	@Override
	protected void inject() {
		memberService = new MemberService(this);
		billService = new BillService(this);
		projectService = new ProjectService(this);
		consumptionService = new ConsumptionService(this);

		String billAdd = this.getIntent().getStringExtra(AddBillActivity.IntentKey.billAddModel);
		if (StringUtils.isNotBlank(billAdd)) {
			billAddModel = JacksonUtil.toBean(billAdd, BillAddModel.class);
		} else {
			//默认值
			billAddModel = new BillAddModel("", DateTimeUtil.timestamp(LocalDate.now()), ConsumptionTypeEnum.SPENDING.name());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//注册消费项列表分页、点击事件
		registerMaterialSearchBar();
		//注册消费项列表分页、点击事件
		registerConsumptionPageView();
		//注册人员列表分页、点击事件
		registerMemberPageView();
		//注册键盘点击事件
		registerKeyboard();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//每次进来该页面的时候都需要刷新一下数据
		Try.of(() -> refreshBillAdd(billAddModel));
	}

	private void registerMaterialSearchBar() {
		Builder builder = new Builder(this)
				// 依附于所点击的View，内部会自动判断在上方或者下方显示
				.atView(viewBinding.topTitleBar)
				//最大高度
				.maxHeight(ScreenUtils.getScreenHeight() / 3)
				//不变暗
				.hasShadowBg(false)
				// 宽度
				.popupWidth(ScreenUtils.getScreenWidth())
				//动画
				.popupAnimation(PopupAnimation.ScrollAlphaFromTop);

		EditText contentBarTitle = viewBinding.topTitleBar.contentBarTitle;
		contentBarTitle.setFocusable(true);
		contentBarTitle.setFocusableInTouchMode(true);
		contentBarTitle.setTextColor(ColorUtils.getColor(R.color.white));

		AttachListPopupView attachListPopupView = builder.asAttachList(projectService.queryAllProjectName(), null, (position, text) -> {
			contentBarTitle.setText(text);
		}, R.layout._xpopup_attach_impl_list, R.layout.base_list_item);

		viewBinding.topTitleBar.setOnClickListener(v -> attachListPopupView.show());
		viewBinding.topTitleBar.contentBarLeftIcon.setOnClickListener(v -> attachListPopupView.show());
	}

	private void registerConsumptionPageView() {
		GridViewPagerUtil.registerPageViewCommonProperty(viewBinding.GridViewPagerConsumptionIconList)
		                 // 设置数据总数量
		                 .setDataAllCount(consumptionImageIconList.size())
		                 // 设置每页行数 // 设置每页列数
		                 .setRowCount(4).setColumnCount(5)
		                 // 设置是否显示指示器
		                 .setPointIsShow(true)
		                 // 设置背景图片(此时设置的背景色无效，以背景图片为主)
		                 .setBackgroundImageLoader(bgImageView -> {
		                 })
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 ImageIconInfo imageIconInfo = consumptionImageIconList.get(position);
			                 //是否是编辑图标
			                 boolean isEditButton = ItemIconEnum.ItemIconEdit.getIconDownloadUrl().equals(imageIconInfo.getIconDownloadUrl());
			                 //如果不是编辑图标,有通用的行为
			                 if (!isEditButton) {
				                 imageIconInfo.bind(imageView, textView);
				                 return;
			                 }
			                 //如果是编辑按钮,编辑图标有单独的行为
			                 imageIconInfo.refresh(imageView, textView);
			                 imageView.setOnClickListener(v -> {
				                 Intent intent = new Intent(this, EditConsumptionActivity.class);
				                 intent.putExtra(EditConsumptionActivity.IntentKey.CONSUMPTION_TYPE, viewBinding.keyboard.getConsumptionType().name());
				                 this.startActivity(intent);
			                 });
		                 });
	}

	private void registerMemberPageView() {
		GridViewPagerUtil.registerPageViewCommonProperty(viewBinding.GridViewPagerMemberIconList)
		                 // 设置数据总数量
		                 .setDataAllCount(memberIconList.size())
		                 // 设置每页行数 // 设置每页列数
		                 .setRowCount(2).setColumnCount(5)
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 ImageIconInfo imageIconInfo = memberIconList.get(position);
			                 //是否是编辑图标
			                 boolean isEditButton = ItemIconEnum.ItemIconEdit.getIconDownloadUrl().equals(imageIconInfo.getIconDownloadUrl());
			                 //如果不是编辑图标,有通用的行为
			                 if (!isEditButton) {
				                 imageIconInfo.bind(imageView, textView);
				                 return;
			                 }
			                 //如果是编辑按钮,编辑图标有单独的行为
			                 imageIconInfo.refresh(imageView, textView);
			                 imageView.setOnClickListener(v -> {
				                 Intent intent = new Intent(this, EditMemberActivity.class);
				                 this.startActivity(intent);
			                 });
		                 });
	}

	private void registerKeyboard() {
		viewBinding.keyboard.onSwitchClick(v -> {
			//注册支出/收入按钮点击事件
			refreshConsumptionIcon(viewBinding.keyboard.getConsumptionType());
		}).onSaveClick(v -> Try.of(() -> {
			//当键盘保存按钮点击之后
			saveBill();
			//6.账单创建完成后跳转到 DashboardFragment
			this.finish();
		})).onSaveLongClick(v -> Try.of(() -> {
			saveBill();
			clear();
		}));
	}

	public void saveBill() {
		AppDatabase.getInstance(this).runInTransaction(() -> {
			LogUtils.i("开启数据库事务");
			//参数校验
			String projectName = viewBinding.topTitleBar.contentBarTitle.getText().toString();
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

	private void clear() {
		consumptionImageIconList.forEach(item -> item.setSelected(false));
		memberIconList.forEach(item -> item.setSelected(false));
		viewBinding.GridViewPagerConsumptionIconList.setDataAllCount(consumptionImageIconList.size()).show();
		viewBinding.GridViewPagerMemberIconList.setDataAllCount(memberIconList.size()).show();
		viewBinding.keyboard.setAmount(null);
		viewBinding.keyboard.setRemark(null);
	}

	private void createBillWithProject(Project project) {
		//3. 日期、备注、金额
		String amount = viewBinding.keyboard.getAmount().trim();
		Preconditions.checkArgument(StringUtils.isNotBlank(amount), "请输入消费金额");
		//1. 选中的消费项 id 的列表
		String consumerItemList = consumptionImageIconList.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
		Preconditions.checkArgument(StringUtils.isNotBlank(consumerItemList), "请选择消费项");
		//2. 选中的人员 的列表
		String memberItemList = memberIconList.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
		Preconditions.checkArgument(StringUtils.isNotBlank(memberItemList), "请选择消费人员");

		String remark = viewBinding.keyboard.getRemark().trim();
		Bill bill = new Bill();
		bill.setConsumptionIds(consumerItemList);
		bill.setProjectId(project.getId());
		//消费金额
		bill.setAmount(MoneyUtil.toFen(amount));
		//消费日期
		Long selectedDate = Optional.ofNullable(viewBinding.keyboard.getDate()).orElse(DateTimeUtil.timestamp(DateTimeUtil.now().toLocalDate()));
		bill.setConsumeDate(selectedDate);

		bill.setMemberIds(memberItemList);
		bill.setCreateTime(DateTimeUtil.timestamp());
		bill.setModifyTime(DateTimeUtil.timestamp());
		bill.setConsumptionType(viewBinding.keyboard.getConsumptionType().name());
		bill.setRemark(remark);
		String iconDownloadUrl = consumptionImageIconList.stream().filter(ImageIconInfo::isSelected).findFirst().map(ImageIconInfo::getIconDownloadUrl).orElse("");
		bill.setIconDownloadUrl(iconDownloadUrl);
		billService.creatBill(bill);
	}

	private void refreshBillAdd(BillAddModel billAddModel) {
		//启动的时候刷新当前页面的标题
		Optional.ofNullable(billAddModel).map(BillAddModel::getProjectName).ifPresent(name -> viewBinding.topTitleBar.contentBarTitle.setText(name));
		Optional.ofNullable(billAddModel).map(BillAddModel::getConsumeDate).ifPresent(viewBinding.keyboard::setDate);
		Optional.ofNullable(billAddModel).map(BillAddModel::getConsumptionType).map(ConsumptionTypeEnum::valueOf).ifPresent(viewBinding.keyboard::setConsumptionType);
		//刷新消费项列表
		String consumptionType = billAddModel.getConsumptionType();
		refreshConsumptionIcon(ConsumptionTypeEnum.valueOf(consumptionType));
		//刷新人员列表
		refreshMemberIcon();
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
		viewBinding.GridViewPagerMemberIconList.setDataAllCount(memberIconList.size()).setRowCount(memberIconList.size() > 5 ? 2 : 1).show();
	}

	private void refreshConsumptionIcon(ConsumptionTypeEnum consumptionType) {
		List<ImageIconInfo> imageIconInfos = consumptionService.queryAllConsumptionIconInfo(consumptionType);
		//添加编辑按钮编辑按钮永远在最后
		ImageIconInfo imageIconInfo = new ImageIconInfo();
		imageIconInfo.setName(ItemIconEnum.ItemIconEdit.getName());
		imageIconInfo.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		imageIconInfo.setSelected(false);
		imageIconInfos.add(imageIconInfo);

		consumptionImageIconList.clear();
		consumptionImageIconList.addAll(imageIconInfos);
		viewBinding.GridViewPagerConsumptionIconList.setDataAllCount(consumptionImageIconList.size()).show();
	}

	public static final class IntentKey {
		public static final String billAddModel = "billAddModel";
	}
}