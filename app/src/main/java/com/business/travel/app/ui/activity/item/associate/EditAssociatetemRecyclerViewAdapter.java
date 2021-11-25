package com.business.travel.app.ui.activity.item.associate;

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
import com.business.travel.app.dal.entity.AssociateItem;
import com.business.travel.app.ui.activity.item.associate.EditAssociatetemRecyclerViewAdapter.EditAssociatetemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.LoadImageUtil;
import org.jetbrains.annotations.NotNull;

public class EditAssociatetemRecyclerViewAdapter extends BaseRecyclerViewAdapter<EditAssociatetemRecyclerViewAdapterViewHolder, AssociateItem> {

	public EditAssociatetemRecyclerViewAdapter(List<AssociateItem> associateItems, BaseActivity<? extends ViewBinding> baseActivity) {
		super(associateItems, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public EditAssociatetemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_item, parent, false);
		return new EditAssociatetemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull EditAssociatetemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		final AssociateItem associateItem = dataList.get(position);
		CompletableFuture.runAsync(() -> {
			LoadImageUtil.loadImageToView(associateItem.getIconDownloadUrl(), holder.imageView);
		});
		holder.textView.setText(associateItem.getName());
	}

	static class EditAssociatetemRecyclerViewAdapterViewHolder extends ViewHolder {
		@BindView(R.id.bill_icon)
		ImageView imageView;
		@BindView(R.id.bill_info)
		TextView textView;

		public EditAssociatetemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
