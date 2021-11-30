package com.business.travel.app.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.business.travel.app.databinding.ActivityLoginBinding;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void goMasterActivity(View view) {
		Intent intent = new Intent(this, MasterActivity.class);
		startActivity(intent);
	}

	public void goAccountLongActivity(View view) {
	}

	public void goSingUpActivity(View view) {

	}
}