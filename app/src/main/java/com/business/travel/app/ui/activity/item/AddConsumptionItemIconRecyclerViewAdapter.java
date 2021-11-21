package com.business.travel.app.ui.activity.item;

import java.io.InputStream;
import java.util.List;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ItemIconInfo.ImageIconInfo;
import com.business.travel.app.ui.activity.bill.ItemIconRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

import static com.business.travel.app.enums.ItemTypeEnum.ASSOCIATE;
import static com.business.travel.app.enums.ItemTypeEnum.CONSUMPTION;

public class AddConsumptionItemIconRecyclerViewAdapter extends ItemIconRecyclerViewAdapter {

	public AddConsumptionItemIconRecyclerViewAdapter(ItemTypeEnum itemTypeEnum, List<ImageIconInfo> imageIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(itemTypeEnum, imageIconInfos, baseActivity);
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
		ItemIconEnum itemIconEnum = ItemIconEnum.ofUrl(iconDownloadUrl);
		if (itemIconEnum != null) {
			uiImageViewIcon.setImageResource(itemIconEnum.getResourceId());
		} else {
			//发起网络请求 todo 异步
			CompletableFutureUtil.runAsync(() -> {
				InputStream iconInputStream = BusinessTravelResourceApi.getIcon(iconDownloadUrl);
				Sharp.loadInputStream(iconInputStream).into(uiImageViewIcon);
			});
		}

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

			//如果当前被选中
			if (imageIconInfo.isSelected()) {
				//取消选中
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_unselect);
				imageIconInfo.setSelected(false);
			} else {
				//如果当前没有被选中
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_select);
				ImageView imageView = activity.findViewById(R.id.UI_ImageView_Icon_1);
				imageView.setImageDrawable(uiImageViewIcon.getDrawable());
				imageIconInfo.setSelected(true);
			}
		});
	}
}
