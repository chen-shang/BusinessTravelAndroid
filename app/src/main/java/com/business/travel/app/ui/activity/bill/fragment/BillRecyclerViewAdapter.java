package com.business.travel.app.ui.activity.bill.fragment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.enums.OperateTypeEnum;
import com.business.travel.app.model.BillAddModel;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.bill.AddBillActivity;
import com.business.travel.app.ui.activity.bill.AddBillActivity.IntentKey;
import com.business.travel.app.ui.activity.bill.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.WeekEnum;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, Long> {

	private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
	private BillService billService;
	private ProjectService projectService;

	public BillRecyclerViewAdapter(List<Long> dateList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dateList, baseActivity);
	}

	@Override
	protected void inject() {
		billService = new BillService(activity);
		projectService = new ProjectService(activity);
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
		//后面可以改成查询数据库
		Long selectedProjectId = billFragment.getSelectedProjectId();
		List<Bill> billList = billService.queryBillByProjectIdAndConsumeDate(selectedProjectId, date);
		if (CollectionUtils.isNotEmpty(billList)) {
			//接下来是当天的账单列表显示
			holder.billItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
			holder.billItemSwipeRecyclerView.setAdapter(new BillItemRecyclerViewAdapter(billList, activity, holder));
		}

		LocalDateTime localDateTime = DateTimeUtil.toLocalDateTime(date);
		holder.consumerDateTextView.setText(DateTimeUtil.format(date, "yyyy-MM-dd"));

		int code = localDateTime.getDayOfWeek().getValue();
		WeekEnum weekEnum = WeekEnum.ofCode(code);
		holder.consumerWeekTextView.setText(weekEnum.getMsg());

		Intent intent = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.SPENDING);
		//时间点击跳转新增页面
		holder.consumerDateTextView.setOnClickListener(v -> {
			//如果是DASHBOARD_FRAGMENT页面,当点击的时候则跳转到新增账单页面
			activity.startActivity(intent);
		});

		//时间点击跳转新增页面
		Intent intent1 = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.INCOME);
		holder.incomeTextView.setOnClickListener(v -> {
			//如果是DASHBOARD_FRAGMENT页面,当点击的时候则跳转到新增账单页面
			activity.startActivity(intent1);
		});

		//时间点击跳转新增页面
		Intent intent2 = genIntent(selectedProjectId, localDateTime, ConsumptionTypeEnum.SPENDING);
		holder.payTextView.setOnClickListener(v -> {
			//如果是DASHBOARD_FRAGMENT页面,当点击的时候则跳转到新增账单页面
			activity.startActivity(intent2);
		});

		//刷新金额显示
		refreshMoneyShow(holder, selectedProjectId, date);
	}

	@NotNull
	private Intent genIntent(Long selectedProjectId, LocalDateTime localDateTime, ConsumptionTypeEnum consumptionTypeEnum) {
		Intent intent = new Intent(activity, AddBillActivity.class);
		Project project = projectService.queryById(selectedProjectId);
		String name = Optional.ofNullable(project).map(Project::getName).orElse(null);
		BillAddModel billAddModel = new BillAddModel(name, DateTimeUtil.timestamp(localDateTime), consumptionTypeEnum.name());
		intent.putExtra(IntentKey.billAddModel, JacksonUtil.toString(billAddModel));
		return intent;
	}

	public void refreshMoneyShow(BillRecyclerViewAdapterViewHolder viewHolder, Long projectId, Long date) {
		//统计一下总收入
		Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(projectId, date);
		viewHolder.incomeTextView.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalIncomeMoney).ifPresent(money -> viewHolder.incomeTextView.setText(String.format("收入:%s", MoneyUtil.toYuanString(money))));

		//统计一下总支出
		Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(projectId, date);
		viewHolder.payTextView.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalSpendingMoney).ifPresent(money -> viewHolder.payTextView.setText(String.format("支出:%s", MoneyUtil.toYuanString(money))));
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
