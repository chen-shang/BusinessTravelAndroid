package com.business.travel.app.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import org.jetbrains.annotations.NotNull;

public class BaseTopView extends ConstraintLayout {
	private ImageView imageView;
	private TextView textView;

	public BaseTopView(@NonNull @NotNull Context context) {
		super(context);

		
	}
}
