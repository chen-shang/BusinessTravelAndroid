package com.business.travel.app;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.databinding.ActivityMainBinding;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.FutureUtil;

/**
 * @author chenshang
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {
	private ConsumptionService consumptionService;
	private MemberService memberService;

	@Override
	protected void inject() {
		memberService = new MemberService(this);
		consumptionService = new ConsumptionService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BarUtils.setStatusBarColor(this, ColorUtils.getColor(R.color.white));

		//开启日志记录写入文件,最大栈深度为5,最多保留一天 todo 生产环境关闭日志提高程序速度
		LogUtils.getConfig().setLog2FileSwitch(true).setStackDeep(5).setSaveDays(1).setLogSwitch(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
		//因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
		LogUtils.i("1秒后启动主界面");
		Timer timer = new Timer();
		Intent goMasterActivityIntent = new Intent(this, MasterActivity.class);
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