package com.business.travel.app.ui.activity.my.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.business.travel.app.databinding.FragmentMyBinding;
import com.business.travel.app.ui.test.TestActivity;
import com.business.travel.app.ui.activity.log.ChangeLogActivity;
import com.business.travel.app.ui.base.BaseFragment;

/**
 * @author chenshang
 */
public class MyFragment extends BaseFragment<FragmentMyBinding> {

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		viewBinding.circleImageView.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), TestActivity.class)));
		viewBinding.imageView7.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), ChangeLogActivity.class)));
		return view;
	}
}