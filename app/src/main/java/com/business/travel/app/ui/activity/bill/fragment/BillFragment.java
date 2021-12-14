package com.business.travel.app.ui.activity.bill.fragment;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentBillBinding;
import com.business.travel.app.model.DateBillInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.business.travel.app.utils.HeaderView;
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
	private final List<DateBillInfo> dateBillInfoList = new ArrayList<>();
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
		registerSwipeRecyclerView(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
		return view;
	}

	/**
	 * 注册账单列表页
	 */
	private void registerSwipeRecyclerView(SwipeRecyclerView swipeRecyclerView) {
		//线性布局
		swipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(dateBillInfoList, (MasterActivity)requireActivity());
		HeaderView.of(billListHeadView).addTo(swipeRecyclerView);
		swipeRecyclerView.setAdapter(billRecyclerViewAdapter);
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

	@SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
	public void refreshBillList(Long projectId) {
		Project project = Optional.ofNullable(projectId).map(projectService::queryById).orElseGet(() -> projectService.queryLatestModify());
		if (project == null) {
			//记得清空数据
			viewBinding.UIBillFragmentTextViewProjectName.setText(null);
			billListHeaderViewHolder.uIBillFragmentTextViewIncome.setText(null);
			billListHeaderViewHolder.UIBillFragmentTextViewPay.setText(null);
			billListHeaderViewHolder.startTime.setText(null);
			billListHeaderViewHolder.endTime.setText(null);
			billListHeaderViewHolder.durationDay.setText("0");
			//展示空的头部即可
			HeaderView.of(billListEmptyHeaderView).addTo(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
			return;
		}

		this.selectedProjectId = project.getId();

		//先刷新头部
		viewBinding.UIBillFragmentTextViewProjectName.setText(project.getName().trim());

		//刷新顶部金额
		refreshMoneyShow(this.selectedProjectId);

		//查询全部的账单列表
		List<Bill> billList = billService.queryBillByProjectId(this.selectedProjectId);
		if (CollectionUtils.isEmpty(billList)) {
			HeaderView.of(billListEmptyHeaderView).addTo(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
			dateBillInfoList.clear();
			billRecyclerViewAdapter.notifyDataSetChanged();
			return;
		}

		HeaderView.of(billListEmptyHeaderView).removeFrom(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
		List<DateBillInfo> newDateBillInfoList = billList
				//to stream
				.stream()
				//按照消费日期分组,先转换成对应的年月日
				.collect(Collectors.groupingBy(item -> DateTimeUtil.format(item.getConsumeDate(), "yyyy-MM-dd")))
				//获取 entry set
				.entrySet()
				//to stream
				.stream()
				//转成时间戳
				.map(entry -> new DateBillInfo(DateTimeUtil.timestamp(entry.getKey(), "yyyy-MM-dd"), entry.getValue()))
				//按照消费时间有小到大排序
				.sorted(Comparator.comparing(DateBillInfo::getDate).reversed()).collect(Collectors.toList());

		this.dateBillInfoList.clear();
		dateBillInfoList.addAll(newDateBillInfoList);
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	public void refreshMoneyShow(Long projectId) {
		//统计一下总收入
		Long sumTotalIncomeMoney = Optional.ofNullable(billService.sumTotalIncomeMoney(projectId)).orElse(0L);
		TextView uIBillFragmentTextViewIncome = billListHeaderViewHolder.uIBillFragmentTextViewIncome;
		uIBillFragmentTextViewIncome.setText(String.valueOf(sumTotalIncomeMoney / 100));

		//统计一下总支出
		Long sumTotalSpendingMoney = Optional.ofNullable(billService.sumTotalSpendingMoney(projectId)).orElse(0L);
		TextView UIBillFragmentTextViewPay = billListHeaderViewHolder.UIBillFragmentTextViewPay;
		UIBillFragmentTextViewPay.setText(String.valueOf(sumTotalSpendingMoney / 100));

		//查询一下项目信息
		Project project = projectService.queryById(projectId);

		//项目开始时间
		String startTime = parseTime(project.getStartTime());
		billListHeaderViewHolder.startTime.setText(startTime);

		//项目结束时间
		String endTime = parseTime(project.getEndTime());
		billListHeaderViewHolder.endTime.setText(endTime);

		//项目耗时
		String duration = genDuration(project.getStartTime(), project.getEndTime());
		billListHeaderViewHolder.durationDay.setText(duration);
	}

	private String genDuration(Long startTime, Long endTime) {
		if (startTime == null || startTime <= 0) {
			return "∞";
		}
		if (endTime == null || endTime <= 0) {
			return String.valueOf(Duration.between(DateTimeUtil.toLocalDateTime(startTime), DateTimeUtil.now()).toDays());
		}
		return String.valueOf(Duration.between(DateTimeUtil.toLocalDateTime(startTime), DateTimeUtil.toLocalDateTime(endTime)).toDays());
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
	TextView UIBillFragmentTextViewPay;
	TextView startTime;
	TextView endTime;
	TextView durationDay;

	public static ListHeaderViewHolder init(View listHeadView) {
		ListHeaderViewHolder holder = new ListHeaderViewHolder();
		holder.uIBillFragmentTextViewIncome = listHeadView.findViewById(R.id.UIBillFragmentTextViewIncome);
		holder.UIBillFragmentTextViewPay = listHeadView.findViewById(R.id.UIBillFragmentTextViewPay);
		holder.startTime = listHeadView.findViewById(R.id.startTime);
		holder.endTime = listHeadView.findViewById(R.id.endTime);
		holder.durationDay = listHeadView.findViewById(R.id.durationDay);
		return holder;
	}
}