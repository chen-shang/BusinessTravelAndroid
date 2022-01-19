package com.business.travel.app.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.business.travel.app.databinding.ActivityAboutMeBinding;
import com.business.travel.app.enums.WebTextTypeEnum;
import com.business.travel.app.ui.activity.log.ChangeLogActivity;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.utils.DateTimeUtil;

/**
 * 关于我的页面
 */
public class AboutMeActivity extends ColorStatusBarActivity<ActivityAboutMeBinding> {
    /**
     * 当前点击次数
     */
    private long counter = 0;
    /**
     * 上次点击的时间
     */
    private long lastClickTime;
    /**
     * 最大点击次数
     */
    private static final int MAX_COUNTER = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进入版本更新页面
        goChangeLogOnViewClick(viewBinding.icon);

        //注册用户协议点击事件
        goWebTextActivityOnViewClick(viewBinding.aboutMeUserAgreement, WebTextTypeEnum.USER_AGREEMENT);
        goWebTextActivityOnViewClick(viewBinding.aboutMeUserAgreement.contentBarRightIcon, WebTextTypeEnum.USER_AGREEMENT);
        goWebTextActivityOnViewClick(viewBinding.aboutMeUserAgreement.contentBarTitle, WebTextTypeEnum.USER_AGREEMENT);
        goWebTextActivityOnViewClick(viewBinding.aboutMeUserAgreement.contentBarLeftIcon, WebTextTypeEnum.USER_AGREEMENT);

        //注册隐私政策
        goWebTextActivityOnViewClick(viewBinding.aboutMePolicy, WebTextTypeEnum.PRIVACY_POLICY);
        goWebTextActivityOnViewClick(viewBinding.aboutMePolicy.contentBarRightIcon, WebTextTypeEnum.PRIVACY_POLICY);
        goWebTextActivityOnViewClick(viewBinding.aboutMePolicy.contentBarTitle, WebTextTypeEnum.PRIVACY_POLICY);
        goWebTextActivityOnViewClick(viewBinding.aboutMePolicy.contentBarLeftIcon, WebTextTypeEnum.PRIVACY_POLICY);
    }

    private void goWebTextActivityOnViewClick(View view, WebTextTypeEnum webTextType) {
        Intent intent = new Intent(this, WebTextActivity.class);
        intent.putExtra(WebTextActivity.IntentKey.WEB_TEXT_TYPE, webTextType.name());
        view.setOnClickListener(v -> startActivity(intent));
    }

    private void goChangeLogOnViewClick(View view) {
        view.setOnClickListener(v -> {
            if (DateTimeUtil.timestamp() - lastClickTime < 2000) {
                counter++;
            } else {
                lastClickTime = DateTimeUtil.timestamp();
                counter = 0;
            }

            if (counter == MAX_COUNTER) {
                startActivity(new Intent(this, ChangeLogActivity.class));
            }
        });
    }
}