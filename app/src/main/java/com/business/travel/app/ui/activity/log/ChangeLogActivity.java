package com.business.travel.app.ui.activity.log;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityChangeLogBinding;
import com.business.travel.app.ui.base.BaseActivity;

public class ChangeLogActivity extends BaseActivity<ActivityChangeLogBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewBinding.log.setMovementMethod(ScrollingMovementMethod.getInstance());
		viewBinding.showLog.setOnClickListener(v -> {
			final List<File> logFiles = LogUtils.getLogFiles();
			final String log = logFiles.stream().map(FileIOUtils::readFile2String).collect(Collectors.joining("\n"));
			viewBinding.log.setText(log);
		});

		viewBinding.clearLog.setOnClickListener(v -> {
			final List<File> logFiles = LogUtils.getLogFiles();
			logFiles.forEach(File::delete);
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		//加载 change log
		String[] changeLog = this.getResources().getStringArray(R.array.change_log);
		final String collect = String.join("\n", changeLog);
		viewBinding.versionHistory.setText(collect);
	}
}