package com.business.travel.app.ui.activity.project;

import java.time.LocalDateTime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

public class EditProjectActivity extends BaseActivity<ActivityEditProjectBinding> {

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

	@Override
	protected void inject() {
		projectService = new ProjectService(this);
		datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {}, DateTimeUtil.now().getYear(), DateTimeUtil.now().getMonth().getValue() - 1, DateTimeUtil.now().getDayOfMonth());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//从别的页面传递过来的项目id
		initProject();
		//注册返回按钮点击事件
		registerImageButtonBack();
		//注册项目开始结束时间点击事件
		registerDateTicker(viewBinding.projectEndTime);
		registerDateTicker(viewBinding.projectStartTime);
		//注册右上角对号保存按钮点击事件
		registerSaveButton(viewBinding.UIEditProjectActivityImageButtonSave);
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshSelectProjectInfo(selectProjectId);
	}

	private void registerSaveButton(View saveButton) {
		saveButton.setOnClickListener(v -> {
			try {
				Project project = new Project();
				project.setName(viewBinding.projectName.getText().toString());
				String startTime = viewBinding.projectStartTime.getText().toString();
				if (StringUtils.isNotBlank(startTime)) {
					project.setStartTime(DateTimeUtil.timestamp(startTime, "yyyy-MM-dd"));
				}
				String endTime = viewBinding.projectEndTime.getText().toString();
				if (StringUtils.isNotBlank(endTime)) {
					project.setEndTime(DateTimeUtil.timestamp(endTime, "yyyy-MM-dd"));
				}

				if (selectProjectId != null) {
					projectService.updateProject(selectProjectId, project);
				} else {
					projectService.createProject(project);
				}
			} catch (Exception e) {
				LogUtils.e("保存失败", e);
				LogToast.errorShow("保存失败:" + e.getMessage());
			}
		});
	}

	private void registerDateTicker(TextView projectEndTime) {
		projectEndTime.setOnClickListener(v -> {
			final String nowDate = ((TextView)v).getText().toString();
			if (StringUtils.isNotBlank(nowDate)) {
				final LocalDateTime localDateTime = DateTimeUtil.parseLocalDateTime(nowDate, "yyyy-MM-dd");
				datePickerDialog.updateDate(localDateTime.getYear(), localDateTime.getMonth().getValue() - 1, localDateTime.getDayOfMonth());
			}

			datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
				final LocalDateTime localDate = LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0);
				((TextView)v).setText(DateTimeUtil.format(localDate, "yyyy-MM-dd"));
			});

			datePickerDialog.show();
		});
	}

	private void initProject() {
		long projectId = getIntent().getLongExtra("projectId", -1L);
		if (projectId < 0) {
			selectProjectId = null;
			viewBinding.title.setText("新增项目");
		} else {
			selectProjectId = projectId;
			viewBinding.title.setText("编辑项目");
		}
	}

	private void refreshSelectProjectInfo(Long projectId) {
		if (projectId == null || projectId < 0) {
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
		if (startTime != null) {
			viewBinding.projectStartTime.setText(DateTimeUtil.format(startTime, "yyyy-MM-dd"));
		}
		final Long endTime = project.getEndTime();
		if (endTime != null) {
			viewBinding.projectEndTime.setText(DateTimeUtil.format(endTime, "yyyy-MM-dd"));
		}
	}

	private void registerImageButtonBack() {
		//返回按钮点击后
		viewBinding.UIEditProjectActivityImageButtonBack.setOnClickListener(v -> this.finish());
	}
}