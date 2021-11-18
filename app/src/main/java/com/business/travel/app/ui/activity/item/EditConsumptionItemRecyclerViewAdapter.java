package com.business.travel.app.ui.activity.item;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.ButterKnife;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.ui.activity.item.EditConsumptionItemRecyclerViewAdapter.EditConsumptionItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import org.jetbrains.annotations.NotNull;

public class EditConsumptionItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<EditConsumptionItemRecyclerViewAdapterViewHolder, ConsumptionItem> {

	public EditConsumptionItemRecyclerViewAdapter(List<ConsumptionItem> consumptionItems, BaseActivity<? extends ViewBinding> baseActivity) {
		super(consumptionItems, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public EditConsumptionItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_consumer_item, parent, false);
		return new EditConsumptionItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull EditConsumptionItemRecyclerViewAdapterViewHolder holder, int position) {

	}

	static class EditConsumptionItemRecyclerViewAdapterViewHolder extends ViewHolder {

		public EditConsumptionItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
