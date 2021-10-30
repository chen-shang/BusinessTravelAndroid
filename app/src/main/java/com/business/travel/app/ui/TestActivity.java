package com.business.travel.app.ui;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.utils.JacksonUtil;
import com.yl.recyclerview.wrapper.SwipeToDismissWrapper;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {
	SwipeToDismissWrapper mSwipeToDismissWrapper;
	private UserDao userDao;
	private List<User> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDao = AppDatabase.getInstance(this).userDao();
		RecyclerView mRecyclerView = binding.recyclerView;
		binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

		mDataList = userDao.loadAllUsers();
		MyAdapter commonAdapter = new MyAdapter(mDataList);

		mSwipeToDismissWrapper = new SwipeToDismissWrapper(commonAdapter, mDataList);
		mSwipeToDismissWrapper.attachToRecyclerView(mRecyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mSwipeToDismissWrapper);
		// 设置删除事件监听
		mSwipeToDismissWrapper.setItemDismissListener(position -> {
			// TODO
			userDao.deleteUsers(mDataList.get(position));
			mDataList.remove(position);
			// 刷新数据需要使用外层Adapter
			mSwipeToDismissWrapper.notifyDataSetChanged();
		});

		binding.swipeRedreshLayout.setOnRefreshListener(() -> {
			select(null);
			binding.swipeRedreshLayout.setRefreshing(false);
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
		Long id = userDao.insertUser(user);
		user.setId(id);
		ToastUtils.showShort("插入成功:" + JacksonUtil.toString(user));

		mDataList.add(user);
		mSwipeToDismissWrapper.notifyDataSetChanged();
	}

	public void select(View view) {
		mDataList.clear();
		mDataList.addAll(userDao.loadAllUsers());
		mSwipeToDismissWrapper.notifyDataSetChanged();
	}

	public void delete(View view) {
		userDao.loadAllUsers().forEach(userDao::deleteUsers);
		mDataList.clear();
		mSwipeToDismissWrapper.notifyDataSetChanged();
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
							userDao.deleteUsers(user);
							mDataList.remove(position);
							mSwipeToDismissWrapper.notifyDataSetChanged();
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

