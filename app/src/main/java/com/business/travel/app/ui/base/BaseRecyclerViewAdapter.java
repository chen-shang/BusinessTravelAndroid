package com.business.travel.app.ui.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

/**
 * @author chenshang
 */
public abstract class BaseRecyclerViewAdapter<VH extends ViewHolder, DATA> extends RecyclerView.Adapter<VH> {
    /**
     * 加载的activity
     */
    protected final Context context;
    /**
     * 加载的数据模型
     */
    protected final List<DATA> dataList;

    public BaseRecyclerViewAdapter(List<DATA> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
        inject();
    }

    protected void inject() {
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
