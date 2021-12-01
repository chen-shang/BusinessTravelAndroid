package com.business.travel.app.ui.activity.master.fragment;

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
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.fragment.ProjectRecyclerViewAdapter.ProjectAdapterHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.lxj.xpopup.XPopup;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ProjectRecyclerViewAdapter extends BaseRecyclerViewAdapter<ProjectAdapterHolder, Project> {

	public final ProjectService projectService = new ProjectService(activity.getApplicationContext());

	public ProjectRecyclerViewAdapter(List<Project> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dataList, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public ProjectAdapterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_project_recyclerview, parent, false);
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
		String productTime = project.getProductTime();
		holder.dateTextView.setText(productTime);

		BillDao billDao = AppDatabase.getInstance(holder.itemView.getContext()).billDao();
		final Long sumTotalSpendingMoney = billDao.sumTotalSpendingMoney(project.getId());
		final Long sumTotalIncomeMoney = billDao.sumTotalIncomeMoney(project.getId());

		if ((sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0) && (sumTotalIncomeMoney == null || sumTotalIncomeMoney == 0)) {
			holder.payTextView.setText("支出: 0");
			holder.incomeTextView.setText("收入: 0");
		} else {
			if (sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0) {
				holder.payTextView.setVisibility(View.GONE);
			} else {
				holder.payTextView.setText("支出: " + (double)sumTotalSpendingMoney / 100);
			}

			if (sumTotalIncomeMoney == null || sumTotalIncomeMoney == 0) {
				holder.incomeTextView.setVisibility(View.GONE);
			} else {
				holder.incomeTextView.setText("收入: " + (double)sumTotalIncomeMoney / 100);
			}
		}

		holder.projectNameTextView.setText(project.getName());
		holder.cardView.setOnClickListener(v -> {
			ViewPager2 viewPager2 = activity.findViewById(R.id.UI_MasterActivity_ViewPager2);
			viewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());

			//把选中的数据传递给 BillFragment 页面
			BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
			billFragment.setSelectedProjectId(project.getId());
		});

		holder.cardView.setOnLongClickListener(v -> {
			// 依附于所点击的View，内部会自动判断在上方或者下方显示
			new XPopup.Builder(activity).atView(holder.projectNameTextView).asAttachList(new String[] {"删除", "编辑"}, new int[] {R.drawable.ic_base_delete, R.drawable.ic_base_green_edit}, (pos, text) -> {
						if (pos == 0) {
							onDelete(position, project);
						} else if (pos == 1) {
							//编辑 todo
						}
					})
					.show();
			return true;
		});

	}

	private void onDelete(int position, Project project) {

		String content = "是否要删除" + project.getName();
		new XPopup.Builder(activity).asConfirm("", content, () -> {
			projectService.softDeleteProjectWithBill(project.getId());
			dataList.remove(position);
			this.notifyDataSetChanged();
			if (CollectionUtils.isEmpty(dataList)) {
				((ProjectFragment)MasterFragmentPositionEnum.PROJECT_FRAGMENT.getFragment()).refreshProjectList();
			}
		}).show();
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
