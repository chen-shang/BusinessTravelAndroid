package com.business.travel.app.ui.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentDashboardBinding;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.utils.AnimalUtil;
import com.business.travel.utils.JacksonUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class DashboardFragment extends BaseFragment<FragmentDashboardBinding, DashBoardSharedData> {

	private BillDao billDao;
	private ProjectDao projectDao;
	private FloatingActionButton floatingActionButton;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
		billDao = AppDatabase.getInstance(this.getContext()).billDao();
		projectDao = AppDatabase.getInstance(this.getContext()).projectDao();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		//FIXME 页面启动后展示最新一条修改过的项目对应的账单列表
		Project project = projectDao.selectLatestModify();
		if (project != null) {
			dataBinding.setProject(project);
		}
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

		List<Bill> bills = billDao.selectByProjectId(project.getId());

		LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
		viewBinding.recyclerView.setLayoutManager(layoutManager);
		viewBinding.recyclerView.setAdapter(new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_project_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				Bill bill = bills.get(position);
				TextView textView1 = holder.itemView.findViewById(R.id.UI_ProjectItem_TextView_ProjectName);
				textView1.setText(JacksonUtil.toString(bill));
				textView1.setTextSize(10);
			}

			@Override
			public int getItemCount() {
				return bills.size();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		AnimalUtil.reset(floatingActionButton);
	}
}