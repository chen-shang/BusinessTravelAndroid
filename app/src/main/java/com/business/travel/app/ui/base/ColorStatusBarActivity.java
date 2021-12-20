package com.business.travel.app.ui.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.business.travel.app.R;
import com.business.travel.app.utils.StatusBarUtil;

public class ColorStatusBarActivity<VB extends ViewBinding> extends BaseActivity<VB> {

	@Override
	protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StatusBarUtil.setStatusBarView(getWindow(), getResources(), R.drawable.corners_shape_change);
	}
}
