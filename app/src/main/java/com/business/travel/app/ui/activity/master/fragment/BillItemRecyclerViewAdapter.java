package com.business.travel.app.ui.activity.master.fragment;

import java.util.List;
import java.util.stream.Collectors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.ui.activity.bill.DetailBillActivity;
import com.business.travel.app.ui.activity.master.fragment.BillItemRecyclerViewAdapter.BillItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.utils.SplitUtil;
import com.lxj.xpopup.XPopup;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillItemRecyclerViewAdapterViewHolder, Bill> {

	private final BillService billService;
	private final ConsumptionService consumptionService;

	public BillItemRecyclerViewAdapter(List<Bill> bills, BaseActivity<? extends ViewBinding> baseActivity) {
		super(bills, baseActivity);
		billService = new BillService(activity.getApplicationContext());
		consumptionService = new ConsumptionService(activity.getApplicationContext());
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

		ImageLoadUtil.loadImageToView(bill.getIconDownloadUrl(), holder.iconImageView);

		String name = bill.getConsumptionIds();
		if (StringUtils.isNotBlank(name)) {
			List<Long> ids = SplitUtil.trimToLongList(name);
			List<Consumption> consumptions = consumptionService.queryByIds(ids);
			String names = consumptions.stream().map(Consumption::getName).collect(Collectors.joining(","));
			holder.consumptionItemTextView.setText(names);
		}

		Long amount = bill.getAmount();
		String type = bill.getConsumptionType();
		String amountText = "";
		if (ConsumptionTypeEnum.INCOME.name().equals(type)) {
			amountText = "" + (double)amount / 100;
		} else if (ConsumptionTypeEnum.SPENDING.name().equals(type)) {
			amountText = "-" + (double)amount / 100;
		}
		holder.amountTextView.setText(amountText);

		holder.cardView.setOnClickListener(v -> {
			Intent intent = new Intent(activity, DetailBillActivity.class);
			intent.putExtra("selectBillId", bill.getId());
			activity.startActivity(intent);
		});

		holder.cardView.setOnLongClickListener(v -> {
			new XPopup.Builder(activity)
					// 依附于所点击的View，内部会自动判断在上方或者下方显示
					.atView(holder.iconImageView)
					.asAttachList(new String[] {"删除", "编辑"}, new int[] {R.drawable.ic_base_delete, R.drawable.ic_base_green_edit}, (pos, text) -> {
						if (pos == 0) {
							onDelete(position, bill);
						} else if (pos == 1) {
							//编辑
							onEdit(bill);
						}
					})
					.show();
			return true;
		});
	}

	private void onEdit(Bill bill) {
	}

	private void onDelete(int position, Bill bill) {
		new XPopup.Builder(activity).asConfirm("", "是否要删除该账目", () -> {
			billService.deleteBillById(bill.getId());
			dataList.remove(position);
			this.notifyDataSetChanged();
			if (CollectionUtils.isEmpty(dataList)) {
				((BillFragment)MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment()).refresh(bill.getProjectId());
			}
		}).show();
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
		@BindView(R.id.card)
		public CardView cardView;

		public BillItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
