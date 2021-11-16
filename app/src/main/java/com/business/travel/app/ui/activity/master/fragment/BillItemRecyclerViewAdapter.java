package com.business.travel.app.ui.activity.master.fragment;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.ButterKnife;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.ui.activity.master.fragment.BillItemRecyclerViewAdapter.BillItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
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

	}

	static class BillItemRecyclerViewAdapterViewHolder extends ViewHolder {

		public BillItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
