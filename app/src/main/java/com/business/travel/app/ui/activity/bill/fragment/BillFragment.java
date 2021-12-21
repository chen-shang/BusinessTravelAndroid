package com.business.travel.app.ui.activity.bill.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentBillBinding;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.business.travel.app.utils.DurationUtil;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.view.HeaderView;
import com.business.travel.utils.DateTimeUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentBillBinding> {
	/**
	 * 账单列表数据
	 */
	private final List<Long> dateList = new ArrayList<>();
	@Getter
	private BillRecyclerViewAdapter billRecyclerViewAdapter;

	/**
	 * 当前页面选中的项目
	 */
	@Setter
	@Getter
	private Long selectedProjectId;

	/**
	 * 账单列表顶部
	 */
	private View billListHeadView;
	/**
	 * 账单顶部view内的元素
	 */
	private ListHeaderViewHolder billListHeaderViewHolder;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View billListEmptyHeaderView;

	/**
	 * 引入各种service
	 */
	private ProjectService projectService;
	private BillService billService;

	/**
	 * 中间的加号
	 */
	private FloatingActionButton floatingActionButton;

	@Override
	protected void inject() {
		super.inject();
		//注入service
		projectService = new ProjectService(requireActivity());
		billService = new BillService(requireActivity());

		//注入head view
		billListHeadView = HeaderView.newBillHeaderView(getLayoutInflater());
		//初始化head view对应的view
		billListHeaderViewHolder = ListHeaderViewHolder.init(billListHeadView);

		billListEmptyHeaderView = HeaderView.newEmptyHeaderView(getLayoutInflater());

		//初始化中间的加号
		floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//注册账单列表页
		registerSwipeRecyclerView(viewBinding.RecyclerViewBillList);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		//旋转上升动画显示中间的加号
		AnimalUtil.show(floatingActionButton);
		//刷新当前项目的账单数据
		refreshBillList(selectedProjectId);
	}

	@Override
	public void onPause() {
		super.onPause();
		AnimalUtil.reset(floatingActionButton);
	}

	/**
	 * 注册账单列表页
	 */
	private void registerSwipeRecyclerView(SwipeRecyclerView swipeRecyclerView) {
		//线性布局
		swipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(dateList, (MasterActivity)requireActivity());
		HeaderView.of(billListHeadView).addTo(swipeRecyclerView);
		swipeRecyclerView.setAdapter(billRecyclerViewAdapter);
	}

	@SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
	public void refreshBillList(Long projectId) {
		//默认显示最后一次访问过的项目
		Project project = Optional.ofNullable(projectId).map(projectService::queryById).orElseGet(() -> projectService.queryLatestModify());
		if (project == null) {
			//记得清空数据
			viewBinding.topTitleBar.contentBarTitle.setText(null);
			billListHeaderViewHolder.uIBillFragmentTextViewIncome.setText(null);
			billListHeaderViewHolder.uIBillFragmentTextViewPay.setText(null);
			billListHeaderViewHolder.startTime.setText(null);
			billListHeaderViewHolder.endTime.setText(null);
			billListHeaderViewHolder.durationDay.setText(null);

			dateList.clear();
			billRecyclerViewAdapter.notifyDataSetChanged();
			//展示空的头部即可
			HeaderView.of(billListEmptyHeaderView).addTo(viewBinding.RecyclerViewBillList);
			return;
		}

		this.selectedProjectId = project.getId();

		//先刷新头部
		viewBinding.topTitleBar.contentBarTitle.setText(project.getName());

		//刷新顶部金额
		refreshMoneyShow(this.selectedProjectId);

		//消费日期,列表的最外层
		List<Long> dateList = billService.queryConsumeDateByProjectId(this.selectedProjectId);
		if (CollectionUtils.isEmpty(dateList)) {
			HeaderView.of(billListEmptyHeaderView).addTo(viewBinding.RecyclerViewBillList);
			dateList.clear();
			billRecyclerViewAdapter.notifyDataSetChanged();
			return;
		}

		HeaderView.of(billListEmptyHeaderView).removeFrom(viewBinding.RecyclerViewBillList);

		this.dateList.clear();
		this.dateList.addAll(dateList);
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	public void refreshMoneyShow(Long projectId) {
		//统计一下总收入
		Long sumTotalIncomeMoney = Optional.ofNullable(billService.sumTotalIncomeMoney(projectId)).orElse(0L);
		TextView uIBillFragmentTextViewIncome = billListHeaderViewHolder.uIBillFragmentTextViewIncome;
		uIBillFragmentTextViewIncome.setText(MoneyUtil.toYuanString(sumTotalIncomeMoney));

		//统计一下总支出
		Long sumTotalSpendingMoney = Optional.ofNullable(billService.sumTotalSpendingMoney(projectId)).orElse(0L);
		TextView UIBillFragmentTextViewPay = billListHeaderViewHolder.uIBillFragmentTextViewPay;
		UIBillFragmentTextViewPay.setText(MoneyUtil.toYuanString((sumTotalSpendingMoney)));

		//查询一下项目信息
		Project project = projectService.queryById(projectId);

		//项目开始时间
		String startTime = parseTime(project.getStartTime());
		billListHeaderViewHolder.startTime.setText(startTime);

		//项目结束时间
		String endTime = parseTime(project.getEndTime());
		billListHeaderViewHolder.endTime.setText(endTime);

		//项目耗时
		String duration = String.valueOf(DurationUtil.durationDay(DurationUtil.convertTimePeriod(project)));
		billListHeaderViewHolder.durationDay.setText(duration);
	}

	private String parseTime(Long startTime) {
		if (startTime == null || startTime <= 0) {
			return "--";
		}
		return DateTimeUtil.format(startTime, "yyyy-MM-dd");
	}
}

class ListHeaderViewHolder {
	TextView uIBillFragmentTextViewIncome;
	TextView uIBillFragmentTextViewPay;
	TextView startTime;
	TextView endTime;
	TextView durationDay;

	public static ListHeaderViewHolder init(View listHeadView) {
		ListHeaderViewHolder holder = new ListHeaderViewHolder();
		holder.uIBillFragmentTextViewIncome = listHeadView.findViewById(R.id.UIBillFragmentTextViewIncome);
		holder.uIBillFragmentTextViewPay = listHeadView.findViewById(R.id.UIBillFragmentTextViewPay);
		holder.startTime = listHeadView.findViewById(R.id.startTime);
		holder.endTime = listHeadView.findViewById(R.id.endTime);
		holder.durationDay = listHeadView.findViewById(R.id.durationDay);
		return holder;
	}
}