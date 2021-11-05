package com.business.travel.app.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.ui.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.ShareData;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding, ShareData> {

	private ProjectDao projectDao;
	private List<Project> projects;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		projectDao = AppDatabase.getInstance(getContext()).projectDao();
		projects = projectDao.selectAll();

		SwipeRecyclerView projectListRecyclerView = viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList;
		projectListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		ProjectRecyclerViewAdapter projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());
		projectListRecyclerView.setAdapter(projectListRecyclerViewAdapter);
		viewBinding.uiProjectFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
			projects.clear();
			projects.addAll(projectDao.selectAll());
			projectListRecyclerViewAdapter.notifyDataSetChanged();
			viewBinding.uiProjectFragmentSwipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次进来的时候,都要刷新一下项目列表
	}
}