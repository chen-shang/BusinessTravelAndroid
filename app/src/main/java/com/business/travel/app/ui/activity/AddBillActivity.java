package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import com.business.travel.app.ui.fragment.DashBoardSharedData;
import com.business.travel.app.ui.fragment.DashboardFragment;
import com.business.travel.utils.DateTimeUtil;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

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
		DashBoardSharedData dataBinding = ((DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment()).getDataBinding();
		Project project = dataBinding.getProject();
		if (project != null) {
			viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
		}
		billDao = AppDatabase.getInstance(this).billDao();
		projectDao = AppDatabase.getInstance(this).projectDao();
		//todo 删除
		mock();

		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLayoutManager(layoutManager);
		IconRecyclerViewAdapter adapter = new IconRecyclerViewAdapter(iconList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setAdapter(adapter);

		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setOnItemMoveListener(new OnItemMoveListener() {
			@Override
			public boolean onItemMove(ViewHolder srcHolder, ViewHolder targetHolder) {
				// 此方法在Item拖拽交换位置时被调用。
				// 第一个参数是要交换为之的Item，第二个是目标位置的Item。

				// 交换数据，并更新adapter。
				int fromPosition = srcHolder.getAdapterPosition();
				int toPosition = targetHolder.getAdapterPosition();
				Collections.swap(iconList, fromPosition, toPosition);
				adapter.notifyItemMoved(fromPosition, toPosition);

				// 返回true，表示数据交换成功，ItemView可以交换位置。
				return true;
			}

			@Override
			public void onItemDismiss(ViewHolder srcHolder) {

			}
		});

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		IconRecyclerViewAdapter iconRecyclerViewAdapter = new IconRecyclerViewAdapter(associateList, this);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(iconRecyclerViewAdapter);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLongPressDragEnabled(true);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setOnItemMoveListener(new OnItemMoveListener() {
			@Override
			public boolean onItemMove(ViewHolder srcHolder, ViewHolder targetHolder) {
				// 此方法在Item拖拽交换位置时被调用。
				// 第一个参数是要交换为之的Item，第二个是目标位置的Item。

				// 交换数据，并更新adapter。
				int fromPosition = srcHolder.getAdapterPosition();
				int toPosition = targetHolder.getAdapterPosition();
				Collections.swap(associateList, fromPosition, toPosition);
				iconRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition);

				// 返回true，表示数据交换成功，ItemView可以交换位置。
				return true;
			}

			@Override
			public void onItemDismiss(ViewHolder srcHolder) {

			}
		});

		GridLayoutManager layoutManager3 = new GridLayoutManager(this, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setLayoutManager(layoutManager3);
		KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter(new ArrayList<>(), this);
		keyboardRecyclerViewAdapter.setOnSaveClick(v -> {
			//各种参数校验
			//根据名字查询project
			//如果没有对应的project则新建project
			String projectName = viewBinding.UIAddBillActivityTextViewProjectName.getText().toString();
			Project project0 = projectDao.selectByName(projectName);
			if (project0 == null) {
				Project project1 = new Project();
				project1.setName(projectName);
				project1.setStartTime(DateTimeUtil.format(new Date()));
				project1.setEndTime(DateTimeUtil.format(new Date()));
				project1.setCreateTime(DateTimeUtil.format(new Date()));
				project1.setModifyTime(DateTimeUtil.format(new Date()));
				project1.setRemark(DateTimeUtil.format(new Date()));
				projectDao.insert(project1);

				project0 = projectDao.selectByName(projectName);
			}

			if (project0 == null) {
				ToastUtils.make().setLeftIcon(R.mipmap.ic_error).show("项目创建失败");
				return;
			}

			Bill bill = new Bill();
			String name = iconList.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getName).collect(Collectors.joining(","));
			bill.setName(name);
			bill.setProjectId(project0.getId());
			String amount = viewBinding.textView5.getText().toString();
			bill.setAmount(Long.valueOf(amount));

			bill.setConsumeTime(DateTimeUtil.format(new Date()));
			//同行人
			String associateId = associateList.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getName).collect(Collectors.joining(","));
			bill.setAssociateId(associateId);
			bill.setCreateTime(DateTimeUtil.format(new Date()));
			bill.setModifyTime(DateTimeUtil.format(new Date()));

			String remark = viewBinding.editText.getText().toString();
			bill.setRemark(remark);
			billDao.insert(bill);
			//然后在新建账单
			ToastUtils.showLong("添加成功");
		});
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);
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