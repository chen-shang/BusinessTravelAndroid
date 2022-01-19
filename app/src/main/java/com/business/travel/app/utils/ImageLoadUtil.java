package com.business.travel.app.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.blankj.utilcode.util.ResourceUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.enums.ItemIconEnum;
import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import org.jetbrains.annotations.NotNull;

/**
 * 加载图片到指定的ImageView工具类
 */
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
     * 加载图片,带颜色
     *
     * @param iconDownloadUrl
     * @param uiImageViewIcon
     * @param color
     */
    public static void loadImageToView(String iconDownloadUrl, ImageView uiImageViewIcon, @ColorInt Integer color) {
        final ItemIconEnum itemIconEnum = ItemIconEnum.ofUrl(iconDownloadUrl);
        if (itemIconEnum != null) {
            changeLocalIconColor(uiImageViewIcon, itemIconEnum.getResourceId(), color);
            return;
        }

        FutureUtil.supplyAsync(() -> BusinessTravelResourceApi.getIcon(iconDownloadUrl)).whenComplete((inputStream, throwable) -> {
            if (inputStream == null) {
                changeLocalIconColor(uiImageViewIcon, R.drawable.ic_base_placeholder, color);
                return;
            }
            //需要改变颜色
            Sharp.loadInputStream(inputStream).setOnElementListener(new ChangeRemoteIconColor(color)).into(uiImageViewIcon);
        });
    }

    private static void changeLocalIconColor(ImageView uiImageViewIcon, int resourceId, Integer color) {
        uiImageViewIcon.setImageResource(resourceId);
        if (color != null) {
            //需要改变颜色
            uiImageViewIcon.getDrawable().setTint(color);
        } else {
            //恢复原来的颜色
            Drawable drawable = ResourceUtils.getDrawable(resourceId);
            uiImageViewIcon.setImageDrawable(drawable);
        }
    }

    /**
     * 改变颜色的函数
     */
    static class ChangeRemoteIconColor implements OnSvgElementListener {
        @ColorInt
        private final Integer color;

        public ChangeRemoteIconColor(Integer color) {
            this.color = color;
        }

        @Override
        public void onSvgStart(@NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF bounds) {

        }

        @Override
        public void onSvgEnd(@NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF bounds) {

        }

        @Override
        public <T> T onSvgElement(@Nullable @org.jetbrains.annotations.Nullable String id, @NonNull @NotNull T element, @Nullable @org.jetbrains.annotations.Nullable RectF elementBounds, @NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable RectF canvasBounds,
                                  @Nullable @org.jetbrains.annotations.Nullable Paint paint) {
            if (paint != null && color != null) {
                paint.setColor(color);
                return element;
            }
            return element;
        }

        @Override
        public <T> void onSvgElementDrawn(@Nullable @org.jetbrains.annotations.Nullable String id, @NonNull @NotNull T element, @NonNull @NotNull Canvas canvas, @Nullable @org.jetbrains.annotations.Nullable Paint paint) {

        }
    }
}
