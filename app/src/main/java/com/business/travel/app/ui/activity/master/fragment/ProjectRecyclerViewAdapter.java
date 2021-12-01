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
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.fragment.ProjectRecyclerViewAdapter.ProjectAdapterHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ProjectRecyclerViewAdapter extends BaseRecyclerViewAdapter<ProjectAdapterHolder, Project> {

	public ProjectService projectService;
	public BillService billService;

	public ProjectRecyclerViewAdapter(List<Project> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dataList, baseActivity);
	}

	@Override
	protected void inject() {
		projectService = new ProjectService(activity.getApplicationContext());
		billService = new BillService(activity.getApplicationContext());
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
			this.notifyDataSetChanged();
			return;
		}
		Project project = dataList.get(position);
		if (project == null) {
			dataList.remove(position);
			this.notifyDataSetChanged();
			return;
		}
		//项目的起止时间
		String productTime = project.getProductTime();
		holder.dateTextView.setText(productTime);

		//项目总支出
		final Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(project.getId());
		//项目总收入
		final Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(project.getId());

		//显示收入和支出的金额
		showSpendingIncomeTag(holder, sumTotalSpendingMoney, sumTotalIncomeMoney);

		holder.projectNameTextView.setText(project.getName());

		//点击项目的时候跳转到账单页面，并把项目id传递过去
		holder.cardView.setOnClickListener(v -> goToBillFragment(project));
		//初始化 长按时候 的弹窗事件
		AttachListPopupView attachListPopupView = initAttachListPopView(holder, position, project);
		//长按项目的时候弹出操作框
		holder.cardView.setOnLongClickListener(v -> {
			attachListPopupView.show();
			return true;
		});

	}

	private AttachListPopupView initAttachListPopView(@NotNull ProjectAdapterHolder holder, int position, Project project) {
		//确认删除弹框
		ConfirmPopupView confirmPopupView = new Builder(activity).asConfirm("是否要删除", project.getName(), () -> confirmDeleteProject(position, project));
		//删除、编辑弹框
		return new Builder(activity).atView(holder.projectNameTextView).asAttachList(new String[] {"删除", "编辑"}, new int[] {R.drawable.ic_base_delete, R.drawable.ic_base_green_edit}, (pos, text) -> {
			//当点击删除的时候
			if (pos == 0) {
				//弹出删除确认框
				confirmPopupView.show();
				return;
			}
			//当点击编辑的时候
			if (pos == 1) {
				//编辑 todo
				return;
			}
		});
	}

	/**
	 * 确认删除项目
	 *
	 * @param position
	 * @param project
	 */
	private void confirmDeleteProject(int position, Project project) {
		//先软删除项目和项目下面的账单
		projectService.softDeleteProjectWithBill(project.getId());
		//移除元素并通知UI更新
		dataList.remove(position);
		this.notifyDataSetChanged();
		//如果移除完了,没有列表数据了,此时需要刷新viewHead了
		if (CollectionUtils.isEmpty(dataList)) {
			((ProjectFragment)MasterFragmentPositionEnum.PROJECT_FRAGMENT.getFragment()).refreshProjectList();
		}
	}

	private void goToBillFragment(Project project) {
		ViewPager2 viewPager2 = activity.findViewById(R.id.UI_MasterActivity_ViewPager2);
		viewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());

		//把选中的数据传递给 BillFragment 页面
		BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
		billFragment.setSelectedProjectId(project.getId());
	}

	@SuppressLint("SetTextI18n")
	private void showSpendingIncomeTag(@NotNull ProjectAdapterHolder holder, Long sumTotalSpendingMoney, Long sumTotalIncomeMoney) {
		final boolean noSumTotalSpendingMoney = sumTotalSpendingMoney == null || sumTotalSpendingMoney == 0;
		final boolean noSumTotalIncomeMoney = sumTotalIncomeMoney == null || sumTotalIncomeMoney == 0;
		if (noSumTotalSpendingMoney && noSumTotalIncomeMoney) {
			holder.payTextView.setText("支出: 0");
			holder.incomeTextView.setText("收入: 0");
			return;
		}
		if (noSumTotalSpendingMoney) {
			holder.payTextView.setVisibility(View.GONE);
		} else {
			holder.payTextView.setText("支出: " + (double)sumTotalSpendingMoney / 100);
		}

		if (noSumTotalIncomeMoney) {
			holder.incomeTextView.setVisibility(View.GONE);
		} else {
			holder.incomeTextView.setText("收入: " + (double)sumTotalIncomeMoney / 100);
		}
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
