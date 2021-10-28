package com.business.travel.app.activity.ui.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.business.travel.app.databinding.FragmentMyBinding;

/**
 * @author chenshang
 */
public class MyFragment extends Fragment {

	private MyViewModel myViewModel;
	private FragmentMyBinding binding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

		binding = FragmentMyBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textNotifications;
		myViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
}