package com.business.travel.app.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

public class HeaderView {

	private final View headView;

	public HeaderView(View headView) {this.headView = headView;}

	public static HeaderView of(View headView) {
		return new HeaderView(headView);
	}

	@SuppressLint("InflateParams")
	public static View newEmptyHeaderView(LayoutInflater layoutInflater) {
		View headView = layoutInflater.inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return headView;
	}

	public void addTo(SwipeRecyclerView swipeRecyclerView) {
		if (swipeRecyclerView.getHeaderCount() == 0) {
			swipeRecyclerView.addHeaderView(headView);
		}
	}

	public void removeFrom(SwipeRecyclerView swipeRecyclerView) {
		swipeRecyclerView.removeHeaderView(headView);
	}
}