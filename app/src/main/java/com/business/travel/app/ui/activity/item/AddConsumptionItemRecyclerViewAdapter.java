package com.business.travel.app.ui.activity.item;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
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
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_consumption_icon_item, parent, false);
		return new AddConsumptionItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddConsumptionItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		final ItemIconInfo itemIconInfo = dataList.get(position);
		if (itemIconInfo == null) {
			return;
		}
		final String path = itemIconInfo.getPath();
		holder.iconPathTextView.setText(path);
		final List<ImageIconInfo> imageIconInfoList = itemIconInfo.getImageIconInfos();
		if (CollectionUtils.isEmpty(imageIconInfoList)) {
			return;
		}

		//接下来是对icon的处理
		LayoutManager layoutManager = new GridLayoutManager(activity, 5);
		holder.imageIconInfoRecyclerView.setLayoutManager(layoutManager);
		AddConsumptionItemIconRecyclerViewAdapter billRecyclerViewAdapter = new AddConsumptionItemIconRecyclerViewAdapter(imageIconInfoList, activity);
		holder.imageIconInfoRecyclerView.setAdapter(billRecyclerViewAdapter);
	}

	@SuppressLint("NonConstantResourceId")
	static class AddConsumptionItemRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_AddConsumptionItem_TextView_IconPath)
		public TextView iconPathTextView;
		@BindView(R.id.UI_AddConsumptionItem_SwipeRecyclerView)
		public SwipeRecyclerView imageIconInfoRecyclerView;

		public AddConsumptionItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
