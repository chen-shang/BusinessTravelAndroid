package com.business.travel.app.ui;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
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

		recyclerView.setLongPressDragEnabled(true);
		// 监听拖拽，更新UI
		recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
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
		});

		//设置侧滑菜单
		recyclerView.setSwipeMenuCreator((swipeLeftMenu, swipeRightMenu, viewType) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(TestActivity.this)
					.setBackground(R.drawable.icon_beizhu)
					.setImage(R.drawable.vector_drawable_my)
					.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)//设置高，这里使用match_parent，就是与item的高相同
					.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);//设置宽
			swipeRightMenu.addMenuItem(deleteItem);//设置右边的侧滑
		});

		recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
			@Override
			public void onItemClick(SwipeMenuBridge menuBridge, int adapterPosition) {
				menuBridge.closeMenu();
				int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。0是左，右是1，暂时没有用到
				//int adapterPosition = menuBridge.getPosition(); // RecyclerView的Item的position。
				int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
				Toast.makeText(TestActivity.this, "删除" + adapterPosition, Toast.LENGTH_SHORT).show();

			}
		});

		recyclerView.addItemDecoration(new DefaultItemDecoration(Color.RED));

		//设置侧滑菜单的点击事件
		recyclerView.setAdapter(adapter);
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
		user.setName(DateTimeUtil.format(new Date()));
		user.setStartTime(DateTimeUtil.format(new Date()));
		user.setEndTime(DateTimeUtil.format(new Date()));
		user.setRemark(DateTimeUtil.format(new Date()));
		user.setCreateTime(DateTimeUtil.format(new Date()));
		user.setModifyTime(DateTimeUtil.format(new Date()));
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

		//CompletableFutureUtil.runAsync(() -> {
		//	try {
		//		//final List<GiteeContent> v5ReposOwnerRepoGiteeContentsIncome = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/收入");
		//		//System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoGiteeContentsIncome));
		//
		//		final List<GiteeContent> v5ReposOwnerRepoGiteeContentsSpend = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出");
		//		final List<String> collect = v5ReposOwnerRepoGiteeContentsSpend.stream()
		//				.filter(item -> "dir".equals(item.getType()))
		//				.map(GiteeContent::getName)
		//				.collect(Collectors.toList());
		//		System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoGiteeContentsSpend));
		//		System.out.println("=================");
		//		System.out.println(JacksonUtil.toPrettyString(collect));
		//
		//		collect.forEach(item -> {
		//			final List<GiteeContent> v5ReposOwnerRepoContents = BusinessTravelResourceApi.getV5ReposOwnerRepoContents("/icon/支出/" + item);
		//			System.out.println("=========v5ReposOwnerRepoContents========");
		//			System.out.println(JacksonUtil.toPrettyString(v5ReposOwnerRepoContents));
		//		});
		//
		//	} catch (Exception e) {
		//		e.printStackTrace();
		//	}
		//});
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
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_project_recyclerview, parent, false);
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

			//cardView.setOnLongClickListener(v -> {
			//	new AlertDialog.Builder(cardView.getContext())
			//			.setTitle("删除")
			//			.setMessage("确定删除？？？")
			//			.setPositiveButton("确定", (dialog, which) -> {
			//				ToastUtils.showLong("点击了确定:" + which);
			//				projectDao.delete(user);
			//				mDataList.remove(position);
			//				adapter.notifyDataSetChanged();
			//			})
			//			.setNegativeButton("取消", null)
			//			.show();
			//	return false;
			//});

		}

		@Override
		public int getItemCount() {
			return userList.size();
		}
	}
}