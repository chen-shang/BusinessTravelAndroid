package com.business.travel.app.ui.activity.master.fragment;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.business.travel.utils.DateTimeUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

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
		viewBinding.UIDashboardFragmentSwipeRecyclerViewBillList.setAdapter(new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_dashboard_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				String datetime = dateList.get(position);
				TextView textView1 = holder.itemView.findViewById(R.id.UI_BillItem_TextView_Date);
				DayOfWeek dayOfWeek = DateTimeUtil.parseLocalDateTime(datetime, "yyyy-MM-dd").getDayOfWeek();
				textView1.setText(datetime + " " + dayOfWeek);
				TextView textView2 = holder.itemView.findViewById(R.id.UI_BillItem_TextView_PAY);
				SwipeRecyclerView recyclerView = holder.itemView.findViewById(R.id.UI_BillItem_SwipeRecyclerView_List);
				LayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
				recyclerView.setLayoutManager(layoutManager2);
				List<Bill> bills = billDao.selectByProjectIdAndConsumeDate(project.getId(), datetime);
				Adapter adapter = new Adapter() {
					@NonNull
					@NotNull
					@Override
					public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
						View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bill_item, parent, false);
						return new ViewHolder(view) {
						};
					}

					@Override
					public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
						ImageView billIcon = holder.itemView.findViewById(R.id.bill_icon);
						TextView textView = holder.itemView.findViewById(R.id.bill_info);
						TextView bill_info2 = holder.itemView.findViewById(R.id.bill_info2);
						Bill bill = bills.get(position);
						billIcon.setImageResource(R.drawable.bill_icon_daily);
						textView.setText(bill.getName());
						//根据同行人ID转换成名字 TODO
						bill_info2.setText(bill.getAssociateId());
						TextView bill_info3 = holder.itemView.findViewById(R.id.bill_info3);
						bill_info3.setText(String.valueOf(bill.getAmount()));
					}

					@Override
					public int getItemCount() {
						return bills.size();
					}
				};
				recyclerView.setAdapter(adapter);
			}

			@Override
			public int getItemCount() {
				return dateList.size();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		AnimalUtil.reset(floatingActionButton);
	}
}