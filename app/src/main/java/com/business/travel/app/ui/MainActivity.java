package com.business.travel.app.ui;

import android.content.Intent;
import android.os.Bundle;
import com.blankj.utilcode.util.ActivityUtils;
import com.business.travel.app.constant.AppConfig;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityMainBinding;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.UserService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.app.view.BottomAgreementPopupView;
import com.lxj.xpopup.XPopup;
import com.umeng.commonsdk.UMConfigure;

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
        if (user != null && user.getAgree()) {
            //要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
            //因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
            //延迟初始化
            start();
            return;
        }
        //弹出是否同意弹框
        showAgreementPopupView();
    }

    /**
     * 弹出是否同意弹框
     */
    private void showAgreementPopupView() {
        new XPopup.Builder(this)
                // 点击外部是否关闭弹窗
                .dismissOnTouchOutside(false)
                // 按返回键是否关闭弹窗
                .dismissOnBackPressed(false)
                // 退出即回收
                .isDestroyOnDismiss(true)
                //是否启用拖拽
                .enableDrag(false)
                // 自定义弹框
                .asCustom(new BottomAgreementPopupView(this,
                        //如果选择同意
                        this::whenAgree,
                        //如果选择不同意,关闭所有页面
                        this::whenCancel)).show();
    }

    /**
     * 如果选择不同意,关闭所有页面
     */
    private void whenCancel() {
        ActivityUtils.finishAllActivities();
    }

    /**
     * 如果选择同意
     */
    private void whenAgree() {
        //更新为同意
        User record = new User();
        record.setAgree(true);
        userService.upsertUser(record);

        //进入主界面
        start();
    }

    /**
     * 开始进入主界面,延迟初始化
     */
    private void start() {

        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(this, "61eeb912e014255fcb04447f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

        //要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
        //因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
        Intent goMasterActivityIntent = new Intent(this, MasterActivity.class);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(goMasterActivityIntent);
            }
        }, 1000);

        FutureUtil.runAsync(() -> Try.of(() -> {
            //初始化
            AppConfig.refreshConfig();
            
            //初次使用app的时候,数据库中是没有消费项图标数据的,因此需要初始化一些默认的图标
            consumptionService.initConsumption();
            //初次使用app的时候,数据库中是没有人员图标数据的,因此需要初始化一些默认的图标
            memberService.initMember();
        }));
    }
}