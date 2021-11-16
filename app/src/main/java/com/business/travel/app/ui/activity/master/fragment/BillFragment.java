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
import com.business.travel.app.databinding.FragmentBillBinding;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class BillFragment extends BaseFragment<FragmentBillBinding, BillFragmentShareData> {

	private final List<String> dateList = new ArrayList<>();
	private BillRecyclerViewAdapter billRecyclerViewAdapter;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		ProjectDao projectDao = AppDatabase.getInstance(this.getContext()).projectDao();
		//刚进入账单页面的时候,查询上次最后编辑或查看过的项目
		Project project = projectDao.selectLatestModify();
		if (project != null) {
			dataBinding.setProject(project);
		}

		//初始化
		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setLayoutManager(layoutManager);
		billRecyclerViewAdapter = new BillRecyclerViewAdapter(project, dateList, (MasterActivity)requireActivity());
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setAdapter(billRecyclerViewAdapter);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.show(floatingActionButton);
		Project project = dataBinding.getProject();
		if (project == null) {
			return;
		}
		show(project);
	}

	private void show(Project project) {
		final BillDao billDao = AppDatabase.getInstance(this.getContext()).billDao();
		TextView textView = viewBinding.textDashboard;
		textView.setText(project.getName());
		dateList.clear();
		dateList.addAll(billDao.selectConsumeDateByProjectId(project.getId()));
		billRecyclerViewAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.UI_MasterActivity_FloatingActionButton);
		AnimalUtil.reset(floatingActionButton);
	}
}