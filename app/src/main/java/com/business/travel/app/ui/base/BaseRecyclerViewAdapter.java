package com.business.travel.app.ui.base;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;

/**
 * @author chenshang
 */
public abstract class BaseRecyclerViewAdapter<VH extends ViewHolder, DATA> extends RecyclerView.Adapter<VH> {
	/**
	 * 加载的activity
	 */
	protected final BaseActivity<? extends ViewBinding> activity;
	/**
	 * 加载的数据模型
	 */
	protected final List<DATA> dataList;

	public BaseRecyclerViewAdapter(List<DATA> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		this.dataList = dataList;
		this.activity = baseActivity;
		inject();
	}

	protected void inject() {}

	@Override
	public int getItemCount() {
		return dataList.size();
	}
}
