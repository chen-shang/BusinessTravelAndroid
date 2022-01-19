package com.business.travel.app.view;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.business.travel.app.R;
import com.kyleduo.switchbutton.SwitchButton;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

public class BottomRemarkEditPopupView extends BottomPopupView {
    /**
     * 收入支出显示tag
     */
    public SwitchButton switchButton;
    /**
     * 金额展示
     */
    public TextView textViewAmount;
    /**
     * 备注编辑框
     */
    public EditText editTextRemark;

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
        editTextRemark = findViewById(R.id.EditText_Remark);
        textViewAmount = findViewById(R.id.TextView_Amount);
        switchButton = findViewById(R.id.TextView_PayType);
    }

    //在展示之前进行数据更新
    @Override
    protected void beforeShow() {
        super.beforeShow();
        editTextRemark.setText(keyboard.getRemark());
        textViewAmount.setText(keyboard.getAmount());
        switchButton.setChecked(keyboard.switchButton.isChecked());
    }

    /**
     * 消失之后
     */
    @Override
    protected void doAfterDismiss() {
        super.doAfterDismiss();
        keyboard.setRemark(editTextRemark.getText().toString());
        keyboard.setAmount(textViewAmount.getText().toString());
        keyboard.switchButton.setChecked(switchButton.isChecked());
    }
}
