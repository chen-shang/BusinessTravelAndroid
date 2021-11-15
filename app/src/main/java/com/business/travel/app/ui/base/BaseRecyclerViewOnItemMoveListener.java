package com.business.travel.app.ui.base;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

/**
 * @author chenshang
 */
public class BaseRecyclerViewOnItemMoveListener<T, VH extends ViewHolder> implements OnItemMoveListener {
	private final List<T> list;
	private final RecyclerView.Adapter<VH> adapter;

	/**
	 * 删除时候执行的业务逻辑
	 */
	private Runnable onItemMove;
	/**
	 * 移动时候执行的业务逻辑
	 */
	private Runnable onItemDismiss;

	public BaseRecyclerViewOnItemMoveListener(List<T> list, Adapter adapter) {
		this.list = list;
		this.adapter = adapter;
	}

	public BaseRecyclerViewOnItemMoveListener<T, VH> onItemMove(Runnable onItemMove) {
		this.onItemMove = onItemMove;
		return this;
	}

	public BaseRecyclerViewOnItemMoveListener<T, VH> onItemDismiss(Runnable onItemDismiss) {
		this.onItemDismiss = onItemDismiss;
		return this;
	}

	@Override
	public boolean onItemMove(ViewHolder srcHolder, ViewHolder targetHolder) {
		// 此方法在Item拖拽交换位置时被调用。
		// 第一个参数是要交换为之的Item，第二个是目标位置的Item。
		if (onItemMove != null) {
			onItemMove.run();
		}
		// 交换数据，并更新adapter。
		int fromPosition = srcHolder.getAdapterPosition();
		int toPosition = targetHolder.getAdapterPosition();
		Collections.swap(list, fromPosition, toPosition);
		adapter.notifyItemMoved(fromPosition, toPosition);

		// 返回true，表示数据交换成功，ItemView可以交换位置。
		return true;
	}

	@Override
	public void onItemDismiss(ViewHolder srcHolder) {
		// 此方法在Item在侧滑删除时被调用。
		if (onItemDismiss != null) {
			onItemDismiss.run();
		}

		// 从数据源移除该Item对应的数据，并刷新Adapter。
		int position = srcHolder.getAdapterPosition();
		list.remove(position);
		adapter.notifyItemRemoved(position);
	}
}
