package com.business.travel.app.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.business.travel.app.R;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.kyleduo.switchbutton.SwitchButton;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

public class BottomRemarkEditPopupView extends BottomPopupView {
    private final Keyboard keyboard;

    public BottomRemarkEditPopupView(@NonNull @NotNull Context context, Keyboard keyboard) {
        super(context);
        this.keyboard = keyboard;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_keyboard_header;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        EditText remark = findViewById(R.id.EditText_Remark);
        remark.setText(keyboard.getRemark());

        remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyboard.setRemark(s.toString());
            }
        });
    }
}
