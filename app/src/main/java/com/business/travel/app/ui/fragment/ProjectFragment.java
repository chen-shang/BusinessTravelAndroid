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
import com.business.travel.app.ui.base.BaseFragment;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding> {

	private ProjectDao projectDao;
	private List<Project> projects;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		projectDao = AppDatabase.getInstance(getContext()).projectDao();
		projects = projectDao.selectAll();
		RecyclerView uiRecyclerView = binding.uiProjectFragmentSwipeRecyclerView;
		uiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		ProjectAdapter projectAdapter = new ProjectAdapter(projects);
		uiRecyclerView.setAdapter(projectAdapter);

		binding.uiProjectFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
			projects.clear();
			projects.addAll(projectDao.selectAll());
			binding.uiProjectFragmentSwipeRefreshLayout.setRefreshing(false);
		});
		return view;
	}
}