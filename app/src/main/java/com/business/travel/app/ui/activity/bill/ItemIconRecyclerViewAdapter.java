package com.business.travel.app.ui.activity.bill;

import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.bill.ItemIconRecyclerViewAdapter.IconRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.activity.item.EditAssociateItemActivity;
import com.business.travel.app.ui.activity.item.EditConsumptionItemActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ItemIconRecyclerViewAdapter extends BaseRecyclerViewAdapter<IconRecyclerViewAdapterViewHolder, ImageIconInfo> {

	private ItemTypeEnum itemTypeEnum;

	public ItemIconRecyclerViewAdapter(ItemTypeEnum itemTypeEnum, List<ImageIconInfo> imageIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(imageIconInfos, baseActivity);
		this.itemTypeEnum = itemTypeEnum;
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
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		ImageIconInfo imageIconInfo = dataList.get(position);
		if (imageIconInfo == null) {
			return;
		}

		ImageView uiImageViewIcon = holder.uiImageViewIcon;

		//初始化 uiImageViewIcon
		final int resourceId = imageIconInfo.getResourceId();
		if (resourceId != 0) {
			uiImageViewIcon.setImageResource(resourceId);
		}

		CompletableFutureUtil.runAsync(() -> {
			final String iconFullName = imageIconInfo.getIconDownloadUrl();
			final InputStream icon = BusinessTravelResourceApi.getIcon(iconFullName);
			Sharp.loadInputStream(icon).into(uiImageViewIcon);
		});

		//int initColor = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.black_100);
		//uiImageViewIcon.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), initColor));

		//初始化 uiTextViewDescription
		TextView uiTextViewDescription = holder.uiTextViewDescription;
		uiTextViewDescription.setText(imageIconInfo.getIconName());
		//uiTextViewDescription.setTextColor(initColor);

		uiImageViewIcon.setOnClickListener(v -> {
			if (imageIconInfo.getResourceId() == R.drawable.bill_icon_add) {
				switch (itemTypeEnum) {
					case ASSOCIATE:
						activity.startActivity(new Intent(activity, EditAssociateItemActivity.class));
						return;
					case CONSUMPTION:
						activity.startActivity(new Intent(activity, EditConsumptionItemActivity.class));
						return;
					default:
						return;
				}
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
