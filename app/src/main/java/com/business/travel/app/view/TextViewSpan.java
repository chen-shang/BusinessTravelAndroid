package com.business.travel.app.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.utils.LogToast;

public class TextViewSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ColorUtils.getColor(R.color.red_2));
        //设置是否有下划线
        ds.setUnderlineText(true);
    }

    @Override
    public void onClick(@NonNull View widget) {
        LogToast.infoShow("这是测试点击1");
    }
}
