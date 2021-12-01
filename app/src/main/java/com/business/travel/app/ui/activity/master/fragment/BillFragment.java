package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
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
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentBillBinding;
import com.business.travel.app.model.DateBillInfo;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentBillBinding, BillFragmentShareData> {

	private final List<DateBillInfo> dateBillInfoList = new ArrayList<>();
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View headView;
	private BillRecyclerViewAdapter billRecyclerViewAdapter;
	private ProjectService projectService;

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
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		headView = getLayoutInflater().inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		//初始化
		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.UIBillFragmentSwipeRecyclerViewBillList.setLayoutManager(layoutManager);
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(dateBillInfoList, (MasterActivity)requireActivity());
		viewBinding.UIBillFragmentSwipeRecyclerViewBillList.setAdapter(billRecyclerViewAdapter);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.show(floatingActionButton);
		refresh(selectedProjectId);
	}

	public void refresh(Long selectedProjectId) {
		Project project = Optional.ofNullable(selectedProjectId).map(projectService::queryById).orElseGet(() -> projectService.queryLatestModify());
		refreshBillList(project);
	}

	@SuppressLint("SetTextI18n")
	private void refreshBillList(Project project) {
		if (project == null) {
			//展示空的头部即可
			viewBinding.UIBillFragmentTextViewProjectName.setText(null);
			viewBinding.UIBillFragmentTextViewDate.setText(null);
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.INVISIBLE);
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.INVISIBLE);
			showEmptyHeader();
			return;
		}
		selectedProjectId = project.getId();

		//先刷新头部
		viewBinding.UIBillFragmentTextViewProjectName.setText(project.getName().trim());
		viewBinding.UIBillFragmentTextViewDate.setText(project.getProductTime());

		BillDao billDao = AppDatabase.getInstance(requireActivity()).billDao();
		Long sumTotalIncomeMoney = billDao.sumTotalIncomeMoney(project.getId());
		if (sumTotalIncomeMoney == null || sumTotalIncomeMoney == 0) {
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.INVISIBLE);
		} else {
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.VISIBLE);
			viewBinding.UIBillFragmentTextViewIncome.setText("收入:" + (double)sumTotalIncomeMoney / 100);
		}
		Long sumTotalSpendingMoney = billDao.sumTotalSpendingMoney(project.getId());
		if (sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0) {
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.INVISIBLE);
		} else {
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.VISIBLE);
			viewBinding.UIBillFragmentTextViewPay.setText("支出:" + (double)sumTotalSpendingMoney / 100);
		}

		List<Bill> billList = billDao.selectByProjectId(project.getId());
		if (CollectionUtils.isEmpty(billList)) {
			showEmptyHeader();
			return;
		}

		viewBinding.UIBillFragmentSwipeRecyclerViewBillList.removeHeaderView(headView);
		dateBillInfoList.clear();
		billList.stream().collect(Collectors.groupingBy(Bill::getConsumeDate)).forEach((consumeDate, bills) -> {
			DateBillInfo dateBillInfo = new DateBillInfo();
			dateBillInfo.setDate(consumeDate);
			dateBillInfo.setBillList(bills);
			dateBillInfoList.add(dateBillInfo);
		});
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void showEmptyHeader() {
		dateBillInfoList.clear();
		billRecyclerViewAdapter.notifyDataSetChanged();
		if (viewBinding.UIBillFragmentSwipeRecyclerViewBillList.getHeaderCount() == 0) {
			viewBinding.UIBillFragmentSwipeRecyclerViewBillList.addHeaderView(headView);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.reset(floatingActionButton);
	}
}