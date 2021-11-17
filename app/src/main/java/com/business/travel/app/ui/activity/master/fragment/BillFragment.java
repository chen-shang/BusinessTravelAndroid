package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentBillBinding;
import com.business.travel.app.model.DateBillInfo;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentBillBinding, BillFragmentShareData> {

	private final List<DateBillInfo> dateBillInfoList = new ArrayList<>();
	private BillRecyclerViewAdapter billRecyclerViewAdapter;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		//初始化
		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setLayoutManager(layoutManager);
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(dateBillInfoList, (MasterActivity)requireActivity());
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setAdapter(billRecyclerViewAdapter);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.show(floatingActionButton);
		Project project = dataBinding.getProject();
		if (project == null) {
			ProjectDao projectDao = AppDatabase.getInstance(this.getContext()).projectDao();
			//刚进入账单页面的时候,查询上次最后编辑或查看过的项目
			project = projectDao.selectLatestModify();
			dataBinding.setProject(project);
		}
		refreshBillList(project);
	}

	@SuppressLint("SetTextI18n")
	private void refreshBillList(Project project) {
		if (project == null) {
			return;
		}
		//先刷新头部
		viewBinding.UIBillFragmentTextViewProjectName.setText(project.getName().trim());
		viewBinding.UIBillFragmentTextViewDate.setText(project.getProductTime());

		final BillDao billDao = AppDatabase.getInstance(requireActivity()).billDao();
		final Long sumTotalIncomeMoney = billDao.sumTotalIncomeMoney(project.getId());
		if (sumTotalIncomeMoney == null || sumTotalIncomeMoney == 0) {
			viewBinding.UIBillFragmentTextViewIncome.setVisibility(View.GONE);
		} else {
			viewBinding.UIBillFragmentTextViewIncome.setText("收入:" + sumTotalIncomeMoney);
		}
		final Long sumTotalSpendingMoney = billDao.sumTotalSpendingMoney(project.getId());
		if (sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0) {
			viewBinding.UIBillFragmentTextViewPay.setVisibility(View.GONE);
		} else {
			viewBinding.UIBillFragmentTextViewPay.setText("支出:" + sumTotalSpendingMoney);
		}
		final List<Bill> billList = billDao.selectByProjectId(project.getId());
		if (CollectionUtils.isEmpty(billList)) {
			return;
		}

		dateBillInfoList.clear();
		billList.stream().collect(Collectors.groupingBy(Bill::getConsumeDate)).forEach((consumeDate, bills) -> {
			DateBillInfo dateBillInfo = new DateBillInfo();
			dateBillInfo.setDate(consumeDate);
			dateBillInfo.setBillList(bills);
			dateBillInfoList.add(dateBillInfo);
		});

		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.reset(floatingActionButton);
	}
}