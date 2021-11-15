package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentDashboardBinding;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentDashboardBinding, BillSharedData> {

	private BillDao billDao;
	private ProjectDao projectDao;
	private FloatingActionButton floatingActionButton;
	private List<String> dateList = new ArrayList<>();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		billDao = AppDatabase.getInstance(this.getContext()).billDao();
		projectDao = AppDatabase.getInstance(this.getContext()).projectDao();
		Project project = projectDao.selectLatestModify();
		if (project != null) {
			dataBinding.setProject(project);
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnimalUtil.show(floatingActionButton);
		Project project = dataBinding.getProject();
		if (project == null) {
			return;
		}
		show(project);
	}

	private void show(Project project) {
		TextView textView = viewBinding.textDashboard;
		textView.setText(project.getName());
		dateList = billDao.selectConsumeDateByProjectId(project.getId());
		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setLayoutManager(layoutManager);
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setAdapter(new BillRecyclerViewAdapter(project, dateList, (MasterActivity)requireActivity()));
	}

	@Override
	public void onPause() {
		super.onPause();
		AnimalUtil.reset(floatingActionButton);
	}
}