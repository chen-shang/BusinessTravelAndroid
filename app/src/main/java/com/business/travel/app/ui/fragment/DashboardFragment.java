package com.business.travel.app.ui.fragment;

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

import static com.business.travel.app.databinding.FragmentDashboardBinding.inflate;

/**
 * @author chenshang
 */
public class DashboardFragment extends Fragment {

	private FragmentDashboardBinding binding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		binding = inflate(inflater, container, false);
		View root = binding.getRoot();
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