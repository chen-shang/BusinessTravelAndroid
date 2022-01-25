package com.business.travel.app.ui.activity.project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.enums.DateTimeTagEnum;
import com.business.travel.app.model.ConsumeDatePeriod;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.google.common.base.Preconditions;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 编辑项目 | 新增项目页面
 */
public class EditProjectActivity extends ColorStatusBarActivity<ActivityEditProjectBinding> {

    /**
     * 当前编辑的项目信息,如果存在则是更新,否则就是新增
     */
    private Long selectProjectId;
    /**
     * 日历选框
     */
    private DatePickerDialog datePickerDialog;
    /**
     * 各种service
     */
    private ProjectService projectService;
    private BillService billService;


    @Override
    protected void inject() {
        projectService = new ProjectService(this);
        billService = new BillService(this);
        LocalDateTime now = DateTimeUtil.now();
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
        }, now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth());

        selectProjectId = getIntent().getLongExtra(IntentKey.PROJECT_ID, -1L);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProject();
        //注册项目开始结束时间点击事件
        //开始时间点击
        viewBinding.projectStartTime.setOnClickListener(v -> whenChoseSelectTime((TextView) v));
        //结束时间点击
        registerDatePicker(viewBinding.projectEndTime);
        //注册右上角对号保存按钮点击事件
        registerSaveButton(viewBinding.topTitleBar.contentBarRightIcon);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Try.of(() -> showSelectProjectInfo(selectProjectId));
    }

    private void registerSaveButton(View saveButton) {
        saveButton.setOnClickListener(v -> Try.of(() -> {
            Project project = new Project();
            String projectName = viewBinding.projectName.getText().toString();
            project.setName(projectName);
            //开始时间
            String projectStartTime = viewBinding.projectStartTime.getText().toString();
            Long startTime = getTime(projectStartTime);
            //默认今天
            startTime = Optional.ofNullable(startTime).orElse(DateTimeUtil.timestamp(LocalDate.now()));
            project.setStartTime(startTime);

            //结束时间
            String projectEndTime = viewBinding.projectEndTime.getText().toString();
            Long endTime = getTime(projectEndTime);
            project.setEndTime(endTime);

            if (endTime != null && !DateTimeTagEnum.TobeDetermined.getCode().equals(endTime)) {
                Preconditions.checkArgument(startTime < endTime, "项目结束时间不能小于开始时间");
            }

            ConsumeDatePeriod consumeDatePeriod = billService.selectMaxAndMinConsumeDate(selectProjectId);
            Long min = consumeDatePeriod.getMin();
            if (min != null && startTime > min) {
                throw new IllegalArgumentException("起止时间范围外有该项目账单");
            }

            Long max = consumeDatePeriod.getMax();
            if (endTime != null && max != null && endTime < max) {
                throw new IllegalArgumentException("起止时间范围外有该项目账单");
            }

            String remark = viewBinding.projectRemark.getText().toString();
            project.setRemark(remark);

            if (selectProjectId > 0) {
                projectService.updateProject(selectProjectId, project);
            } else {
                project = projectService.createProject(project);
            }
            this.finishForResult(project);
        }));
    }

    private void finishForResult(Project project) {
        Activity lastActivity = getLastActivity();
        if (lastActivity != null) {
            Intent intent = new Intent(this, lastActivity.getClass());
            intent.putExtra(IntentKey.EDITE_PROJECT_RESULT, JacksonUtil.toString(project));
            this.setResult(RESULT_OK, intent);
            startActivityForResult(intent, 1);
        }
        this.finish();
    }

    private Activity getLastActivity() {
        List<Activity> activityList = ActivityUtils.getActivityList();
        if (CollectionUtils.isEmpty(activityList) || activityList.size() < 2) {
            return null;
        }
        return activityList.get(1);
    }

    private Long getTime(String startTime) {
        if (StringUtils.isBlank(startTime)) {
            return null;
        }
        if (DateTimeTagEnum.TobeDetermined.getMsg().equals(startTime)) {
            return DateTimeTagEnum.TobeDetermined.getCode();
        }

        if ("今天".equals(startTime)) {
            return DateTimeUtil.timestamp(LocalDate.now());
        }

        return DateTimeUtil.timestamp(startTime, "yyyy-MM-dd");
    }

    private String parseTime(Long time) {
        if (time == null) {
            return null;
        }
        if (DateTimeTagEnum.TobeDetermined.getCode().equals(time)) {
            return DateTimeTagEnum.TobeDetermined.getMsg();
        }

        if (DateTimeUtil.toLocalDateTime(time).toLocalDate().equals(LocalDate.now())) {
            return "今天";
        }

        return DateTimeUtil.format(time, "yyyy-MM-dd");
    }

    private void registerDatePicker(TextView textview) {
        //这个是添加新项目时候弹出的
        //这个是点击右上角三个小圆点弹出的
        AttachListPopupView attachListPopupView = new Builder(this).atView(textview).asAttachList(new String[]{"待定", "日期"}, new int[]{R.drawable.ic_base_to_be_determined, R.drawable.ic_calendar}, (position, text) -> {
            if (position == 0) {
                //如果选择了待定
                refreshTimeShow(DateTimeTagEnum.TobeDetermined.getCode(), textview);
                return;
            }

            if (position == 1) {
                whenChoseSelectTime(textview);
            }
        });
        //点击的时候弹出待定、时间选框
        textview.setOnClickListener(v -> {
            String s = ((TextView) v).getText().toString();
            if (StringUtils.isNotBlank(s)) {
                attachListPopupView.show();
            } else {
                whenChoseSelectTime((TextView) v);
            }
        });
    }

    private void whenChoseSelectTime(TextView textview) {
        final String nowDate = textview.getText().toString();
        updatePickDialogDate(nowDate);
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
            refreshTimeShow(DateTimeUtil.timestamp(localDate), textview);
        });

        datePickerDialog.show();
    }

    private void updatePickDialogDate(String nowDate) {
        Long time = getTime(nowDate);
        LocalDateTime localDateTime;
        if (time == null || DateTimeTagEnum.TobeDetermined.getCode().equals(time)) {
            localDateTime = LocalDateTime.now();
        } else {
            localDateTime = DateTimeUtil.toLocalDateTime(time);
        }
        datePickerDialog.updateDate(localDateTime.getYear(), localDateTime.getMonth().ordinal(), localDateTime.getDayOfMonth());
    }

    private void initProject() {
        if (selectProjectId < 0) {
            viewBinding.topTitleBar.contentBarTitle.setText("新增项目");
        } else {
            viewBinding.topTitleBar.contentBarTitle.setText("编辑项目");
        }
    }

    private void showSelectProjectInfo(Long projectId) {
        if (projectId < 0) {
            return;
        }

        Project project = projectService.queryById(projectId);
        if (project == null) {
            return;
        }

        final String name = project.getName();
        if (StringUtils.isNotBlank(name)) {
            viewBinding.projectName.setText(name);
        }

        final Long startTime = project.getStartTime();
        refreshTimeShow(startTime, viewBinding.projectStartTime);

        final Long endTime = project.getEndTime();
        refreshTimeShow(endTime, viewBinding.projectEndTime);

        final String remark = project.getRemark();
        if (StringUtils.isNotBlank(remark)) {
            viewBinding.projectRemark.setText(remark);
        }
    }

    public void refreshTimeShow(Long time, TextView textView) {
        String projectStartTime = parseTime(time);
        textView.setText(projectStartTime);

        Integer icon = parseIcon(time);
        Drawable drawable = ResourceUtils.getDrawable(icon);
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    private Integer parseIcon(Long time) {
        if (Objects.equals(DateTimeTagEnum.TobeDetermined.getCode(), time)) {
            return R.drawable.ic_project_determined;
        }

        return R.drawable.ic_keyboard_date;
    }

    public static class IntentKey {
        /**
         * 跳转页面传递过来的projectId,非必填
         */
        public static final String PROJECT_ID = "projectId";

        /**
         * 编辑项目的结果回传给上一个页面，或更新或新增
         */
        public static final String EDITE_PROJECT_RESULT = "editeProjectResult";
    }
}