package com.business.travel.app.ui;

import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.ShareData;
import com.business.travel.utils.JacksonUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding, ShareData> {
	MyAdapter adapter;
	private UserDao userDao;
	private List<User> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDao = AppDatabase.getInstance(this).userDao();
		SwipeRecyclerView recyclerView = viewBinding.recyclerView;

		mDataList = userDao.selectAll();
		adapter = new MyAdapter(mDataList);

		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		// 拖拽排序，默认关闭。
		recyclerView.setLongPressDragEnabled(true);
		// 侧滑删除，默认关闭。
		recyclerView.setItemViewSwipeEnabled(true);

		OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {
			@Override
			public boolean onItemMove(ViewHolder srcHolder, ViewHolder targetHolder) {
				// 此方法在Item拖拽交换位置时被调用。
				// 第一个参数是要交换为之的Item，第二个是目标位置的Item。

				// 交换数据，并更新adapter。
				int fromPosition = srcHolder.getAdapterPosition();
				int toPosition = targetHolder.getAdapterPosition();
				Collections.swap(mDataList, fromPosition, toPosition);
				adapter.notifyItemMoved(fromPosition, toPosition);

				// 返回true，表示数据交换成功，ItemView可以交换位置。
				return true;
			}

			@Override
			public void onItemDismiss(ViewHolder srcHolder) {
				// 此方法在Item在侧滑删除时被调用。

				// 从数据源移除该Item对应的数据，并刷新Adapter。
				int position = srcHolder.getAdapterPosition();
				mDataList.remove(position);
				adapter.notifyItemRemoved(position);
			}
		};
		// 监听拖拽，更新UI
		recyclerView.setOnItemMoveListener(mItemMoveListener);
		viewBinding.recyclerView.setAdapter(adapter);

		viewBinding.swipeRedreshLayout.setOnRefreshListener(() -> {
			select(null);
			viewBinding.swipeRedreshLayout.setRefreshing(false);
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppDatabase.getInstance(this).close();
	}

	public void insert(View view) {
		User user = new User();
		user.setFirstName("chen" + RandomUtils.nextInt());
		user.setLastName("shang" + RandomUtils.nextInt());
		Long id = userDao.insert(user);
		user.setId(id);
		ToastUtils.showShort("插入成功:" + JacksonUtil.toString(user));

		mDataList.add(user);
		adapter.notifyDataSetChanged();
	}

	public void select(View view) {
		mDataList.clear();
		mDataList.addAll(userDao.selectAll());
		adapter.notifyDataSetChanged();
	}

	public void delete(View view) {
		userDao.selectAll().forEach(userDao::delete);
		mDataList.clear();
		adapter.notifyDataSetChanged();
	}

	private class MyAdapter extends Adapter {
		private final List<User> userList;

		public MyAdapter(List<User> userList) {this.userList = userList;}

		@NonNull
		@NotNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item_simple, parent, false);
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
			TextView userInfo = holder.itemView.findViewById(R.id.userInfo);
			User user = userList.get(position);
			userInfo.setText(JacksonUtil.toString(user));

			CardView cardView = holder.itemView.findViewById(R.id.card_view);
			cardView.setOnClickListener(v -> {
				ToastUtils.showLong(userInfo.getText());
			});

			cardView.setOnLongClickListener(v -> {
				new AlertDialog.Builder(cardView.getContext())
						.setTitle("删除")
						.setMessage("确定删除？？？")
						.setPositiveButton("确定", (dialog, which) -> {
							ToastUtils.showLong("点击了确定:" + which);
							userDao.delete(user);
							mDataList.remove(position);
							adapter.notifyDataSetChanged();
						})
						.setNegativeButton("取消", null)
						.show();
				return false;
			});

		}

		@Override
		public int getItemCount() {
			return userList.size();
		}
	}
}

