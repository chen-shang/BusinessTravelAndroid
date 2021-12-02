package com.business.travel.app.utils;

import android.view.View;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

public class HeaderView {

	private final View headView;

	public HeaderView(View headView) {this.headView = headView;}

	public static HeaderView of(View headView) {
		return new HeaderView(headView);
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
