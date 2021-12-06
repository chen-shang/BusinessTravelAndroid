package com.business.travel.app.ui.activity.master.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.business.travel.app.databinding.FragmentMyBinding;
import com.business.travel.app.ui.TestActivity;
import com.business.travel.app.ui.activity.version.VersionHistoryActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.ShareData;

/**
 * @author chenshang
 */
public class MyFragment extends BaseFragment<FragmentMyBinding, ShareData> {

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		viewBinding.circleImageView.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), TestActivity.class)));
		viewBinding.imageView7.setOnClickListener(v -> startActivity(new Intent(this.getActivity(), VersionHistoryActivity.class)));
		return view;
	}
}