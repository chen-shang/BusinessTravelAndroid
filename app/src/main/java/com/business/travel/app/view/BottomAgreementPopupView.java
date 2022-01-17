package com.business.travel.app.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.constant.BusinessTravelResourceConstant;
import com.business.travel.app.enums.WebTextTypeEnum;
import com.business.travel.app.model.Config;
import com.business.travel.app.ui.activity.my.WebTextActivity;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.NetworkUtil;
import com.business.travel.utils.JacksonUtil;
import com.lxj.xpopup.core.BottomPopupView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

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

        TextView content = findViewById(R.id.content);
        String userNotice = getUserNotice();
        if (StringUtils.isBlank(userNotice)) {
            userNotice = "在使用前,请认真阅读并同意我们的《用户协议》和《隐私政策》";
        }
        SpannableString sp = new SpannableString(userNotice);

        // 找到用户协议位置，加点击事件
        int start = userNotice.indexOf("《用户协议》");
        sp.setSpan(new TextViewSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                goWebTextActivity(WebTextTypeEnum.USER_AGREEMENT);
            }
        }, start, start + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //找到隐私政策位置，加点击事件
        int start1 = userNotice.indexOf("《隐私政策》");
        sp.setSpan(new TextViewSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                goWebTextActivity(WebTextTypeEnum.PRIVACY_POLICY);
            }
        }, start1, start1 + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        content.setText(sp);

        content.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getUserNotice() {
        try {
            return FutureUtil.supplyAsync(() -> {
                if (!NetworkUtil.isAvailable()) {
                    LogToast.errorShow("网络环境较差,请稍后重试");
                    return "{}";
                }
                return BusinessTravelResourceApi.getRepoRaw(BusinessTravelResourceConstant.APP_CONFIG_PATH);
            }).thenApply(config -> {
                Config newConfig = JacksonUtil.toBean(config, Config.class);
                return newConfig.getUserNotice();
            }).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            LogUtils.e("网络不好,请稍后重试:" + e);
            LogToast.errorShow("网络不好,请稍后重试:" + e.getMessage());
        }
        return null;
    }

    @Override
    protected int getPopupHeight() {
        return ScreenUtils.getScreenHeight() / 3;
    }

    private void goWebTextActivity(WebTextTypeEnum webTextType) {
        this.dismiss();

        Intent intent = new Intent(getContext(), WebTextActivity.class);
        intent.putExtra("WebTextType", webTextType.name());
        getContext().startActivity(intent);
    }
}
