package com.business.travel.app.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.business.travel.app.constant.ThreadPoolConstant;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.ui.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.ui.base.ShareData;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding, ShareData> {

	private final List<Project> projects = new ArrayList<>();
	private ProjectRecyclerViewAdapter projectListRecyclerViewAdapter;
	private ProjectDao projectDao;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		projectDao = AppDatabase.getInstance(getContext()).projectDao();

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
		projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());
		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setAdapter(projectListRecyclerViewAdapter);
		//开启滑动删除
		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setItemViewSwipeEnabled(true);
		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(projects, projectListRecyclerViewAdapter).onItemMove(() -> {
			//FIXME 滑动删除后的功能 @郑大朝
		}));

		viewBinding.UIProjectFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
			//下滑刷新项目列表
			ThreadPoolConstant.COMMON_THREAD_POOL.submit(() -> {
				refreshProjectList();
				viewBinding.UIProjectFragmentSwipeRefreshLayout.setRefreshing(false);
			});
		});
		return view;
	}

	/**
	 * 刷新项目列表
	 * TODO: 后续改成分页
	 */
	private void refreshProjectList() {
		//先清空
		projects.clear();
		//在查询全量
		projects.addAll(projectDao.selectAll());
		//通知adapter更新列表
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次进来的时候,都要刷新一下项目列表
		refreshProjectList();
	}
}