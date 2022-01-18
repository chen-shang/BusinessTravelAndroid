package com.business.travel.app.view;

import android.content.Context;
import androidx.annotation.NonNull;
import com.business.travel.app.R;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

public class BottomRemarkEditPopupView extends BottomPopupView {
    public BottomRemarkEditPopupView(@NonNull @NotNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_keyboard_header;
    }
}
