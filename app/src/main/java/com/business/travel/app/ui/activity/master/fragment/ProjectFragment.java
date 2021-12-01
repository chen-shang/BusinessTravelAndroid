package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.ShareData;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding, ShareData> {

	/**
	 * 项目列表
	 */
	private final List<Project> projects = new ArrayList<>();
	/**
	 * 项目列表适配器
	 */
	private ProjectRecyclerViewAdapter projectListRecyclerViewAdapter;

	private ProjectService projectService;

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(requireActivity());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
		projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());
		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setAdapter(projectListRecyclerViewAdapter);
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
		//在查询全量
		final List<Project> allProjects = projectService.queryAll();
		projects.clear();
		projects.addAll(allProjects);
		//通知adapter更新列表
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}
}