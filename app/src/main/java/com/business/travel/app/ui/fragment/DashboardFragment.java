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

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionButton view = getActivity().findViewById(R.id.floatingActionButton);
		AnimalUtil.show(view);
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionButton view = getActivity().findViewById(R.id.floatingActionButton);
		AnimalUtil.reset(view);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}