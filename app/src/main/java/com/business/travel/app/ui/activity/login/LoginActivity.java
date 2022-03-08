package com.business.travel.app.ui.activity.login;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.business.travel.app.MainActivity;
import com.business.travel.app.databinding.ActivityLoginBinding;
import com.business.travel.app.ui.activity.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
		 * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
		 * UMConfigure.init调用中appkey和channel参数请置为null）。
		 */
		UMConfigure.init(this, "61eeb912e014255fcb04447f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

		PlatformConfig.setWeixin("wxaae6cbabcbc0aa53", "29730d7b1ced83f60f7701d3371e8151");
	}

	public void goMasterActivity(View view) {
		UMAuthListener authListener = new UMAuthListener() {
			/**
			 * @param platform 平台名称
			 * @desc 授权开始的回调
			 */
			@Override
			public void onStart(SHARE_MEDIA platform) {

			}

			/**
			 * @param platform 平台名称
			 * @param action   行为序号，开发者用不上
			 * @param data     用户资料返回
			 * @desc 授权成功的回调
			 */
			@Override
			public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
				LogToast.infoShow("成功了" + data);
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
			}

			/**
			 * @param platform 平台名称
			 * @param action   行为序号，开发者用不上
			 * @param t        错误原因
			 * @desc 授权失败的回调
			 */
			@Override
			public void onError(SHARE_MEDIA platform, int action, Throwable t) {

				LogToast.infoShow("失败：" + t.getMessage());
			}

			/**
			 * @param platform 平台名称
			 * @param action   行为序号，开发者用不上
			 * @desc 授权取消的回调
			 */
			@Override
			public void onCancel(SHARE_MEDIA platform, int action) {
				LogToast.infoShow("取消了");
			}
		};
		UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
	}
}