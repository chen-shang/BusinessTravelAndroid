package com.business.travel.app.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;

public abstract class TextViewSpan extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ColorUtils.getColor(R.color.red_2));
        //设置是否有下划线
        ds.setUnderlineText(true);
    }
}
