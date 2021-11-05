package com.business.travel.app.ui.fragment;

import java.util.Date;
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
import com.business.travel.utils.DateTimeUtil;
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
		//TODO 删除
		mock();

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

	private void mock() {
		Project project = new Project();
		project.setId(0L);
		project.setName("项目1");
		project.setStartTime(DateTimeUtil.format(new Date()));
		project.setEndTime(DateTimeUtil.format(new Date()));
		project.setCreateTime(DateTimeUtil.format(new Date()));
		project.setModifyTime(DateTimeUtil.format(new Date()));
		project.setRemark("12345");
		projects.add(project);

		Project project2 = new Project();
		project2.setId(1L);
		project2.setName("项目2");
		project2.setStartTime(DateTimeUtil.format(new Date()));
		project2.setEndTime(DateTimeUtil.format(DateTimeUtil.toLocalDateTime(new Date()).plusDays(10)));
		project2.setCreateTime(DateTimeUtil.format(new Date()));
		project2.setModifyTime(DateTimeUtil.format(new Date()));
		project2.setRemark("12345");
		projects.add(project2);
	}
}