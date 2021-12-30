package com.business.travel.app.ui.activity.my.fragment;

import java.util.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.databinding.FragmentMyBinding;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.my.AboutMeActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.MoneyUtil;

/**
 * @author chenshang
 */
public class MyFragment extends BaseFragment<FragmentMyBinding> {

	private BillService billService;
	private ProjectService projectService;

	@Override
	protected void inject() {
		super.inject();
		billService = new BillService(this.getActivity());
		projectService = new ProjectService(this.getActivity());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		//进入到关于我的页面
		goAboutMeActivityOnViewClick(viewBinding.aboutMe);
		goAboutMeActivityOnViewClick(viewBinding.aboutMe.contentBarRightIcon);
		goAboutMeActivityOnViewClick(viewBinding.aboutMe.contentBarTitle);
		goAboutMeActivityOnViewClick(viewBinding.aboutMe.contentBarLeftIcon);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		Long totalTravelDay = projectService.countTotalTravelDayByYear(null);
		viewBinding.totalTravelDay.setText(String.valueOf(totalTravelDay));

		Long billCount = Optional.ofNullable(billService.countBill()).orElse(0L);
		viewBinding.totalBillCount.setText(String.valueOf(billCount));

		Long totalSpendingMoney = Optional.ofNullable(projectService.sumTotalSpendingMoney()).orElse(0L);
		viewBinding.totalPay.setText(MoneyUtil.toYuanString(totalSpendingMoney));
	}

	private void goAboutMeActivityOnViewClick(View view) {
		Intent goAboutMeActivityIntent = new Intent(this.requireActivity(), AboutMeActivity.class);
		view.setOnClickListener(v -> {
			LogUtils.i("跳转关于我的");
			this.requireActivity().startActivity(goAboutMeActivityIntent);
		});
	}
}