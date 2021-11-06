package com.business.travel.app.ui.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.IconRecyclerViewAdapter.IconRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class IconRecyclerViewAdapter extends BaseRecyclerViewAdapter<IconRecyclerViewAdapterViewHolder, ImageIconInfo> {

	public IconRecyclerViewAdapter(List<ImageIconInfo> imageIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(imageIconInfos, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public IconRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bill_icon_item, parent, false);
		return new IconRecyclerViewAdapterViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull IconRecyclerViewAdapterViewHolder holder, int position) {
		ImageIconInfo imageIconInfo = dataList.get(position);
		ImageView uiImageViewIcon = holder.uiImageViewIcon;

		//初始化 uiImageViewIcon
		uiImageViewIcon.setImageResource(imageIconInfo.getResourceId());
		int initColor = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.black_100);
		uiImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), initColor));

		//初始化 uiTextViewDescription
		TextView uiTextViewDescription = holder.uiTextViewDescription;
		uiTextViewDescription.setText(imageIconInfo.getName());
		uiTextViewDescription.setTextColor(initColor);

		uiImageViewIcon.setOnClickListener(v -> {
			if (imageIconInfo.getResourceId() == R.drawable.bill_icon_add) {
				//TODO 新增
				ToastUtils.showShort("新增");
				return;
			}

			if (imageIconInfo.isSelected()) {
				int color = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.black_100);
				uiImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
				uiTextViewDescription.setTextColor(color);
				imageIconInfo.setSelected(false);
			} else {
				int color = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.teal_800);
				uiImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
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

	@SuppressLint("NonConstantResourceId")
	static class IconRecyclerViewAdapterViewHolder extends ViewHolder {
		@BindView(R.id.UI_ImageView_Icon)
		ImageView uiImageViewIcon;
		@BindView(R.id.UI_TextView_Description)
		TextView uiTextViewDescription;

		public IconRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
