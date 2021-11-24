package com.business.travel.app.ui.activity.master.fragment;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.ui.activity.master.fragment.BillItemRecyclerViewAdapter.BillItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.FutureUtil;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillItemRecyclerViewAdapterViewHolder, Bill> {

	public BillItemRecyclerViewAdapter(List<Bill> bills, BaseActivity<? extends ViewBinding> baseActivity) {
		super(bills, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public BillItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bill_item, parent, false);
		return new BillItemRecyclerViewAdapterViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull BillItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		Bill bill = dataList.get(position);
		if (bill == null) {
			return;
		}

		String iconDownloadUrl = bill.getIconDownloadUrl();
		FutureUtil.runAsync(() -> {
			final InputStream iconInputStream = BusinessTravelResourceApi.getIcon(iconDownloadUrl);
			Sharp.loadInputStream(iconInputStream).into(holder.iconImageView);
		});

		String name = bill.getName();
		holder.consumptionItemTextView.setText(name);

		Long amount = bill.getAmount();
		String type = bill.getConsumptionType();
		String amountText = "";
		if (ConsumptionTypeEnum.INCOME.name().equals(type)) {
			amountText = "" + amount;
		} else if (ConsumptionTypeEnum.SPENDING.name().equals(type)) {
			amountText = "-" + amount;
		}
		holder.amountTextView.setText(amountText);
	}

	@SuppressLint("NonConstantResourceId")
	static class BillItemRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_BillFragment_BillItemAdapter_Icon)
		public ImageView iconImageView;
		@BindView(R.id.UI_BillFragment_BillItemAdapter_Amount)
		public TextView amountTextView;
		@BindView(R.id.UI_BillFragment_BillItemAdapter_Associate)
		public TextView associateTextView;
		@BindView(R.id.UI_BillFragment_BillItemAdapter_ConsumptionItem)
		public TextView consumptionItemTextView;

		public BillItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
