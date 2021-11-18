package com.business.travel.app.ui.activity.master.fragment;

import java.util.List;
import java.util.stream.Collectors;

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
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.DateBillInfo;
import com.business.travel.app.ui.activity.master.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, DateBillInfo> {

	public BillRecyclerViewAdapter(List<DateBillInfo> dateBillInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dateBillInfos, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public BillRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bill, parent, false);
		return new BillRecyclerViewAdapterViewHolder(view) {
		};
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindViewHolder(@NonNull @NotNull BillRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		BillFragmentShareData dataBinding = ((BillFragment)MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment()).getDataBinding();
		Project project = dataBinding.getProject();
		if (project == null) {
			return;
		}

		DateBillInfo dateBillInfo = dataList.get(position);
		String date = dateBillInfo.getDate();
		holder.consumerDateTextView.setText(date);
		List<Bill> billList = dateBillInfo.getBillList();
		if (CollectionUtils.isEmpty(billList)) {
			return;
		}
		billList.stream().collect(Collectors.groupingBy(Bill::getConsumptionType)).forEach((type, bills) -> {
			long sumTotalMoney = bills.stream().map(Bill::getAmount).reduce(Long::sum).orElse(0L);
			if (ConsumptionTypeEnum.INCOME.name().equals(type)) { //收入
				if (sumTotalMoney == 0) {
					holder.incomeTextView.setVisibility(View.GONE);
				} else {
					holder.incomeTextView.setVisibility(View.VISIBLE);
					holder.incomeTextView.setText("收入:" + sumTotalMoney);
				}

			}
			if (ConsumptionTypeEnum.SPENDING.name().equals(type)) {
				if (sumTotalMoney == 0) {
					holder.payTextView.setVisibility(View.GONE);
				} else {
					holder.incomeTextView.setVisibility(View.VISIBLE);
					holder.payTextView.setText("支出:" + sumTotalMoney);
				}
			}
		});

		holder.billItemSwipeRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
		holder.billItemSwipeRecyclerView.setAdapter(new BillItemRecyclerViewAdapter(billList, activity));
	}

	@SuppressLint("NonConstantResourceId")
	static class BillRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_BillFragment_BillAdapter_ConsumeDate)
		public TextView consumerDateTextView;
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
