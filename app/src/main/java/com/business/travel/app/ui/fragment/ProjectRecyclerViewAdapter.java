package com.business.travel.app.ui.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.ui.fragment.ProjectRecyclerViewAdapter.ProjectAdapterHolder;
import com.business.travel.utils.DateTimeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ProjectRecyclerViewAdapter extends BaseRecyclerViewAdapter<ProjectAdapterHolder, Project> {

	public ProjectRecyclerViewAdapter(List<Project> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dataList, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public ProjectAdapterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_project_item, parent, false);
		return new ProjectAdapterHolder(view);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindViewHolder(@NonNull @NotNull ProjectAdapterHolder holder, int position) {
		Project project = dataList.get(position);
		if (project == null) {
			ToastUtils.make().setLeftIcon(R.mipmap.ic_error).show("项目为空");
			return;
		}
		String startTime = DateTimeUtil.format(DateTimeUtil.parseDate(project.getStartTime()), "MM月dd日");
		String endTime = DateTimeUtil.format(DateTimeUtil.parseDate(project.getEndTime()), "MM月dd日");
		holder.dateTextView.setText(startTime + "-" + endTime);

		BillDao billDao = AppDatabase.getInstance(holder.itemView.getContext()).billDao();
		Long sumTotalMoney = billDao.sumTotalMoney(project.getId());
		holder.payTextView.setText("支出:" + sumTotalMoney);
		holder.projectNameTextView.setText(project.getName());

		holder.cardView.setOnClickListener(v -> {
			ViewPager2 viewPager2 = activity.findViewById(R.id.viewPager);
			viewPager2.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());
			DashboardFragment dashboardFragment = (DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment();
			DashBoardSharedData sharedData = dashboardFragment.getDataBinding();
			sharedData.setProject(project);
		});
	}

	class ProjectAdapterHolder extends ViewHolder {
		@BindView(R.id.UI_ProjectItem_TextView_ProjectName)
		public TextView projectNameTextView;
		@BindView(R.id.UI_ProjectItem_TextView_Date)
		public TextView dateTextView;
		@BindView(R.id.UI_ProjectItem_TextView_PAY)
		public TextView payTextView;
		@BindView(R.id.UI_ProjectItem_CardView)
		public CardView cardView;

		public ProjectAdapterHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
