package com.business.travel.app.ui.activity.project;

import java.time.LocalDate;
import java.time.LocalDateTime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.Try;
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
		datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
		}, DateTimeUtil.now().getYear(), DateTimeUtil.now().getMonth().getValue() - 1, DateTimeUtil.now().getDayOfMonth());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//从别的页面传递过来的项目id
		initProject();
		//注册项目开始结束时间点击事件
		registerDatePicker(viewBinding.projectEndTime);
		registerDatePicker(viewBinding.projectStartTime);
		//注册右上角对号保存按钮点击事件
		registerSaveButton(viewBinding.topTitleBar.contentBarRightIcon);
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshSelectProjectInfo(selectProjectId);
	}

	private void registerSaveButton(View saveButton) {
		saveButton.setOnClickListener(v -> Try.of(() -> {
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

			String remark = viewBinding.projectRemark.getText().toString();
			project.setRemark(remark);

			if (selectProjectId != null) {
				projectService.updateProject(selectProjectId, project);
			} else {
				projectService.createProject(project);
			}

			//退出
			this.finish();
		}));
	}

	private void registerDatePicker(TextView textview) {
		textview.setOnClickListener(v -> {
			final String nowDate = ((TextView)v).getText().toString();
			if (StringUtils.isNotBlank(nowDate)) {
				final LocalDateTime localDateTime = DateTimeUtil.parseLocalDateTime(nowDate, "yyyy-MM-dd");
				datePickerDialog.updateDate(localDateTime.getYear(), localDateTime.getMonth().ordinal(), localDateTime.getDayOfMonth());
			}
			datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
				LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
				((TextView)v).setText(DateTimeUtil.format(localDate, "yyyy-MM-dd"));
			});

			datePickerDialog.show();
		});
	}

	private void initProject() {
		long projectId = getIntent().getLongExtra("projectId", -1L);
		if (projectId < 0) {
			selectProjectId = null;
			viewBinding.topTitleBar.contentBarTitle.setText("新增项目");
		} else {
			selectProjectId = projectId;
			viewBinding.topTitleBar.contentBarTitle.setText("编辑项目");
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

		final String remark = project.getRemark();
		if (StringUtils.isNotBlank(remark)) {
			viewBinding.projectRemark.setText(remark);
		}
	}

	private void registerImageButtonBack(ImageButton imageButton) {
		//返回按钮点击后
		imageButton.setOnClickListener(v -> this.finish());
	}
}