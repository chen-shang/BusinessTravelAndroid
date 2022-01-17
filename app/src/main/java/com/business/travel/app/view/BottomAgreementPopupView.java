package com.business.travel.app.view;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ScreenUtils;
import com.business.travel.app.R;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

/***
 * 需要用户确认的用户须知底部弹框
 */
public class BottomAgreementPopupView extends BottomPopupView {

    /**
     * 当点击确定之后
     */
    public Runnable onConfirm;
    /**
     * 当点击取消之后
     */
    public Runnable onCancel;

    public BottomAgreementPopupView(@NonNull @NotNull Context context, Runnable onConfirm, Runnable onCancel) {
        super(context);
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.bottom_agreement_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        //同意按钮点击行为
        TextView textView = findViewById(R.id.agree);
        textView.setOnClickListener(view -> {
            this.dismiss();
            onConfirm.run();
        });

        //取消按钮点击行为
        TextView textView2 = findViewById(R.id.un_agree);
        textView2.setOnClickListener(view -> {
            this.dismiss();
            onCancel.run();
        });
    }

    @Override
    protected int getPopupHeight() {
        return ScreenUtils.getScreenHeight() / 3;
    }
}
