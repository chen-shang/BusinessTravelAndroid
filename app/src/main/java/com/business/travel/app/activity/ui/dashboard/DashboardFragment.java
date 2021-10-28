package com.business.travel.app.activity.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.business.travel.app.R;
import com.business.travel.app.databinding.FragmentDashboardBinding;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class DashboardFragment extends Fragment {

	private DashboardViewModel dashboardViewModel;
	private FragmentDashboardBinding binding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textDashboard;
		dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
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