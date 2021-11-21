package com.business.travel.app.ui.activity.item;

import java.io.InputStream;
import java.util.List;

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
import com.blankj.utilcode.util.StringUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.model.ItemIconInfo.ImageIconInfo;
import com.business.travel.app.ui.activity.item.AddConsumptionItemIconRecyclerViewAdapter.AddConsumptionItemIconRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.CompletableFutureUtil;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

public class AddConsumptionItemIconRecyclerViewAdapter extends BaseRecyclerViewAdapter<AddConsumptionItemIconRecyclerViewAdapterViewHolder, ImageIconInfo> {

	public AddConsumptionItemIconRecyclerViewAdapter(List<ImageIconInfo> imageIconInfos, BaseActivity<? extends ViewBinding> baseActivity) {
		super(imageIconInfos, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public AddConsumptionItemIconRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bill_icon_item, parent, false);
		return new AddConsumptionItemIconRecyclerViewAdapterViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull AddConsumptionItemIconRecyclerViewAdapterViewHolder holder, int position) {
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

		//最后一个被选中的按钮
		//当图片按钮被点击的时候
		holder.itemView.setOnClickListener(v -> {
			if (imageIconInfo.isSelected()) { //如果当前被选中
				//点击则取消选中
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_unselect);
				imageIconInfo.setSelected(false);
			} else {
				//如果当前没有被选中
				//之前的取消

				//当前的选中
				uiImageViewIcon.setBackgroundResource(R.drawable.corners_shape_select);
				ImageView imageView = activity.findViewById(R.id.UI_ImageView_Icon_1);
				imageView.setImageDrawable(uiImageViewIcon.getDrawable());
				imageIconInfo.setSelected(true);

				if (((AddConsumptionItemActivity)activity).getLastSelectedImageView() != null) {
					((AddConsumptionItemActivity)activity).getLastSelectedImageView().setBackgroundResource(R.drawable.corners_shape_unselect);
					((AddConsumptionItemActivity)activity).getLastSelectedImageIcon().setSelected(false);
				}

				((AddConsumptionItemActivity)activity).setLastSelectedImageView(uiImageViewIcon);
				((AddConsumptionItemActivity)activity).setLastSelectedImageIcon(imageIconInfo);
			}
		});
	}

	@SuppressLint("NonConstantResourceId")
	static class AddConsumptionItemIconRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_ImageView_Icon)
		public ImageView uiImageViewIcon;
		@BindView(R.id.UI_TextView_Description)
		public TextView uiTextViewDescription;

		public AddConsumptionItemIconRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

}
