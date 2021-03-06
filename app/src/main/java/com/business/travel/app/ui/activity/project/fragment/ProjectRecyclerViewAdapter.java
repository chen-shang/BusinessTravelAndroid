package com.business.travel.app.ui.activity.project.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.bill.fragment.BillFragment;
import com.business.travel.app.ui.activity.project.EditProjectActivity;
import com.business.travel.app.ui.activity.project.fragment.ProjectRecyclerViewAdapter.ProjectAdapterHolder;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.utils.DateTimeUtil;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * @author chenshang
 */
public class ProjectRecyclerViewAdapter extends BaseRecyclerViewAdapter<ProjectAdapterHolder, Project> {

    private final ProjectFragment projectFragment = MasterFragmentPositionEnum.PROJECT_FRAGMENT.getFragment();
    private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();

    public ProjectService projectService;
    public BillService billService;

    public ProjectRecyclerViewAdapter(List<Project> projects, Context context) {
        super(projects, context);
    }

    @Override
    protected void inject() {
        projectService = new ProjectService(context);
        billService = new BillService(context);
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
        //?????????????????????
        String productTime = project.getProductTime();
        holder.dateTextView.setText(productTime);

        //?????????????????????
        final Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(project.getId());
        holder.incomeTextView.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
        Optional.ofNullable(sumTotalIncomeMoney).ifPresent(money -> holder.incomeTextView.setText("??????:" + MoneyUtil.toYuanString(money)));

        //?????????????????????
        final Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(project.getId());
        holder.payTextView.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
        Optional.ofNullable(sumTotalSpendingMoney).ifPresent(money -> holder.payTextView.setText("??????:" + MoneyUtil.toYuanString(money)));

        holder.projectNameTextView.setText(project.getName());

        //?????????????????????????????????????????????????????????id????????????
        holder.cardView.setOnClickListener(v -> {
            //??????????????????
            project.setModifyTime(DateTimeUtil.timestamp());
            projectService.updateProject(project.getId(), project);
            goToBillFragment(project);
        });
        //????????? ???????????? ???????????????
        AttachListPopupView attachListPopupView = initAttachListPopView(holder, position, project);
        //????????????????????????????????????
        holder.cardView.setOnLongClickListener(v -> {
            attachListPopupView.show();
            return true;
        });
    }

    private AttachListPopupView initAttachListPopView(@NotNull ProjectAdapterHolder holder, int position, Project project) {
        //?????????????????????
        return new Builder(context).watchView(holder.cardView).asAttachList(new String[]{"??????", "??????"}, new int[]{R.drawable.ic_base_delete, R.drawable.ic_base_edit}, (pos, text) -> {
            switch (pos) {
                case 0:
                    delete(position, project);
                    break;
                case 1:
                    edit(position, project);
                    break;
                default:
                    //do nothing
            }
        });
    }


    /**
     * ????????????????????????
     *
     * @param position
     * @param project
     */
    private void edit(int position, Project project) {
        final Intent intent = new Intent(context, EditProjectActivity.class);
        intent.putExtra(EditProjectActivity.IntentKey.PROJECT_ID, project.getId());
        context.startActivity(intent);
    }

    /**
     * ???????????????????????????
     *
     * @param position
     * @param project
     */
    private void delete(int position, Project project) {
        //??????????????????
        new Builder(context).asConfirm("???????????????", project.getName(), () -> confirmDelete(position, project)).show();
    }

    /**
     * ??????????????????
     *
     * @param position
     * @param project
     */
    private void confirmDelete(int position, Project project) {
        //??????????????????????????????????????????
        projectService.softDeleteProjectWithBill(project.getId());
        //?????????????????????UI??????
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size() - position);
        //??????????????????,?????????????????????,??????????????????viewHead???
        if (CollectionUtils.isEmpty(dataList)) {
            projectFragment.refreshProjectList();
        }
        projectFragment.refreshProjectHeader();
    }

    private void goToBillFragment(Project project) {
        //??????????????????????????? BillFragment ??????
        LogUtils.i("?????? project:" + project.getId() + ":" + project.getName() + "????????????????????????");
        billFragment.setSelectedProjectId(project.getId());

        ViewPager2 viewPager2 = ((Activity) context).findViewById(R.id.UI_MasterActivity_ViewPager2);
        viewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
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
