package com.business.travel.app.ui.activity.master.fragment;

import java.util.List;
import java.util.Optional;

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
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.activity.master.fragment.ProjectRecyclerViewAdapter.ProjectAdapterHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
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
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		Project project = dataList.get(position);
		if (project == null) {
			return;
		}
		String productTime = getProductTime(project);
		holder.dateTextView.setText(productTime);

		BillDao billDao = AppDatabase.getInstance(holder.itemView.getContext()).billDao();
		final Long sumTotalMoney = billDao.sumTotalSpendingMoney(project.getId());
		if (sumTotalMoney == null || sumTotalMoney == 0) {
			holder.payTextView.setVisibility(View.GONE);
		} else {
			holder.payTextView.setText("支出:" + sumTotalMoney);
		}

		final Long sumTotalSpendingMoney = billDao.sumTotalIncomeMoney(project.getId());
		if (sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0) {
			holder.incomeTextView.setVisibility(View.GONE);
		} else {
			holder.incomeTextView.setText("收入:" + sumTotalSpendingMoney);
		}

		holder.projectNameTextView.setText(project.getName());
		holder.cardView.setOnClickListener(v -> {
			ViewPager2 viewPager2 = activity.findViewById(R.id.UI_MasterActivity_ViewPager2);
			viewPager2.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());

			//把选中的数据传递给 BillFragment 页面
			BillFragment billFragment = MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment();
			BillFragmentShareData billFragmentShareData = billFragment.getDataBinding();
			billFragmentShareData.setProject(project);
		});

		holder.cardView.setOnLongClickListener(v -> {
			LogToast.infoShow(JacksonUtil.toPrettyString(project));
			//// TODO: 2021/11/7 弹出对话框
			return true;
		});

	}

	@NotNull
	private String getProductTime(Project project) {
		String startTime = Optional.ofNullable(project.getStartTime())
				.filter(StringUtils::isNotBlank)
				.map(DateTimeUtil::parseDate)
				.map(datetime -> DateTimeUtil.format(datetime, "MM月dd日"))
				.orElse("");

		String endTime = Optional.ofNullable(project.getEndTime())
				.filter(StringUtils::isNotBlank)
				.map(DateTimeUtil::parseDate)
				.map(datetime -> DateTimeUtil.format(datetime, "MM月dd日"))
				.orElse("");
		String productTime = startTime;
		if (StringUtils.isNotBlank(endTime)) {
			productTime = productTime + " - " + endTime;
		}
		return productTime;
	}

	@SuppressLint("NonConstantResourceId")
	static class ProjectAdapterHolder extends ViewHolder {
		@BindView(R.id.UI_ProjectItem_TextView_ProjectName)
		public TextView projectNameTextView;
		@BindView(R.id.UI_ProjectItem_TextView_Date)
		public TextView dateTextView;
		@BindView(R.id.UI_ProjectItem_TextView_PAY)
		public TextView payTextView;
		@BindView(R.id.UI_ProjectItem_TextView_Income)
		public TextView incomeTextView;
		@BindView(R.id.UI_ProjectItem_CardView)
		public CardView cardView;

		public ProjectAdapterHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
