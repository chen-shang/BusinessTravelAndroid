package com.business.travel.app.ui.activity.bill.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.BillAddModel;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.bill.AddBillActivity;
import com.business.travel.app.ui.activity.bill.AddBillActivity.IntentKey;
import com.business.travel.app.ui.activity.bill.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.WeekEnum;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, Long> {

    private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
    private BillService billService;
    private ProjectService projectService;

    public BillRecyclerViewAdapter(List<Long> dataList, Context context) {
        super(dataList, context);
    }

    @Override
    protected void inject() {
        billService = new BillService(context);
        projectService = new ProjectService(context);
    }

    @NonNull
    @NotNull
    @Override
    public BillRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bill_recyclerview, parent, false);
        return new BillRecyclerViewAdapterViewHolder(view) {
        };
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull BillRecyclerViewAdapterViewHolder holder, int position) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        Long date = dataList.get(position);
        //?????????????????????????????????
        Long selectedProjectId = billFragment.getSelectedProjectId();
        List<Bill> billList = billService.queryBillByProjectIdAndConsumeDate(selectedProjectId, date);
        if (CollectionUtils.isNotEmpty(billList)) {
            //???????????????????????????????????????
            holder.billItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            holder.billItemSwipeRecyclerView.setAdapter(new BillItemRecyclerViewAdapter(billList, context, holder));
        }

        LocalDateTime localDateTime = DateTimeUtil.toLocalDateTime(date);
        holder.consumerDateTextView.setText(DateTimeUtil.format(date, "yyyy-MM-dd"));

        int code = localDateTime.getDayOfWeek().getValue();
        WeekEnum weekEnum = WeekEnum.ofCode(code);
        holder.consumerWeekTextView.setText(weekEnum.getMsg());

        Intent intent = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.SPENDING);
        //??????????????????????????????
        holder.consumerDateTextView.setOnClickListener(v -> {
            //?????????DASHBOARD_FRAGMENT??????,????????????????????????????????????????????????
            context.startActivity(intent);
        });

        //??????????????????????????????
        Intent intent1 = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.INCOME);
        holder.incomeTextView.setOnClickListener(v -> {
            //?????????DASHBOARD_FRAGMENT??????,????????????????????????????????????????????????
            context.startActivity(intent1);
        });

        //??????????????????????????????
        Intent intent2 = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.SPENDING);
        holder.payTextView.setOnClickListener(v -> {
            //?????????DASHBOARD_FRAGMENT??????,????????????????????????????????????????????????
            context.startActivity(intent2);
        });

        //??????????????????
        refreshMoneyShow(holder, selectedProjectId, date);
    }

    @NotNull
    private Intent genIntent(Long selectedProjectId, LocalDateTime localDateTime, ConsumptionTypeEnum consumptionTypeEnum) {
        Intent intent = new Intent(context, AddBillActivity.class);
        Project project = projectService.queryById(selectedProjectId);
        String name = Optional.ofNullable(project).map(Project::getName).orElse(null);
        BillAddModel billAddModel = new BillAddModel(name, DateTimeUtil.timestamp(localDateTime), consumptionTypeEnum.name());
        intent.putExtra(IntentKey.BILL_ADD_MODEL, JacksonUtil.toString(billAddModel));
        return intent;
    }

    public void refreshMoneyShow(BillRecyclerViewAdapterViewHolder viewHolder, Long projectId, Long date) {
        //?????????????????????
        Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(projectId, date);
        viewHolder.incomeTextView.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
        Optional.ofNullable(sumTotalIncomeMoney).ifPresent(money -> viewHolder.incomeTextView.setText(String.format("??????:%s", MoneyUtil.toYuanString(money))));

        //?????????????????????
        Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(projectId, date);
        viewHolder.payTextView.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
        Optional.ofNullable(sumTotalSpendingMoney).ifPresent(money -> viewHolder.payTextView.setText(String.format("??????:%s", MoneyUtil.toYuanString(money))));
    }

    @SuppressLint("NonConstantResourceId")
    static class BillRecyclerViewAdapterViewHolder extends ViewHolder {

        @BindView(R.id.UI_BillFragment_BillAdapter_ConsumeDate)
        public TextView consumerDateTextView;
        @BindView(R.id.UI_BillFragment_BillAdapter_ConsumeWeek)
        public TextView consumerWeekTextView;
        @BindView(R.id.UI_BillFragment_BillAdapter_TextView_Pay)
        public TextView payTextView;
        @BindView(R.id.UI_BillFragment_BillAdapter_TextView_Income)
        public TextView incomeTextView;
        @BindView(R.id.UI_BillFragment_BillAdapter_SwipeRecyclerView_BillItem)
        public SwipeRecyclerView billItemSwipeRecyclerView;

        public BillRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
