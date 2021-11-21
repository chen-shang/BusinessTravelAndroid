package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.view.Gravity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.ItemIconInfo.ImageIconInfo;
import com.business.travel.app.ui.activity.master.fragment.BillFragment;
import com.business.travel.app.ui.activity.master.fragment.BillFragmentShareData;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

	private final List<ImageIconInfo> iconList = new ArrayList<>();
	private final List<ImageIconInfo> associateList = new ArrayList<>();

	private BillDao billDao;
	private ProjectDao projectDao;

	private ItemIconRecyclerViewAdapter billRecyclerViewAdapter;

	/**
	 * 当前被选中的是支出还是收入
	 */
	private ConsumptionTypeEnum consumptionType = ConsumptionTypeEnum.SPENDING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		billDao = AppDatabase.getInstance(this).billDao();
		projectDao = AppDatabase.getInstance(this).projectDao();
		//todo 删除
		mock();

		//消费项目列表
		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewConsumptionItem.setLayoutManager(layoutManager);
		billRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, iconList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewConsumptionItem.setAdapter(billRecyclerViewAdapter);
		//长按移动排序
		viewBinding.UIAddBillActivitySwipeRecyclerViewConsumptionItem.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewConsumptionItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(iconList, billRecyclerViewAdapter));

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		ItemIconRecyclerViewAdapter itemIconRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.ASSOCIATE, associateList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(itemIconRecyclerViewAdapter);
		//长按移动排序
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(associateList, itemIconRecyclerViewAdapter));

		GridLayoutManager layoutManager3 = new GridLayoutManager(this, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setLayoutManager(layoutManager3);
		KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter(new ArrayList<>(), this).onSaveClick(v -> {
			try {
				//当键盘保存按钮点击之后
				saveBill(billRecyclerViewAdapter, itemIconRecyclerViewAdapter);
				//6.账单创建完成后跳转到 DashboardFragment
				ActivityUtils.finishActivity(AddBillActivity.this, true);
			} catch (Exception e) {
				LogToast.errorShow(e.getMessage());
			}
		}).onDeleteClick(v -> {
			//当键盘删除键点击之后
			//控制金额文本框的金额删除

		}).onReRecordClick(v -> {
			try {
				//当键盘保存按钮点击之后
				saveBill(billRecyclerViewAdapter, itemIconRecyclerViewAdapter);
			} catch (Exception e) {
				ToastUtils.make().setLeftIcon(R.drawable.icon_error).setGravity(Gravity.CENTER, 0, 0).setDurationIsLong(true).show(e.getMessage());
			}
		});
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);

		viewBinding.UIAddBillActivityTextViewPayType.setOnClickListener(v -> {
			String payType = viewBinding.UIAddBillActivityTextViewPayType.getText().toString();
			if (ConsumptionTypeEnum.INCOME.getMsg().equals(payType)) {
				consumptionType = ConsumptionTypeEnum.SPENDING;
				viewBinding.UIAddBillActivityTextViewPayType.setText(ConsumptionTypeEnum.SPENDING.getMsg());
			} else if (ConsumptionTypeEnum.SPENDING.getMsg().equals(payType)) {
				consumptionType = ConsumptionTypeEnum.INCOME;
				viewBinding.UIAddBillActivityTextViewPayType.setText(ConsumptionTypeEnum.INCOME.getMsg());
			} else {
				//todo 异常
			}
		});
	}

	private void saveBill(ItemIconRecyclerViewAdapter billRecyclerViewAdapter, ItemIconRecyclerViewAdapter itemIconRecyclerViewAdapter) {
		//参数校验
		String projectName = viewBinding.UIAddBillActivityTextViewProjectName.getText().toString();
		if (StringUtils.isBlank(projectName)) {
			throw new IllegalArgumentException("请输入项目名称");
		}
		//根据项目名称查询是否存在该项目
		Project project = projectDao.selectByName(projectName);
		//3.如果不存在则新增项目
		project = Optional.ofNullable(project).orElseGet(() -> {
			//新增
			createProject(projectName);
			//并获取新增的项目
			return projectDao.selectByName(projectName);
		});
		//如果项目不存在并且创建失败的话抛出异常
		Optional.ofNullable(project).orElseThrow(() -> new IllegalArgumentException("请输入项目名称"));
		//如果存在则在当前项目下创建账单
		createBill(project);
		//更新项目的修改时间
		updateProjectModifyTime(project);
		//初始化数据
		iconList.forEach(item -> item.setSelected(false));
		associateList.forEach(item -> item.setSelected(false));
		billRecyclerViewAdapter.notifyDataSetChanged();
		itemIconRecyclerViewAdapter.notifyDataSetChanged();

		viewBinding.UIAddBillActivityTextViewAmount.setText(null);
		//更新返回页的数据
		BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
		BillFragmentShareData sharedData = billFragment.getDataBinding();
		sharedData.setProject(project);
	}

	private void updateProjectModifyTime(Project project) {
		project.setModifyTime(DateTimeUtil.format(new Date()));
		projectDao.update(project);
	}

	private void createBill(Project project) {
		//3. 日期、备注、金额
		String remark = viewBinding.UIAddBillActivityEditTextRemark.getText().toString().trim();
		String amount = viewBinding.UIAddBillActivityTextViewAmount.getText().toString().trim();
		if (StringUtils.isBlank(amount)) {
			throw new IllegalArgumentException("请输入消费金额");
		}
		//1. 选中的消费项目
		String consumerItemList = iconList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(","));
		//2. 选中的同行人
		String associateItemList = associateList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(","));

		Bill bill = new Bill();
		bill.setName(consumerItemList);
		bill.setProjectId(project.getId());
		bill.setAmount(Long.valueOf(amount));
		// TODO: 2021/11/6
		final String format = mockDate();
		bill.setConsumeDate(format);
		bill.setAssociateId(associateItemList);
		bill.setCreateTime(DateTimeUtil.format(new Date()));
		bill.setModifyTime(DateTimeUtil.format(new Date()));
		bill.setConsumptionType(consumptionType.name());
		bill.setRemark(remark);
		final String iconDownloadUrl = iconList.stream().filter(ImageIconInfo::isSelected).findFirst().map(ImageIconInfo::getIconDownloadUrl).orElse("");
		bill.setIconDownloadUrl(iconDownloadUrl);
		billDao.insert(bill);
	}

	@NotNull
	private String mockDate() {
		int i = RandomUtils.nextInt(0, 10);
		final String format = DateTimeUtil.format(DateTimeUtil.toLocalDateTime(new Date()).plusDays(i), "yyyy-MM-dd");
		return format;
	}

	private void createProject(String projectName) {
		Project project = new Project();
		project.setName(projectName);
		project.setStartTime(DateTimeUtil.format(new Date()));
		project.setEndTime(null);
		project.setCreateTime(DateTimeUtil.format(new Date()));
		project.setModifyTime(DateTimeUtil.format(new Date()));
		project.setRemark(null);
		projectDao.insert(project);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//启动的时候刷新当前页面的标题
		BillFragmentShareData dataBinding = ((BillFragment)MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment()).getDataBinding();
		if (dataBinding == null) {
			return;
		}
		Project project = dataBinding.getProject();
		if (project != null) {
			viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
		}

		//先获取当前是支出还是收入类型
		refreshConsumptionIcon(ConsumptionTypeEnum.INCOME);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//启动的时候刷新当前页面的标题
		BillFragmentShareData dataBinding = ((BillFragment)MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment()).getDataBinding();
		if (dataBinding == null) {
			return;
		}
		Project project = dataBinding.getProject();
		if (project != null) {
			viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
		}

		//先获取当前是支出还是收入类型
		refreshConsumptionIcon(ConsumptionTypeEnum.INCOME);
	}

	public void refreshConsumptionIcon(ConsumptionTypeEnum consumptionType) {
		final ConsumptionItemDao consumptionItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		List<ConsumptionItem> consumptionItems = consumptionItemDao.selectByType(consumptionType.name());
		if (CollectionUtils.isEmpty(consumptionItems)) {
			consumptionItems = new ArrayList<>();
			// 搞默认值 插入数据库 todo
		}

		final List<ImageIconInfo> imageIconInfos = consumptionItems.stream().map(consumptionItem -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setName(consumptionItem.getName());
			imageIconInfo.setIconDownloadUrl(consumptionItem.getIconDownloadUrl());
			imageIconInfo.setSelected(false);
			return imageIconInfo;
		}).collect(Collectors.toList());
		//添加编辑按钮
		ImageIconInfo imageIconInfo = new ImageIconInfo();
		imageIconInfo.setName(ItemIconEnum.ItemIconEdit.getName());
		imageIconInfo.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		imageIconInfo.setSelected(false);
		imageIconInfos.add(imageIconInfo);

		iconList.clear();
		iconList.addAll(imageIconInfos);
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void mock() {
		ImageIconInfo imageAddIconInfo2 = new ImageIconInfo();
		imageAddIconInfo2.setName(ItemIconEnum.ItemIconEdit.getName());
		imageAddIconInfo2.setIconDownloadUrl(ItemIconEnum.ItemIconEdit.getIconDownloadUrl());
		associateList.add(imageAddIconInfo2);
	}
}