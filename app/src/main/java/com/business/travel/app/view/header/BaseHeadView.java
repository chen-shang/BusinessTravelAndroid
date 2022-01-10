package com.business.travel.app.view.header;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

public abstract class BaseHeadView {
	//判断当前视图是否已经添加过了
	private static final Map<SwipeRecyclerView, BaseHeadView> map = new HashMap<>();
	//账单页面顶部视图
	protected View headView;

	public void addTo(SwipeRecyclerView swipeRecyclerView) {
		if (this.equals(map.get(swipeRecyclerView))) {
			//已经添加了
			return;
		}
		swipeRecyclerView.addHeaderView(headView);
		map.put(swipeRecyclerView, this);
	}

	public void removeFrom(SwipeRecyclerView swipeRecyclerView) {
		swipeRecyclerView.removeHeaderView(headView);
		map.remove(swipeRecyclerView);
	}
}
