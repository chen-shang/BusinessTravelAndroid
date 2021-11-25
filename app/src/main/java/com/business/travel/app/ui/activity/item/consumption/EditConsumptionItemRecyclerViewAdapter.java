package com.business.travel.app.ui.activity.item.consumption;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.ui.activity.item.consumption.EditConsumptionItemRecyclerViewAdapter.EditConsumptionItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.LoadImageUtil;
import org.jetbrains.annotations.NotNull;

public class EditConsumptionItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<EditConsumptionItemRecyclerViewAdapterViewHolder, ConsumptionItem> {

	public EditConsumptionItemRecyclerViewAdapter(List<ConsumptionItem> consumptionItems, BaseActivity<? extends ViewBinding> baseActivity) {
		super(consumptionItems, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public EditConsumptionItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_item, parent, false);
		return new EditConsumptionItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull EditConsumptionItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		final ConsumptionItem consumptionItem = dataList.get(position);
		CompletableFuture.runAsync(() -> LoadImageUtil.loadImageToView(consumptionItem.getIconDownloadUrl(), holder.imageView));
		holder.textView.setText(consumptionItem.getName());
	}

	static class EditConsumptionItemRecyclerViewAdapterViewHolder extends ViewHolder {
		@BindView(R.id.bill_icon)
		ImageView imageView;
		@BindView(R.id.bill_info)
		TextView textView;

		public EditConsumptionItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
