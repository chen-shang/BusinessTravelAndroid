package com.business.travel.app.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemIconEnum;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

public class ImageLoadUtil {
	/**
	 * 加载图片到指定的ImageView
	 *
	 * @param iconDownloadUrl
	 * @param uiImageViewIcon
	 */
	public static void loadImageToView(String iconDownloadUrl, ImageView uiImageViewIcon) {
		loadImageToView(iconDownloadUrl, uiImageViewIcon, null);
	}

	/**
	 * 加载突变,带颜色
	 *
	 * @param iconDownloadUrl
	 * @param uiImageViewIcon
	 * @param color
	 */
	public static void loadImageToView(String iconDownloadUrl, ImageView uiImageViewIcon, @ColorInt Integer color) {
		final ItemIconEnum itemIconEnum = ItemIconEnum.ofUrl(iconDownloadUrl);
		if (itemIconEnum != null) {
			uiImageViewIcon.setImageResource(itemIconEnum.getResourceId());
			if (color != null) {
				//需要改变颜色
				uiImageViewIcon.getDrawable().setTint(color);
			}
			return;
		}

		FutureUtil.supplyAsync(() -> BusinessTravelResourceApi.getIcon(iconDownloadUrl)).whenComplete((inputStream, throwable) -> {
			if (inputStream == null) {
				return;
			}
			if (color == null) {
				//不需要改变颜色
				Sharp.loadInputStream(inputStream).into(uiImageViewIcon);
				return;
			}
			Sharp.loadInputStream(inputStream).setOnElementListener(new OnSvgElementListener() {
				@Override
				public void onSvgStart(@NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF bounds) {

				}

				@Override
				public void onSvgEnd(@NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF bounds) {

				}

				@Override
				public <T> T onSvgElement(@Nullable @org.jetbrains.annotations.Nullable String id, @NonNull @NotNull T element, @Nullable @org.jetbrains.annotations.Nullable RectF elementBounds, @NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF canvasBounds, @Nullable @org.jetbrains.annotations.Nullable Paint paint) {
					if (paint != null) {
						paint.setColor(color);
						return element;
					}
					return null;
				}

				@Override
				public <T> void onSvgElementDrawn(@Nullable @org.jetbrains.annotations.Nullable String id, @NonNull @NotNull T element, @NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable Paint paint) {

				}
			}).into(uiImageViewIcon);
		});
	}
}
