package com.business.travel.app.ui.activity.project;

import java.time.LocalDateTime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

public class EditProjectActivity extends BaseActivity<ActivityEditProjectBinding> {

	private DatePickerDialog datePickerDialog;
	/**
	 * 当前编辑的项目信息,如果存在则是更新,否则就是新增
	 */
	private Long selectProjectId;
	private ProjectService projectService;

	@Override
	protected void inject() {
		projectService = new ProjectService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//从别的页面传递过来的项目id
		initProject();
		registerImageButtonBack();
		datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {}, DateTimeUtil.now().getYear(), DateTimeUtil.now().getMonth().getValue() - 1, DateTimeUtil.now().getDayOfMonth());
		registerDateTicker(viewBinding.projectEndTime);
		registerDateTicker(viewBinding.projectStartTime);

		viewBinding.UIEditProjectActivityImageButtonSave.setOnClickListener(v -> {
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
				projectService.updateProjectById(selectProjectId, project);
			} else {
				final Project newProject = projectService.createIfNotExist(viewBinding.projectName.getText().toString());
				projectService.updateProjectById(newProject.getId(), project);
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

	@Override
	protected void onStart() {
		super.onStart();
		refreshSelectProjectInfo();
	}

	private void refreshSelectProjectInfo() {
		if (selectProjectId == null || selectProjectId < 0) {
			return;
		}

		Project project = projectService.queryById(selectProjectId);
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