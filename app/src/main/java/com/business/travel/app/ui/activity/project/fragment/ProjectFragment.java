package com.business.travel.app.ui.activity.project.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.activity.project.EditProjectActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.view.HeaderView;
import com.business.travel.app.utils.MoneyUtil;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding> {

	/**
	 * 项目列表
	 */
	private final List<Project> projectList = new ArrayList<>();
	/**
	 * 项目列表适配器
	 */
	private ProjectRecyclerViewAdapter projectListRecyclerViewAdapter;

	private ProjectService projectService;
	private View projectListHeaderView;
	private ListHeaderViewHolder listHeaderViewHolder;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View projectListEmptyHeaderView;

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(requireActivity());

		projectListHeaderView = HeaderView.newProjectHeaderView(getLayoutInflater());
		listHeaderViewHolder = ListHeaderViewHolder.init(projectListHeaderView);

		projectListEmptyHeaderView = HeaderView.newEmptyHeaderView(getLayoutInflater());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//注册项目列表页
		registerSwipeRecyclerView(viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList);
		//注册右上角点击事件
		registerPopupView(viewBinding.topTitleBar.contentBarRightIcon);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次进来的时候,都要刷新一下项目列表
		refreshProjectList();

		refreshProjectHeader();
	}

	/**
	 * 注册右上角点击事件
	 */
	private void registerPopupView(ImageView imageView) {
		//这个是添加新项目时候弹出的
		//这个是点击右上角三个小圆点弹出的
		final AttachListPopupView attachListPopupView = new Builder(getContext()).atView(imageView).asAttachList(new String[] {"添加项目"}, new int[] {R.drawable.ic_project_add}, (position, text) -> startActivity(new Intent(this.requireActivity(), EditProjectActivity.class)));
		imageView.setOnClickListener(v -> attachListPopupView.show());
	}

	/**
	 * 注册项目列表页
	 */
	private void registerSwipeRecyclerView(SwipeRecyclerView swipeRecyclerView) {
		//采用线性布局
		swipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
		HeaderView.of(projectListHeaderView).addTo(viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList);
		//项目列表适配器,这里面有项目卡片的布局
		projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projectList, (MasterActivity)requireActivity());
		swipeRecyclerView.setAdapter(projectListRecyclerViewAdapter);
	}

	/**
	 * 刷新项目列表
	 * TODO: 后续改成分页
	 */
	public void refreshProjectList() {
		SwipeRecyclerView swipeRecyclerView = viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList;
		//先查询全量
		final List<Project> projectList = projectService.queryAll();
		if (CollectionUtils.isEmpty(projectList)) {
			//如果没有数据就展示空Header
			notifyProjectDataClear();
			//显示空的Header
			HeaderView.of(projectListEmptyHeaderView).addTo(swipeRecyclerView);
			return;
		}

		//移除空的Header
		HeaderView.of(projectListEmptyHeaderView).removeFrom(swipeRecyclerView);
		//通知adapter更新列表
		notifyProjectListDataChange(projectList);
	}

	private void notifyProjectDataClear() {
		//清空原来的数据
		projectList.clear();
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void notifyProjectListDataChange(List<Project> allProjects) {
		projectList.clear();
		projectList.addAll(allProjects);
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}

	public void refreshProjectHeader() {
		//统计一下总收入
		Long sumTotalIncomeMoney = Optional.ofNullable(projectService.sumTotalIncomeMoney()).orElse(0L);
		TextView uIBillFragmentTextViewIncome = listHeaderViewHolder.projectIncome;
		uIBillFragmentTextViewIncome.setText(MoneyUtil.toYuanString(sumTotalIncomeMoney));

		//统计一下总支出
		Long sumTotalSpendingMoney = Optional.ofNullable(projectService.sumTotalSpendingMoney()).orElse(0L);
		TextView UIBillFragmentTextViewPay = listHeaderViewHolder.projectPay;
		UIBillFragmentTextViewPay.setText(MoneyUtil.toYuanString(sumTotalSpendingMoney));

		Long countTotalProjectByYear = projectService.countTotalProjectByYear(null);
		listHeaderViewHolder.projectCount.setText(String.valueOf(countTotalProjectByYear));

		//项目耗时
		Long duration = projectService.countTotalTravelDayByYear(null);
		listHeaderViewHolder.durationDay.setText(String.valueOf(duration));

	}

}

class ListHeaderViewHolder {
	TextView projectIncome;
	TextView projectPay;
	TextView projectCount;
	TextView endTime;
	TextView durationDay;

	public static ListHeaderViewHolder init(View listHeadView) {
		ListHeaderViewHolder holder = new ListHeaderViewHolder();
		holder.projectIncome = listHeadView.findViewById(R.id.UIProjectFragmentTextViewIncome);
		holder.projectPay = listHeadView.findViewById(R.id.UIProjectFragmentTextViewPay);
		holder.projectCount = listHeadView.findViewById(R.id.projectCount);

		//holder.startTime = listHeadView.findViewById(R.id.startTime);
		//holder.endTime = listHeadView.findViewById(R.id.endTime);
		holder.durationDay = listHeadView.findViewById(R.id.durationDay);
		return holder;
	}
}