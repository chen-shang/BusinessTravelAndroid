package com.business.travel.app.ui.activity.item;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ItemIconInfo;
import com.business.travel.app.model.ItemIconInfo.ImageIconInfo;
import com.business.travel.app.ui.activity.item.AddConsumptionItemRecyclerViewAdapter.AddConsumptionItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

public class AddConsumptionItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<AddConsumptionItemRecyclerViewAdapterViewHolder, ItemIconInfo> {

	public AddConsumptionItemRecyclerViewAdapter(List<ItemIconInfo> itemIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(itemIconInfos, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public AddConsumptionItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_consumer_icon_item, parent, false);
		return new AddConsumptionItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddConsumptionItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		final ItemIconInfo itemIconInfo = dataList.get(position);
		final String path = itemIconInfo.getPath();
		final TextView textView = holder.itemView.findViewById(R.id.path);
		textView.setText(path);

		final List<ImageIconInfo> iconDownloadUrl = itemIconInfo.getImageIconInfos();
		final SwipeRecyclerView swipeRecyclerView = holder.itemView.findViewById(R.id.SwipeRecyclerView2);
		LayoutManager layoutManager = new GridLayoutManager(activity, 5);
		swipeRecyclerView.setLayoutManager(layoutManager);
		AddConsumptionItemIconRecyclerViewAdapter billRecyclerViewAdapter = new AddConsumptionItemIconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, iconDownloadUrl, activity);
		swipeRecyclerView.setAdapter(billRecyclerViewAdapter);
	}

	static class AddConsumptionItemRecyclerViewAdapterViewHolder extends ViewHolder {

		public AddConsumptionItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
