package com.business.travel.app.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 *
 */
public class HeaderView {

	private final View headView;

	private HeaderView(View headView) {
		this.headView = headView;
	}

	public static HeaderView of(View headView) {
		return new HeaderView(headView);
	}

	/**
	 * 空的头部
	 *
	 * @param layoutInflater
	 * @return
	 */
	@SuppressLint("InflateParams")
	public static View newEmptyHeaderView(LayoutInflater layoutInflater) {
		View headView = layoutInflater.inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return headView;
	}

	/**
	 * 项目页面头部
	 *
	 * @param layoutInflater
	 * @return
	 */
	@SuppressLint("InflateParams")
	public static View newProjectHeaderView(LayoutInflater layoutInflater) {
		View headView = layoutInflater.inflate(R.layout.project_list_header_view, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return headView;
	}

	/**
	 * 账单页面头部
	 *
	 * @param layoutInflater
	 * @return
	 */
	@SuppressLint("InflateParams")
	public static View newBillHeaderView(LayoutInflater layoutInflater) {
		View headView = layoutInflater.inflate(R.layout.bill_list_header_view, null, false);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return headView;
	}

	public void addTo(SwipeRecyclerView swipeRecyclerView) {
		if (swipeRecyclerView.getHeaderCount() < 2) {
			swipeRecyclerView.addHeaderView(headView);
		}
	}

	public void removeFrom(SwipeRecyclerView swipeRecyclerView) {
		swipeRecyclerView.removeHeaderView(headView);
	}
}
