package com.business.travel.app.ui.activity.my.fragment;

import java.util.Optional;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.business.travel.app.databinding.FragmentMyBinding;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.log.ChangeLogActivity;
import com.business.travel.app.ui.activity.my.AboutMeActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.test.TestActivity;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.utils.DateTimeUtil;

/**
 * @author chenshang
 */
public class MyFragment extends BaseFragment<FragmentMyBinding> {

	private long time;
	private long counter = 0;
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
		viewBinding.aboutMe.contentBarLeftIcon.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), ChangeLogActivity.class)));

		viewBinding.topTitleBar.contentBarLeftIcon.setOnClickListener(v -> {

			if (DateTimeUtil.timestamp() - time < 2000) {
				counter++;
			} else {
				time = DateTimeUtil.timestamp();
				counter = 0;
			}

			if (counter == 5) {
				startActivity(new Intent(this.getActivity(), TestActivity.class));
			}
		});

		Intent goAboutMeActivityIntent = new Intent(this.requireActivity(), AboutMeActivity.class);
		viewBinding.aboutMe.contentBarRightIcon.setOnClickListener(v -> this.requireActivity().startActivity(goAboutMeActivityIntent));
		viewBinding.aboutMe.contentBarTitle.setOnClickListener(v -> this.requireActivity().startActivity(goAboutMeActivityIntent));
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
}