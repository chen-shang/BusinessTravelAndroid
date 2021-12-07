package com.business.travel.app.ui.activity.project;

import java.util.Date;

import android.os.Bundle;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

public class EditProjectActivity extends BaseActivity<ActivityEditProjectBinding> {

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
		viewBinding.UIEditProjectActivityImageButtonSave.setOnClickListener(v -> {

			Project project = new Project();
			project.setId(selectProjectId);
			project.setName(viewBinding.projectName.getText().toString());
			String startTime = viewBinding.UITextViewStartTime.getText().toString();
			if ("今天".equals(startTime)) {
				startTime = DateTimeUtil.format(new Date());
			}
			project.setStartTime(startTime);

			String endTime = viewBinding.UIKeyboardItemTextViewEndTime.getText().toString();
			if ("待定".equals(endTime)) {
				endTime = "";
			}
			project.setEndTime(endTime);
			projectService.updateProjectById(selectProjectId, project);
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
		viewBinding.projectName.setText(project.getName());
		viewBinding.UITextViewStartTime.setText(DateTimeUtil.format(project.getStartTime(), "yyyy.MM.dd"));
		String endTime = project.getEndTime();
		if (StringUtils.isNotBlank(endTime)) {
			viewBinding.UIKeyboardItemTextViewEndTime.setText(DateTimeUtil.format(project.getEndTime(), "yyyy.MM.dd"));
		}
	}

	private void registerImageButtonBack() {
		//返回按钮点击后
		viewBinding.UIEditProjectActivityImageButtonBack.setOnClickListener(v -> {
			this.finish();
		});
	}
}