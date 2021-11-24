package com.business.travel.app.ui.activity.bill;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.business.travel.app.R;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.bill.ItemIconRecyclerViewAdapter.IconRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.activity.item.EditAssociateItemActivity;
import com.business.travel.app.ui.activity.item.EditConsumptionItemActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.business.travel.app.utils.LoadImageUtil;
import org.jetbrains.annotations.NotNull;

import static com.business.travel.app.enums.ItemTypeEnum.ASSOCIATE;
import static com.business.travel.app.enums.ItemTypeEnum.CONSUMPTION;

/**
 * @author chenshang
 */
public class ItemIconRecyclerViewAdapter extends BaseRecyclerViewAdapter<IconRecyclerViewAdapterViewHolder, ImageIconInfo> {

	protected final ItemTypeEnum itemTypeEnum;

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
		//先根据RUL地址查询本地资源
		String iconDownloadUrl = imageIconInfo.getIconDownloadUrl();
		if (StringUtils.isEmpty(iconDownloadUrl)) {
			return;
		}

		ImageView uiImageViewIcon = holder.uiImageViewIcon;
		//发起网络请求
		CompletableFutureUtil.runAsync(() -> LoadImageUtil.loadImageToView(iconDownloadUrl, uiImageViewIcon));

		int unSelectColor = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.black_100);
		int selectColor = ContextCompat.getColor(uiImageViewIcon.getContext(), R.color.teal_800);
		uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_unselect);
		//初始化 uiTextViewDescription
		TextView uiTextViewDescription = holder.uiTextViewDescription;
		uiTextViewDescription.setText(imageIconInfo.getName());
		uiTextViewDescription.setTextColor(unSelectColor);
		//编辑按钮
		boolean isEditImageButton = ItemIconEnum.ItemIconEdit.getIconDownloadUrl().equals(iconDownloadUrl);
		//当图片按钮被点击的时候
		uiImageViewIcon.setOnClickListener(v -> {
			if (isEditImageButton && ASSOCIATE == itemTypeEnum) {
				activity.startActivity(new Intent(activity, EditAssociateItemActivity.class));
				return;
			}

			if (isEditImageButton && CONSUMPTION == itemTypeEnum) {
				activity.startActivity(new Intent(activity, EditConsumptionItemActivity.class));
				return;
			}

			imageIconInfo.setSelected(!imageIconInfo.isSelected());
			if (imageIconInfo.isSelected()) {
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_select);
				uiTextViewDescription.setTextColor(selectColor);
			}
			if (!imageIconInfo.isSelected()) {
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_unselect);
				uiTextViewDescription.setTextColor(unSelectColor);
			}
		});
	}

	@SuppressLint("NonConstantResourceId")
	public static class IconRecyclerViewAdapterViewHolder extends ViewHolder {
		@BindView(R.id.UI_ImageView_Icon)
		public ImageView uiImageViewIcon;
		@BindView(R.id.UI_TextView_Description)
		public TextView uiTextViewDescription;

		public IconRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
