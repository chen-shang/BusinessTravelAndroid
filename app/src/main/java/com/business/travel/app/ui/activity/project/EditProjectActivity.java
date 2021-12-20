package com.business.travel.app.ui.activity.project;

import java.time.LocalDate;
import java.time.LocalDateTime;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.blankj.utilcode.util.ResourceUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityEditProjectBinding;
import com.business.travel.app.enums.DateTimeTagEnum;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import org.apache.commons.lang3.StringUtils;

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
			//开始时间
			String projectStartTime = viewBinding.projectStartTime.getText().toString();
			Long startTime = getTime(projectStartTime);
			project.setStartTime(startTime);

			//结束时间
			String projectEndTime = viewBinding.projectEndTime.getText().toString();
			Long endTime = getTime(projectEndTime);
			project.setEndTime(endTime);

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

	private Long getTime(String startTime) {
		if (StringUtils.isNotBlank(startTime)) {
			return null;
		}
		if (DateTimeTagEnum.TobeDetermined.getMsg().equals(startTime)) {
			return DateTimeTagEnum.TobeDetermined.getCode();
		}
		return DateTimeUtil.timestamp(startTime, "yyyy-MM-dd");
	}

	private void registerDatePicker(TextView textview) {
		//这个是添加新项目时候弹出的
		//这个是点击右上角三个小圆点弹出的
		AttachListPopupView attachListPopupView = new Builder(this).atView(textview).asAttachList(new String[] {"待定", "日期"}, new int[] {R.drawable.ic_base_to_be_determined, R.drawable.ic_calendar}, (position, text) -> {
			if (position == 0) {
				//如果选择了待定
				whenChoseTobeDetermined(textview);
				return;
			}

			if (position == 1) {
				whenChoseSelectTime(textview);
			}
		});
		//点击的时候弹出待定、时间选框
		textview.setOnClickListener(v -> attachListPopupView.show());
	}

	private void whenChoseSelectTime(TextView textview) {
		final String nowDate = textview.getText().toString();
		updatePickDialogDate(nowDate);
		datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
			LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
			textview.setText(DateTimeUtil.format(localDate, "yyyy-MM-dd"));
		});

		datePickerDialog.show();
	}

	private void updatePickDialogDate(String nowDate) {
		if (StringUtils.isBlank(nowDate)) {
			return;
		}

		if (DateTimeTagEnum.TobeDetermined.getMsg().equals(nowDate)) {
			final LocalDateTime localDateTime = DateTimeUtil.now();
			datePickerDialog.updateDate(localDateTime.getYear(), localDateTime.getMonth().ordinal(), localDateTime.getDayOfMonth());
			return;
		}

		final LocalDateTime localDateTime = DateTimeUtil.parseLocalDateTime(nowDate, "yyyy-MM-dd");
		datePickerDialog.updateDate(localDateTime.getYear(), localDateTime.getMonth().ordinal(), localDateTime.getDayOfMonth());
	}

	private void whenChoseTobeDetermined(TextView textview) {
		Drawable drawable = ResourceUtils.getDrawable(R.drawable.ic_base_to_be_determined);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
		textview.setCompoundDrawables(drawable, null, null, null);
		textview.setText("待定");
	}

	private void tobeDetermined(TextView textview) {
		Drawable drawable = ResourceUtils.getDrawable(R.drawable.ic_base_to_be_determined);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2, drawable.getIntrinsicHeight() / 2);
		textview.setCompoundDrawables(drawable, null, null, null);
		textview.setText("待定");
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
			if (-1 == startTime) {
				//显示待定
				tobeDetermined(viewBinding.projectStartTime);
			} else {
				viewBinding.projectStartTime.setText(DateTimeUtil.format(startTime, "yyyy-MM-dd"));
			}
		}
		final Long endTime = project.getEndTime();
		if (endTime != null) {
			if (-1 == endTime) {
				//显示待定
				tobeDetermined(viewBinding.projectEndTime);
			} else {
				viewBinding.projectEndTime.setText(DateTimeUtil.format(endTime, "yyyy-MM-dd"));
			}
		}

		final String remark = project.getRemark();
		if (StringUtils.isNotBlank(remark)) {
			viewBinding.projectRemark.setText(remark);
		}
	}

}