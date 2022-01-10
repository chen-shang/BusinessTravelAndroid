package com.business.travel.app.view.header;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import com.business.travel.app.R;
import com.business.travel.app.view.header.BaseHeadView;

public class EmptyHeaderView extends BaseHeadView {

	@SuppressLint("InflateParams")
	public EmptyHeaderView(LayoutInflater layoutInflater) {
		headView = layoutInflater.inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
}
