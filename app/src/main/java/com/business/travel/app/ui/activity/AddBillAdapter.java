package com.business.travel.app.ui.activity;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.AddBillAdapter.AddBillImageViewHolder;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class AddBillAdapter extends RecyclerView.Adapter<AddBillImageViewHolder> {
	private final List<ImageIconInfo> list;

	public AddBillAdapter(List<ImageIconInfo> list) {this.list = list;}

	@NonNull
	@NotNull
	@Override
	public AddBillImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bill_icon_item, parent, false);
		return new AddBillImageViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddBillImageViewHolder holder, int position) {
		ImageIconInfo imageIconInfo = list.get(position);
		ImageView uIImageViewIcon = holder.uIImageViewIcon;
		uIImageViewIcon.setImageResource(imageIconInfo.getResourceId());
		TextView uiTextViewDescription = holder.uiTextViewDescription;
		uiTextViewDescription.setText(imageIconInfo.getName());

		uIImageViewIcon.setOnClickListener(v -> {
			if (imageIconInfo.getResourceId() == R.drawable.bill_icon_add) {
				//TODO 新增
				ToastUtils.showShort("新增");
				return;
			}

			if (imageIconInfo.isSelected()) {
				int color = ContextCompat.getColor(uIImageViewIcon.getContext(), R.color.black_100);
				uIImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
				uiTextViewDescription.setTextColor(color);
				imageIconInfo.setSelected(false);
			} else {
				int color = ContextCompat.getColor(uIImageViewIcon.getContext(), R.color.teal_800);
				uIImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
				uiTextViewDescription.setTextColor(color);
				imageIconInfo.setSelected(true);
			}
		});
	}

	private Drawable changeToColor(int resourceId, int color) {
		Drawable drawable = ResourceUtils.getDrawable(resourceId);
		Drawable wrap = DrawableCompat.wrap(drawable);
		DrawableCompat.setTint(wrap, color);
		return drawable;
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	static class AddBillImageViewHolder extends ViewHolder {
		@BindView(R.id.UI_ImageView_Icon)
		ImageView uIImageViewIcon;
		@BindView(R.id.UI_TextView_Description)
		TextView uiTextViewDescription;

		public AddBillImageViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
