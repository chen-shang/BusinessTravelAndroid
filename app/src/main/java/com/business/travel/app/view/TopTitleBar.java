package com.business.travel.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.business.travel.app.R;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义的顶部标题栏
 */
public class TopTitleBar extends ConstraintLayout {

	public final ImageView topTitleBarMore;
	public final ImageView topTitleBarIcon;
	public final TextView topTitleBarTitle;

	public TopTitleBar(@NonNull @NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
		View inflate = LayoutInflater.from(context).inflate(R.layout.top_title_bar, this);

		topTitleBarIcon = inflate.findViewById(R.id.top_title_bar_icon);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopTitleBar);
		Drawable drawableIcon = typedArray.getDrawable(R.styleable.TopTitleBar_top_title_bar_icon);
		if (drawableIcon == null) {
			topTitleBarIcon.setVisibility(GONE);
		} else {
			topTitleBarIcon.setVisibility(VISIBLE);
			topTitleBarIcon.setImageDrawable(drawableIcon);
		}

		topTitleBarTitle = inflate.findViewById(R.id.top_title_bar_title);
		String title = typedArray.getString(R.styleable.TopTitleBar_top_title_bar_title);
		if (StringUtils.isNotBlank(title)) {
			topTitleBarTitle.setText(title);
		}

		topTitleBarMore = inflate.findViewById(R.id.top_title_bar_more);
		Drawable drawableMore = typedArray.getDrawable(R.styleable.TopTitleBar_top_title_bar_more);
		if (drawableMore == null) {
			topTitleBarMore.setVisibility(GONE);
		} else {
			topTitleBarMore.setVisibility(VISIBLE);
			topTitleBarMore.setImageDrawable(drawableMore);
		}

		typedArray.recycle();
	}
}
