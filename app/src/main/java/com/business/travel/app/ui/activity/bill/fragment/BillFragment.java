package com.business.travel.app.ui.activity.bill.fragment;

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
	private View billListHeadView;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View billListEmptyHeaderView;

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
	/**
	 * 中间的加号
	 */
	private FloatingActionButton floatingActionButton;

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(requireActivity());
		billService = new BillService(requireActivity());
		
		billListHeadView = HeaderView.newBillHeaderView(getLayoutInflater());
		billListEmptyHeaderView = HeaderView.newEmptyHeaderView(getLayoutInflater());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		//初始化中间的加号
		floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
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
			//viewBinding.UIBillFragmentTextViewDate.setText(null);
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.INVISIBLE);
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.INVISIBLE);
			//展示空的头部即可
			HeaderView.of(billListEmptyHeaderView).addTo(viewBinding.UIBillFragmentSwipeRecyclerViewBillList);
			return;
		}

		this.selectedProjectId = project.getId();

		//先刷新头部
		viewBinding.UIBillFragmentTextViewProjectName.setText(project.getName().trim());
		//viewBinding.UIBillFragmentTextViewDate.setText(project.getProductTime());
		//刷新项目信息

		//刷新右上角金额
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
				//按照消费日期分组,先转换成对应的面月日
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

	public void refreshMoneyShow(Long id) {
		//统计一下总收入
		Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(id);
		viewBinding.UIBillFragmentTextViewIncome.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalIncomeMoney).ifPresent(money -> viewBinding.UIBillFragmentTextViewIncome.setText(String.format("收入:%s", (double)money / 100)));

		//统计一下总支出
		Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(id);
		viewBinding.UIBillFragmentTextViewPay.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalSpendingMoney).ifPresent(money -> viewBinding.UIBillFragmentTextViewPay.setText(String.format("支出:%s", (double)money / 100)));
	}
}