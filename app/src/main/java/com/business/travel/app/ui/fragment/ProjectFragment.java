package com.business.travel.app.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.ui.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.ShareData;

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
		Project project = new Project();
		project.setId(0L);
		project.setName("项目1");
		project.setStartTime("56789");
		project.setEndTime("98765");
		project.setCreateTime("ghjk");
		project.setModifyTime("56789");
		project.setRemark("12345");
		projects.add(project);

		Project project2 = new Project();
		project2.setId(1L);
		project2.setName("项目2");
		project2.setStartTime("56789");
		project2.setEndTime("98765");
		project2.setCreateTime("ghjk");
		project2.setModifyTime("56789");
		project2.setRemark("12345");
		projects.add(project2);


		RecyclerView uiRecyclerView = viewBinding.uiProjectFragmentSwipeRecyclerView;
		uiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		ProjectRecyclerViewAdapter projectRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());
		uiRecyclerView.setAdapter(projectRecyclerViewAdapter);

		viewBinding.uiProjectFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
			projects.clear();
			projects.addAll(projectDao.selectAll());
			viewBinding.uiProjectFragmentSwipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}
}