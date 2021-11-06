package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
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
		KeyboardUtils.fixAndroidBug5497(this);

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
			try {
				//当键盘保存按钮点击之后
				saveBill(billRecyclerViewAdapter, iconRecyclerViewAdapter);
				//6.账单创建完成后跳转到 DashboardFragment
				ActivityUtils.finishActivity(AddBillActivity.this, true);
			} catch (Exception e) {
				ToastUtils.make().setLeftIcon(R.drawable.icon_error).setGravity(Gravity.CENTER, 0, 0).setDurationIsLong(true).show(e.getMessage());
			}
		}).onDeleteClick(v -> {
			//当键盘删除键点击之后
			//控制金额文本框的金额删除

		}).onReRecordClick(v -> {
			try {
				//当键盘保存按钮点击之后
				saveBill(billRecyclerViewAdapter, iconRecyclerViewAdapter);
			} catch (Exception e) {
				ToastUtils.make().setLeftIcon(R.drawable.icon_error).setGravity(Gravity.CENTER, 0, 0).setDurationIsLong(true).show(e.getMessage());
			}
		});
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);
	}

	private void saveBill(IconRecyclerViewAdapter billRecyclerViewAdapter, IconRecyclerViewAdapter iconRecyclerViewAdapter) {
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
		iconRecyclerViewAdapter.notifyDataSetChanged();

		viewBinding.UIAddBillActivityTextViewAmount.setText(null);
		//更新返回页的数据
		DashboardFragment dashboardFragment = MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment();
		DashBoardSharedData sharedData = dashboardFragment.getDataBinding();
		sharedData.setProject(project);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = {0, 0};
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				//使EditText触发一次失去焦点事件
				v.setFocusable(false);
				//                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
				v.setFocusableInTouchMode(true);
				return true;
			}
		}
		return false;
	}

	private void updateProjectModifyTime(Project project) {
		project.setModifyTime(DateTimeUtil.format(new Date()));
		projectDao.update(project);
	}

	private void createBill(Project project) {
		//3. 日期、备注、金额
		String calender = viewBinding.UIAddBillActivityTextViewCalendar.getText().toString();
		String remark = viewBinding.UIAddBillActivityEditTextRemark.getText().toString().trim();
		String amount = viewBinding.UIAddBillActivityTextViewAmount.getText().toString().trim();
		if (StringUtils.isBlank(amount)) {
			throw new IllegalArgumentException("请输入消费金额");
		}
		//1. 选中的消费项目
		String consumerItemList = iconList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.filter(StringUtils::isBlank)
				.collect(Collectors.joining(","));
		//2. 选中的同行人
		String associateItemList = associateList.stream()
				.filter(ImageIconInfo::isSelected)
				.map(ImageIconInfo::getName)
				.filter(StringUtils::isBlank)
				.collect(Collectors.joining(","));

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