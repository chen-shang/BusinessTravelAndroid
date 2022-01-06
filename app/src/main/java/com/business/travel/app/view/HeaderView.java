package com.business.travel.app.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * 列表头
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
	 * 把视图添加到对应的view
	 *
	 * @param swipeRecyclerView
	 */
	public void addTo(SwipeRecyclerView swipeRecyclerView) {
		if (swipeRecyclerView.getHeaderCount() < 2) {
			swipeRecyclerView.addHeaderView(headView);
		}
	}

	/**
	 * 移除对应的视图
	 *
	 * @param swipeRecyclerView
	 */
	public void removeFrom(SwipeRecyclerView swipeRecyclerView) {
		swipeRecyclerView.removeHeaderView(headView);
	}
}
