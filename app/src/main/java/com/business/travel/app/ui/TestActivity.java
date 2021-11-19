package com.business.travel.app.ui;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.business.travel.utils.JacksonUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {
	MyAdapter adapter;
	private ProjectDao projectDao;
	private List<Project> mDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		projectDao = AppDatabase.getInstance(this).projectDao();
		//CompletableFutureUtil.runAsync(() -> {
		//
		//	try {
		//
		//		final String s = HttpWrapper.withOkHttpClient(new OkHttpClient()).doGet("https://gitee.com/chen-shang/business-travel-resource/raw/master/config/icon.json");
		//		final Request build = new Builder().url("https://gitee.com/chen-shang/business-travel-resource/raw/master/icon/12%20%E4%B8%AA%E6%8A%A4/3%20%E5%8F%A3%E7%BA%A2.svg").build();
		//		final InputStream inputStream = new OkHttpClient().newCall(build).execute().body().byteStream();
		//		Sharp.loadInputStream(inputStream).into(viewBinding.image1);
		//		System.out.println("+========" + s);
		//		ToastUtils.showShort(s);
		//	} catch (IOException e) {
		//		e.printStackTrace();
		//	}
		//
		//});

		SwipeRecyclerView recyclerView = viewBinding.recyclerView;

		mDataList = projectDao.selectAll();
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

		//viewBinding.swipeRedreshLayout.setOnRefreshListener(() -> {
		//	select(null);
		//	viewBinding.swipeRedreshLayout.setRefreshing(false);
		//});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppDatabase.getInstance(this).close();
	}

	public void insert(View view) {
		Project user = new Project();
		user.setId(0L);
		user.setName("");
		user.setStartTime("");
		user.setEndTime("");
		user.setRemark("");
		user.setCreateTime("");
		user.setModifyTime("");

		Long id = projectDao.insert(user);
		user.setId(id);
		ToastUtils.showShort("插入成功:" + JacksonUtil.toString(user));

		mDataList.add(user);
		adapter.notifyDataSetChanged();
	}

	public void select(View view) {
		mDataList.clear();
		mDataList.addAll(projectDao.selectAll());
		adapter.notifyDataSetChanged();

		CompletableFutureUtil.runAsync(() -> {
			try {
				//final List<GiteeContent> v5ReposOwnerRepoGiteeContentsIncome = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/收入");
				//System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoGiteeContentsIncome));

				final List<GiteeContent> v5ReposOwnerRepoGiteeContentsSpend = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出");
				final List<String> collect = v5ReposOwnerRepoGiteeContentsSpend.stream()
						.filter(item -> "dir".equals(item.getType()))
						.map(GiteeContent::getName)
						.collect(Collectors.toList());
				System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoGiteeContentsSpend));
				System.out.println("=================");
				System.out.println(JacksonUtil.toPrettyString(collect));

				collect.forEach(item -> {
					final List<GiteeContent> v5ReposOwnerRepoContents = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出/" + item);
					System.out.println("=========v5ReposOwnerRepoContents========");
					System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoContents));
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void delete(View view) {
		projectDao.selectAll().forEach(projectDao::delete);
		mDataList.clear();
		adapter.notifyDataSetChanged();
	}

	private class MyAdapter extends Adapter {
		private final List<Project> userList;

		public MyAdapter(List<Project> userList) {this.userList = userList;}

		@NonNull
		@NotNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_project_item, parent, false);
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
			TextView userInfo = holder.itemView.findViewById(R.id.UI_ProjectItem_TextView_ProjectName);
			Project user = userList.get(position);
			userInfo.setText(JacksonUtil.toString(user));

			CardView cardView = holder.itemView.findViewById(R.id.UI_ProjectItem_CardView);
			cardView.setOnClickListener(v -> {
				ToastUtils.showLong(userInfo.getText());
			});

			cardView.setOnLongClickListener(v -> {
				new AlertDialog.Builder(cardView.getContext())
						.setTitle("删除")
						.setMessage("确定删除？？？")
						.setPositiveButton("确定", (dialog, which) -> {
							ToastUtils.showLong("点击了确定:" + which);
							projectDao.delete(user);
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

@Data
class Content {
	private List<GiteeContent> giteeContents;
}
