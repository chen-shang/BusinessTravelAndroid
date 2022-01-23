package com.business.travel.app.ui.activity.log;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.R;
import com.business.travel.app.constant.AppConfig;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityChangeLogBinding;
import com.business.travel.app.model.ConsumeDatePeriod;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.UserService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeLogActivity extends BaseActivity<ActivityChangeLogBinding> {

    private UserService userService;
    private BillService billService;

    @Override
    protected void inject() {
        super.inject();
        userService = new UserService(this);
        billService = new BillService(this);
    }

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

        viewBinding.clearCache.setOnClickListener(v -> {
            LogToast.infoShow("实时更新图标");
            AppConfig.getConfig().setIconTtl(0L);
        });

        viewBinding.changeAgree.setOnClickListener(v -> {
            User user = userService.queryUser();
            user.setAgree(!user.getAgree());
            userService.upsertUser(user);
            LogToast.infoShow("同意用户协议:" + user.getAgree());
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