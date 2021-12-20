package com.business.travel.app.ui.test;

import android.os.Bundle;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import lombok.SneakyThrows;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {

	@Override
	protected void inject() {
	}

	@SneakyThrows
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		viewBinding.ceshi.setOnClickListener(v -> {
			throw new IllegalArgumentException("我错了！！！！呜呜呜");
		});

		FutureUtil.supplyAsync(() -> BusinessTravelResourceApi.getUserAgreement()).whenComplete((s, throwable) -> {
			LogToast.infoShow(s);
			viewBinding.userAgreement.setText(s);
		});
	}
}