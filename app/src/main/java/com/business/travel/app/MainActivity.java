package com.business.travel.app;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ColorUtils;
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		//要充分利用启动页面的停顿时间,尽量做一些后台工作,比如检查网络,同步数据之类的,初始化数据之类
		//因为这个类只在启动的时候启动一次,不会重复做一些事情,可以提升其他页面的访问速度
		FutureUtil.runAsync(() -> {
			//初次使用app的时候,数据库中是没有消费项图标数据的,因此需要初始化一些默认的图标
			consumptionService.initConsumption();
			//初次使用app的时候,数据库中是没有人员图标数据的,因此需要初始化一些默认的图标
			memberService.initMember();
		});

		Timer timer = new Timer();
		Intent goMasterActivityIntent = new Intent(this, MasterActivity.class);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				startActivity(goMasterActivityIntent);
			}
		}, 1000);
	}
}