package com.business.travel.app.ui.fragment;

import java.util.List;

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
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.ui.base.ShareData;
import com.business.travel.app.ui.fragment.ProjectAdapter.ProjectAdapterHolder;
import com.business.travel.utils.JacksonUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ProjectAdapter extends BaseRecyclerViewAdapter<ProjectAdapterHolder, Project> {

	public ProjectAdapter(List<Project> dataList, BaseActivity<? extends ViewBinding, ShareData> baseActivity) {
		super(dataList, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public ProjectAdapterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item, parent, false);
		return new ProjectAdapterHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ProjectAdapterHolder holder, int position) {
		Project project = dataList.get(position);
		holder.textViewName.setText(project.getName());
		holder.cardView.setOnClickListener(v -> {
			ToastUtils.showShort(JacksonUtil.toString(project));
			ViewPager2 viewPager2 = activity.findViewById(R.id.viewPager);
			viewPager2.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());
			DashboardFragment dashboardFragment = (DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment();
			DashBoardSharedData sharedData = dashboardFragment.getDataBinding();
			sharedData.setProject(project);
		});
	}

	class ProjectAdapterHolder extends ViewHolder {
		@BindView(R.id.text_view_name)
		public TextView textViewName;
		@BindView(R.id.TextView_time)
		public TextView statementShow;
		@BindView(R.id.card_view)
		public CardView cardView;

		public ProjectAdapterHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
