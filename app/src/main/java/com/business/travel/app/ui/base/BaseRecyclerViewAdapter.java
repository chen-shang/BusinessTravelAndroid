package com.business.travel.app.ui.base;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;

/**
 * @author chenshang
 */
public abstract class BaseRecyclerViewAdapter<T extends ViewHolder, O> extends RecyclerView.Adapter<T> {
	protected final List<O> dataList;
	protected final BaseActivity<? extends ViewBinding, ShareData> baseActivity;

	public BaseRecyclerViewAdapter(List<O> dataList, BaseActivity<? extends ViewBinding, ShareData> baseActivity) {
		this.dataList = dataList;
		this.baseActivity = baseActivity;
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}
}
