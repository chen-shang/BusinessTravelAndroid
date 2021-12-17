package com.business.travel.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.blankj.utilcode.util.ActivityUtils;
import com.business.travel.app.R;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义的顶部标题栏
 */
public class ContentBar extends ConstraintLayout {

	public final ImageView contentBarLeftIcon;
	public final TextView contentBarTitle;
	public final ImageView contentBarRightIcon;

	public ContentBar(@NonNull @NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
		//顶部title_bar布局
		View inflate = LayoutInflater.from(context).inflate(R.layout.content_bar, this);

		//左侧图标
		contentBarLeftIcon = inflate.findViewById(R.id.content_bar_left_icon);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContentBar);
		Drawable drawableIcon = typedArray.getDrawable(R.styleable.ContentBar_content_bar_left_icon);
		if (drawableIcon == null) {
			contentBarLeftIcon.setVisibility(GONE);
		} else {
			contentBarLeftIcon.setVisibility(VISIBLE);
			contentBarLeftIcon.setImageDrawable(drawableIcon);

			String iconPath = typedArray.getString(R.styleable.ContentBar_content_bar_left_icon);
			if ("res/drawable/ic_base_return.xml".equals(iconPath)) {
				contentBarLeftIcon.setOnClickListener(v -> ActivityUtils.getTopActivity().finish());
			}
		}

		//中间文字
		contentBarTitle = inflate.findViewById(R.id.content_bar_title);
		String title = typedArray.getString(R.styleable.ContentBar_content_bar_title);
		if (StringUtils.isNotBlank(title)) {
			contentBarTitle.setText(title);
		}

		int color = typedArray.getColor(R.styleable.ContentBar_content_bar_title_color, contentBarTitle.getCurrentTextColor());
		contentBarTitle.setTextColor(color);

		String gravity = typedArray.getString(R.styleable.ContentBar_content_bar_title_gravity);
		if ("1".equals(gravity)) {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			contentBarTitle.setLayoutParams(layoutParams);
			contentBarTitle.setGravity(Gravity.CENTER);
		}

		//右侧图标
		contentBarRightIcon = inflate.findViewById(R.id.content_bar_right_icon);
		Drawable drawableMore = typedArray.getDrawable(R.styleable.ContentBar_content_bar_right_icon);
		if (drawableMore == null) {
			contentBarRightIcon.setVisibility(GONE);
		} else {
			contentBarRightIcon.setVisibility(VISIBLE);
			contentBarRightIcon.setImageDrawable(drawableMore);
		}

		typedArray.recycle();
	}
}