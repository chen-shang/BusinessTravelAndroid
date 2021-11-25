package com.business.travel.app.ui.activity.item;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter.EditItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.LoadImageUtil;
import org.jetbrains.annotations.NotNull;

public class EditItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<EditItemRecyclerViewAdapterViewHolder, ImageIconInfo> {

	public EditItemRecyclerViewAdapter(List<ImageIconInfo> imageIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(imageIconInfos, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public EditItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_item, parent, false);
		return new EditItemRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull EditItemRecyclerViewAdapterViewHolder holder, int position) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		final ImageIconInfo imageIconInfo = dataList.get(position);
		CompletableFuture.runAsync(() -> LoadImageUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), holder.iconImageView));
		holder.nameTextView.setText(imageIconInfo.getName());
	}

	@SuppressLint("NonConstantResourceId")
	static class EditItemRecyclerViewAdapterViewHolder extends ViewHolder {
		@BindView(R.id.UI_EditItem_ImageView_Icon)
		ImageView iconImageView;
		@BindView(R.id.UI_EditItem_TextView_Name)
		TextView nameTextView;

		public EditItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
