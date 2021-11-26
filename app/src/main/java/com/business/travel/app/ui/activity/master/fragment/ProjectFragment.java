package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.BaseSwipeMenuCreator;
import com.business.travel.app.ui.base.ShareData;
import com.business.travel.app.utils.LogToast;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding, ShareData> {

	private final List<Project> projects = new ArrayList<>();
	private ProjectRecyclerViewAdapter projectListRecyclerViewAdapter;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
		projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setAdapter(projectListRecyclerViewAdapter);
		viewBinding.UIProjectFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
			//下滑刷新项目列表
			refreshProjectList();
			viewBinding.UIProjectFragmentSwipeRefreshLayout.setRefreshing(false);
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次进来的时候,都要刷新一下项目列表
		refreshProjectList();
	}

	/**
	 * 刷新项目列表
	 * TODO: 后续改成分页
	 */
	private void refreshProjectList() {
		ProjectDao projectDao = AppDatabase.getInstance(getContext()).projectDao();
		//先清空
		projects.clear();
		//在查询全量
		final List<Project> allProjects = projectDao.selectAll();
		if (CollectionUtils.isNotEmpty(allProjects)) {
			projects.addAll(allProjects);
		}
		//通知adapter更新列表
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}
}