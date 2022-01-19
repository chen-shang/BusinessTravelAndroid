package com.business.travel.app.ui;

import android.content.Intent;
import android.os.Bundle;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityMainBinding;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.UserService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.view.BottomAgreementPopupView;
import com.lxj.xpopup.XPopup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author chenshang
 * 首页,主界面,用户看到的第一个页面
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    //各种service
    private ConsumptionService consumptionService;
    private MemberService memberService;
    private UserService userService;

    @Override
    protected void inject() {
        memberService = new MemberService(this);
        consumptionService = new ConsumptionService(this);
        userService = new UserService(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //用户必须同意用户协议才能进行下一步
        User user = userService.queryUser();
        //此时还没有用户
        if (user == null || !user.getAgree()) {
            //弹出是否同意弹框
            new XPopup.Builder(this)
                    .dismissOnTouchOutside(true) // 点击外部是否关闭弹窗，默认为true
                    .asCustom(new BottomAgreementPopupView(this,
                            //如果选择同意
                            () -> {
                                //更新为同意
                                User record = new User();
                                record.setAgree(true);
                                userService.upsertUser(record);

                                //进入主界面
                                start();
                            },
                            //如果选择不同意,关闭所有页面
                            ActivityUtils::finishAllActivities)).show();
            return;
        }

        //要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
        //因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
        start();
    }

    /**
     * 开始
     */
    private void start() {
        //要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
        //因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
        Intent goMasterActivityIntent = new Intent(this, MasterActivity.class);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtils.i("开始启动主界面");
                startActivity(goMasterActivityIntent);
            }
        }, 1000);

        FutureUtil.runAsync(() -> {
            LogUtils.i("应用启动的时候,异步初始化消费项和人员图标数据");
            //初次使用app的时候,数据库中是没有消费项图标数据的,因此需要初始化一些默认的图标
            consumptionService.initConsumption();
            //初次使用app的时候,数据库中是没有人员图标数据的,因此需要初始化一些默认的图标
            memberService.initMember();
        });
    }
}