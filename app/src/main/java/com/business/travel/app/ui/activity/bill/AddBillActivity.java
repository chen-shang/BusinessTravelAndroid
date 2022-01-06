package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
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
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
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
	private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;
	//各种service
	private BillService billService;
	private ProjectService projectService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	/**
	 * 如果是编辑
	 */
	private Long selectBillId;

	@Override
	protected void inject() {
		memberService = new MemberService(this);
		billService = new BillService(this);
		projectService = new ProjectService(this);
		consumptionService = new ConsumptionService(this);

		//其他页面传递进来的billId,代表编辑
		selectBillId = this.getIntent().getLongExtra("selectBillId", -1);
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
		//注册支出/收入按钮点击事件
		registerConsumptionType();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//每次进来该页面的时候都需要刷新一下数据
		Try.of(() -> refreshData(selectBillId));
	}

	private void registerMaterialSearchBar() {
		Builder builder = new Builder(this)
				// 依附于所点击的View，内部会自动判断在上方或者下方显示
				.atView(viewBinding.topTitleBar)
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

		viewBinding.topTitleBar.setOnClickListener(v -> {
			AttachListPopupView attachListPopupView = builder.asAttachList(projectService.queryAllProjectName(), null, (position, text) -> {
				contentBarTitle.setText(text);
			}, R.layout._xpopup_attach_impl_list, R.layout.base_list_item);
			attachListPopupView.show();
		});

		viewBinding.topTitleBar.contentBarLeftIcon.setOnClickListener(v -> {
			AttachListPopupView attachListPopupView = builder.asAttachList(projectService.queryAllProjectName(), null, (position, text) -> {
				contentBarTitle.setText(text);
			}, R.layout._xpopup_attach_impl_list, R.layout.base_list_item);
			attachListPopupView.show();
		});
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
			                 bind(imageView, textView, consumptionImageIconList.get(position), EditConsumptionActivity.class);
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
			                 bind(imageView, textView, memberIconList.get(position), EditMemberActivity.class);
		                 });
	}

	private void registerKeyboard() {
		viewBinding.keyboard.onSaveClick(v -> Try.of(() -> {
			//当键盘保存按钮点击之后
			if (selectBillId > 0) {
				updateBill();
			} else {
				saveBill();
			}
			//6.账单创建完成后跳转到 DashboardFragment
			this.finish();
		})).onSaveLongClick(v -> Try.of(() -> {
			saveBill();
			clear();
		}));
	}

	private void updateBill() {
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
			updateBillWithProject(project);
			//更新返回页的数据
			BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
			billFragment.setSelectedProjectId(project.getId());
			LogToast.infoShow("记账成功");
		});
	}

	private void updateBillWithProject(Project project) {
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
		bill.setConsumptionType(consumptionType.name());
		bill.setRemark(remark);
		String iconDownloadUrl = consumptionImageIconList.stream().filter(ImageIconInfo::isSelected).findFirst().map(ImageIconInfo::getIconDownloadUrl).orElse("");
		bill.setIconDownloadUrl(iconDownloadUrl);
		billService.updateBill(selectBillId, bill);
	}

	private void registerConsumptionType() {
		viewBinding.keyboard.textViewConsumptionType.setOnClickListener(v -> {
			consumptionType = changeConsumptionType(consumptionType);
			((TextView)v).setText(consumptionType.getMsg());
			refreshConsumptionIcon(consumptionType);
		});
	}

	private void bind(ImageView imageView, TextView textView, ImageIconInfo imageIconInfo, Class<?> cls) {
		boolean isEditImageButton = ItemIconEnum.ItemIconEdit.getIconDownloadUrl().equals(imageIconInfo.getIconDownloadUrl());
		//默认未选中状态
		imageView.setBackgroundResource(R.drawable.corners_shape_unselect);
		imageView.setImageResource(R.drawable.ic_base_placeholder);

		textView.setText(imageIconInfo.getName());

		int selectColor = ContextCompat.getColor(getApplicationContext(), R.color.red_2);
		ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView, imageIconInfo.isSelected() ? selectColor : null);

		imageView.setOnClickListener(v -> {
			//如果是编辑按钮
			if (isEditImageButton) {
				Intent intent = new Intent(this, cls);
				intent.putExtra("consumptionType", consumptionType.name());
				this.startActivity(intent);
				return;
			}
			//否则,改变选中颜色
			imageIconInfo.setSelected(!imageIconInfo.isSelected());
			ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView, imageIconInfo.isSelected() ? selectColor : null);
		});
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

	private ConsumptionTypeEnum changeConsumptionType(ConsumptionTypeEnum consumptionType) {
		if (ConsumptionTypeEnum.SPENDING == consumptionType) {
			return ConsumptionTypeEnum.INCOME;
		} else if (ConsumptionTypeEnum.INCOME == consumptionType) {
			return ConsumptionTypeEnum.SPENDING;
		} else {
			throw new IllegalArgumentException("未知的消费项类型标识");
		}
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
		viewBinding.GridViewPagerConsumptionIconList.setDataAllCount(consumptionImageIconList.size()).show();
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
		bill.setConsumptionType(consumptionType.name());
		bill.setRemark(remark);
		String iconDownloadUrl = consumptionImageIconList.stream().filter(ImageIconInfo::isSelected).findFirst().map(ImageIconInfo::getIconDownloadUrl).orElse("");
		bill.setIconDownloadUrl(iconDownloadUrl);
		billService.creatBill(bill);
	}

	private void refreshData(Long selectBillId) {
		if (selectBillId < 0) {
			// TODO: 2021/12/30
			//启动的时候刷新当前页面的标题
			refreshProjectName();
			//当前是支出还是收入
			consumptionType = refreshConsumptionType();
			//刷新消费项列表
			refreshConsumptionIcon(consumptionType);
			//刷新人员列表
			refreshMemberIcon();
		} else {
			Bill bill = billService.queryBillById(selectBillId);
			Project project = projectService.queryById(bill.getProjectId());
			Optional.of(project).map(Project::getName).ifPresent(name -> {
				viewBinding.topTitleBar.contentBarTitle.setText(name);
			});

			//键盘收入支出选择
			consumptionType = ConsumptionTypeEnum.valueOf(bill.getConsumptionType());
			viewBinding.keyboard.setConsumptionType(consumptionType.getMsg());

			//金额显示
			Long amount = bill.getAmount();

			viewBinding.keyboard.setAmount(MoneyUtil.toYuanString(amount));
			//显示消费项目
			//刷新消费项列表
			refreshConsumptionIcon2(bill, consumptionType);

			//显示消费人员
			//刷新人员列表
			refreshMemberIcon2(bill);
			//日期显示
			Long consumeDate = bill.getConsumeDate();
			viewBinding.keyboard.setDate(consumeDate);
			//备注显示
			viewBinding.keyboard.setRemark(bill.getRemark());
		}
	}

	private void refreshMemberIcon2(Bill bill) {
		List<ImageIconInfo> newLeastMemberIconList = memberService.queryAllMembersIconInfo();
		String memberIds = bill.getMemberIds();
		List<Long> memberIdList = SplitUtil.trimToLongList(memberIds);
		newLeastMemberIconList.forEach(member -> member.setSelected(memberIdList.contains(member.getId())));

		memberIconList.clear();
		memberIconList.addAll(newLeastMemberIconList);

		//最后在添加一个编辑按钮
		ImageIconInfo editImageIcon = new ImageIconInfo();
		editImageIcon.setName(ItemIconEnum.ItemIconEdit.getName());
		editImageIcon.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		memberIconList.add(editImageIcon);
		viewBinding.GridViewPagerMemberIconList.setDataAllCount(memberIconList.size()).setRowCount(memberIconList.size() > 5 ? 2 : 1).show();

	}

	private void refreshConsumptionIcon2(Bill bill, ConsumptionTypeEnum consumptionType) {
		List<ImageIconInfo> imageIconInfos = consumptionService.queryAllConsumptionIconInfo(consumptionType);

		String consumptionIds = bill.getConsumptionIds();
		List<Long> ids = SplitUtil.trimToLongList(consumptionIds);
		imageIconInfos.forEach(imageIconInfo -> imageIconInfo.setSelected(ids.contains(imageIconInfo.getId())));

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

	private void refreshProjectName() {
		Long selectedProjectId = billFragment.getSelectedProjectId();
		if (selectedProjectId == null) {
			return;
		}
		Project project = projectService.queryById(selectedProjectId);
		if (project == null) {
			return;
		}
		viewBinding.topTitleBar.contentBarTitle.setText(project.getName());
	}

	private ConsumptionTypeEnum refreshConsumptionType() {
		String payType = viewBinding.keyboard.getConsumptionType();
		if (ConsumptionTypeEnum.INCOME.getMsg().equals(payType)) {
			return ConsumptionTypeEnum.INCOME;
		} else if (ConsumptionTypeEnum.SPENDING.getMsg().equals(payType)) {
			return ConsumptionTypeEnum.SPENDING;
		} else {
			throw new IllegalArgumentException("未知的消费项类型标识:" + payType);
		}
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
}