package com.business.travel.app.ui.activity.bill.fragment;

import java.util.List;
import java.util.Optional;

import android.annotation.SuppressLint;
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
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.enums.WeekEnum;
import com.business.travel.app.model.DateBillInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.ui.activity.bill.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.utils.DateTimeUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, DateBillInfo> {

	private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
	private BillService billService;

	public BillRecyclerViewAdapter(List<DateBillInfo> dateBillInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dateBillInfos, baseActivity);
	}

	@Override
	protected void inject() {
		billService = new BillService(activity);
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

		DateBillInfo dateBillInfo = dataList.get(position);
		Long date = dateBillInfo.getDate();
		holder.consumerDateTextView.setText(DateTimeUtil.format(date, "yyyy-MM-dd"));
		int code = DateTimeUtil.toLocalDateTime(date).getDayOfWeek().getValue();
		WeekEnum weekEnum = WeekEnum.ofCode(code);
		holder.consumerWeekTextView.setText(weekEnum.getMsg());

		//后面可以改成查询数据库
		Long selectedProjectId = billFragment.getSelectedProjectId();
		List<Bill> billList = dateBillInfo.getBillList();
		if (CollectionUtils.isEmpty(billList)) {
			dataList.remove(position);
			this.notifyDataSetChanged();
			return;
		}

		//刷新金额显示
		refreshMoneyShow(holder, selectedProjectId, date);

		//接下来是当天的账单列表显示
		holder.billItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
		final BillItemRecyclerViewAdapter adapter = new BillItemRecyclerViewAdapter(billList, activity, holder);
		holder.billItemSwipeRecyclerView.setAdapter(adapter);
	}

	public void refreshMoneyShow(BillRecyclerViewAdapterViewHolder viewHolder, Long projectId, Long date) {
		//统计一下总收入
		Long sumTotalIncomeMoney = billService.sumTotalIncomeMoney(projectId, date);
		viewHolder.incomeTextView.setVisibility(sumTotalIncomeMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalIncomeMoney).ifPresent(money -> viewHolder.incomeTextView.setText("收入:" + (double)money / 100));

		//统计一下总支出
		Long sumTotalSpendingMoney = billService.sumTotalSpendingMoney(projectId, date);
		viewHolder.payTextView.setVisibility(sumTotalSpendingMoney == null ? View.GONE : View.VISIBLE);
		Optional.ofNullable(sumTotalSpendingMoney).ifPresent(money -> viewHolder.payTextView.setText("支出:" + (double)money / 100));
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
