package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.ui.fragment.DashBoardSharedData;
import com.business.travel.app.ui.fragment.DashboardFragment;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

	private final List<ImageIconInfo> iconList = new ArrayList<>();
	private final List<ImageIconInfo> associateList = new ArrayList<>();

	private BillDao billDao;
	private ProjectDao projectDao;

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
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLayoutManager(layoutManager);
		IconRecyclerViewAdapter billRecyclerViewAdapter = new IconRecyclerViewAdapter(iconList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setAdapter(billRecyclerViewAdapter);
		//长按移动排序
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(iconList, billRecyclerViewAdapter));

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		IconRecyclerViewAdapter iconRecyclerViewAdapter = new IconRecyclerViewAdapter(associateList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(iconRecyclerViewAdapter);
		//长按移动排序
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(associateList, iconRecyclerViewAdapter));

		GridLayoutManager layoutManager3 = new GridLayoutManager(this, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setLayoutManager(layoutManager3);
		KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter(new ArrayList<>(), this).onSaveClick(v -> {
			//当键盘保存按钮点击之后
			//1.参数校验
			String projectName = viewBinding.UIAddBillActivityTextViewProjectName.getText().toString();
			if (StringUtils.isBlank(projectName)) {
				return;
			}
			//2.根据项目名称查询是否存在该项目
			Project project = projectDao.selectByName(projectName);
			//3.如果不存在则新增项目
			if (project == null) {
				createProject(projectName);
				project = projectDao.selectByName(projectName);
			}
			if (project == null) {
				//TODO 创建失败
				return;
			}
			//4.如果存在则在当前项目下创建账单
			createBill(project);
			//5.更新项目的修改时间
			updateProjectModifyTime(project);
			//6.账单创建完成后跳转到 DashboardFragment
			Intent intent = new Intent(AddBillActivity.this, MasterActivity.class);
			startActivity(intent);
		}).onDeleteClick(v -> {
			//当键盘删除键点击之后
			//控制金额文本框的金额删除

		}).onReRecordClick(v -> {
			//当键盘再记按钮点击之后
			//1.参数校验
			//2.根据项目名称查询是否存在该项目
			//3.如果不存在则新增项目
			//4.如果存在则在当前项目下创建账单
			//5.更新项目的修改时间
			//6.注意不用留在当前页面
		});
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);
	}

	private void updateProjectModifyTime(Project project) {
		project.setModifyTime(DateTimeUtil.format(new Date()));
		projectDao.update(project);
	}

	private void createBill(Project project) {
		//1. 选中的消费项目
		String consumerItemList = iconList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.collect(Collectors.joining(","));
		//2. 选中的同行人
		String associateItemList = associateList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.collect(Collectors.joining(","));
		//3. 日期、备注、金额
		String calender = viewBinding.UIAddBillActivityTextViewCalendar.getText().toString();
		String remark = viewBinding.UIAddBillActivityEditTextRemark.getText().toString();
		String amount = viewBinding.UIAddBillActivityTextViewAmount.getText().toString();
		if (StringUtils.isBlank(amount)) {
			ToastUtils.make().setLeftIcon(R.mipmap.ic_error).show("请输入消费金额");
		}

		Bill bill = new Bill();
		bill.setName(consumerItemList);
		bill.setProjectId(project.getId());
		bill.setAmount(100 * Float.valueOf(amount).longValue());
		// TODO: 2021/11/6
		bill.setConsumeTime(DateTimeUtil.format(new Date()));
		bill.setAssociateId(associateItemList);
		bill.setCreateTime(DateTimeUtil.format(new Date()));
		bill.setModifyTime(DateTimeUtil.format(new Date()));
		bill.setRemark(remark);
		billDao.insert(bill);
	}

	private void createProject(String projectName) {
		Project project = new Project();
		project.setName(projectName);
		project.setStartTime(DateTimeUtil.format(new Date()));
		project.setEndTime(DateTimeUtil.format(new Date()));
		project.setCreateTime(DateTimeUtil.format(new Date()));
		project.setModifyTime(DateTimeUtil.format(new Date()));
		project.setRemark(DateTimeUtil.format(new Date()));
		projectDao.insert(project);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//启动的时候刷新当前页面的标题
		DashBoardSharedData dataBinding = ((DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment()).getDataBinding();
		Project project = dataBinding.getProject();
		if (project != null) {
			viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
		}
	}

	private void mock() {
		Arrays.stream(IconEnum.values()).forEach(iconEnum -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setResourceId(iconEnum.getResourceId());
			imageIconInfo.setName(iconEnum.getDescription());
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		});

		ImageIconInfo imageAddIconInfo = new ImageIconInfo();
		imageAddIconInfo.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo.setName("添加");
		iconList.add(imageAddIconInfo);

		ImageIconInfo imageAddIconInfoMe = new ImageIconInfo();
		imageAddIconInfoMe.setResourceId(R.drawable.vector_drawable_my);
		imageAddIconInfoMe.setName("我");
		associateList.add(imageAddIconInfoMe);

		ImageIconInfo imageAddIconInfo2 = new ImageIconInfo();
		imageAddIconInfo2.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo2.setName("添加");
		associateList.add(imageAddIconInfo2);
	}
}