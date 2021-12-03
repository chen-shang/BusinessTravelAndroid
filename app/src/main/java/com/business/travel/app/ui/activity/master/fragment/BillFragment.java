package com.business.travel.app.ui.activity.master.fragment;

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
import android.view.ViewGroup.LayoutParams;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
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
import com.business.travel.app.ui.base.ShareData;
import com.business.travel.app.utils.AnimalUtil;
import com.business.travel.app.utils.HeaderView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentBillBinding, ShareData> {

	private final List<DateBillInfo> dateBillInfoList = new ArrayList<>();
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View headView;
	@Getter
	private BillRecyclerViewAdapter billRecyclerViewAdapter;
	private ProjectService projectService;
	private BillService billService;

	/**
	 * 当前页面选中的项目
	 */
	@Setter
	@Getter
	private Long selectedProjectId;

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(requireActivity());
		billService = new BillService(requireActivity());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		// 初始化  列表为空时候显示的内容,用headView实现该效果
		initHeadView();
		//注册账单列表页
		registerSwipeRecyclerView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.show(floatingActionButton);
		//刷新当前项目的账单数据
		refreshBillList(selectedProjectId);
	}

	/**
	 * 注册账单列表页
	 */
	private void registerSwipeRecyclerView() {
		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.UIBillFragmentSwipeRecyclerViewBillList.setLayoutManager(layoutManager);
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(dateBillInfoList, (MasterActivity)requireActivity());
		viewBinding.UIBillFragmentSwipeRecyclerViewBillList.setAdapter(billRecyclerViewAdapter);
	}

	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private void initHeadView() {
		headView = getLayoutInflater().inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	}

	@SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
	public void refreshBillList(Long projectId) {
		Project project = Optional.ofNullable(projectId).map(projectService::queryById).orElseGet(() -> projectService.queryLatestModify());
		if (project == null) {
			//记得清空数据
			viewBinding.UIBillFragmentTextViewProjectName.setText(null);
			viewBinding.UIBillFragmentTextViewDate.setText(null);
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.INVISIBLE);
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.INVISIBLE);
			//展示空的头部即可
			HeaderView.of(headView).addTo(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
			return;
		}

		this.selectedProjectId = project.getId();

		//先刷新头部
		viewBinding.UIBillFragmentTextViewProjectName.setText(project.getName().trim());
		viewBinding.UIBillFragmentTextViewDate.setText(project.getProductTime());

		//刷新右上角金额
		refreshMoneyShow(this.selectedProjectId);

		//查询全部的账单列表
		List<Bill> billList = billService.selectByProjectId(this.selectedProjectId);
		if (CollectionUtils.isEmpty(billList)) {
			//展示空的头部即可
			HeaderView.of(headView).addTo(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
			dateBillInfoList.clear();
			billRecyclerViewAdapter.notifyDataSetChanged();
			return;
		}

		HeaderView.of(headView).removeFrom(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
		List<DateBillInfo> newDateBillInfoList = billList.stream().collect(Collectors.groupingBy(Bill::getConsumeDate)).entrySet().stream().map(entry -> new DateBillInfo(entry.getKey(), entry.getValue())).sorted(Comparator.comparing(DateBillInfo::getDate).reversed()).collect(Collectors.toList());
		this.dateBillInfoList.clear();
		dateBillInfoList.addAll(newDateBillInfoList);
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	public void refreshMoneyShow(Long id) {
		//统计一下总收入
		Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(id);
		viewBinding.UIBillFragmentTextViewIncome.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalIncomeMoney).ifPresent(
				money -> viewBinding.UIBillFragmentTextViewIncome.setText("收入:" + (double)money / 100)
		);

		//统计一下总支出
		Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(id);
		viewBinding.UIBillFragmentTextViewPay.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalSpendingMoney).ifPresent(
				money -> viewBinding.UIBillFragmentTextViewPay.setText("支出:" + (double)money / 100)
		);
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.reset(floatingActionButton);
	}
}