package com.business.travel.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.business.travel.app.R;
import com.business.travel.app.databinding.FragmentDashboardBinding;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class DashboardFragment extends BaseFragment<FragmentDashboardBinding> {

	private FloatingActionButton floatingActionButton;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnimalUtil.show(floatingActionButton);
	}

	@Override
	public void onPause() {
		super.onPause();
		AnimalUtil.reset(floatingActionButton);
	}
}