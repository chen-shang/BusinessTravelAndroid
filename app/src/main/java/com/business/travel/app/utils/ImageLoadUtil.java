package com.business.travel.app.utils;

import android.widget.ImageView;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemIconEnum;
import com.pixplicity.sharp.Sharp;

public class ImageLoadUtil {
	/**
	 * 加载图片到指定的ImageView
	 *
	 * @param iconDownloadUrl
	 * @param uiImageViewIcon
	 */
	public static void loadImageToView(String iconDownloadUrl, ImageView uiImageViewIcon) {
		final ItemIconEnum itemIconEnum = ItemIconEnum.ofUrl(iconDownloadUrl);
		if (itemIconEnum != null) {
			uiImageViewIcon.setImageResource(itemIconEnum.getResourceId());
			return;
		}

		FutureUtil.supplyAsync(() -> BusinessTravelResourceApi.getIcon(iconDownloadUrl)).whenComplete((inputStream, throwable) -> {
			if (inputStream != null) {
				Sharp.loadInputStream(inputStream).into(uiImageViewIcon);
				return;
			}
			uiImageViewIcon.setImageResource(R.drawable.ic_base_placeholder);
		});
	}
}
