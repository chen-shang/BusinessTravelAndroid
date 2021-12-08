package com.business.travel.app.service;

import java.util.List;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.room.Transaction;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class ProjectService {
	private final ProjectDao projectDao;
	private final BillDao billDao;

	public ProjectService(Context context) {
		projectDao = AppDatabase.getInstance(context).projectDao();
		billDao = AppDatabase.getInstance(context).billDao();
	}

	@Transaction
	public void softDeleteProjectWithBill(Long projectId) {
		Project project = projectDao.selectByPrimaryKey(projectId);
		project.setName(project.getName() + "_" + projectId);
		project.setIsDeleted(DeleteEnum.DELETE.getCode());
		project.setModifyTime(DateTimeUtil.timestamp());
		projectDao.update(project);
		billDao.softDeleteByProjectId(projectId);
	}

	/**
	 * 根据项目名称创建项目如果项目不存在的话
	 *
	 * @param projectName
	 * @return
	 */
	@Nullable
	public Project createIfNotExist(String projectName) {
		if (StringUtils.isBlank(projectName)) {
			throw new IllegalArgumentException("请输入项目名称");
		}
		//按照项目名称查询项目信息
		Project project = projectDao.selectByName(projectName);
		//如果已经存在的项目,则直接返回
		if (project != null) {
			//注意这里更新了一下最后的访问时间
			project.setModifyTime((DateTimeUtil.timestamp()));
			projectDao.update(project);
			return project;
		}

		//如果不存在的项目则直接新建
		project = new Project();
		project.setName(projectName);
		project.setStartTime(DateTimeUtil.timestamp());
		project.setEndTime(null);
		project.setRemark("");
		project.setCreateTime(DateTimeUtil.timestamp());
		project.setModifyTime(DateTimeUtil.timestamp());
		project.setIsDeleted(DeleteEnum.NOT_DELETE.getCode());
		projectDao.insert(project);

		//然后在查询刚才的项目,这里主要目的在于查询主键出来
		return projectDao.selectByName(projectName);
	}

	public Project queryById(Long id) {
		return projectDao.selectByPrimaryKey(id);
	}

	public Project queryLatestModify() {
		return projectDao.selectLatestModify();
	}

	public List<Project> queryAll() {
		return projectDao.selectAll();
	}

	public void updateProject(Long projectId, Project project) {
		LogUtils.i("更新项目 projectId:" + projectId + " -> project:" + JacksonUtil.toPrettyString(project));
		Preconditions.checkArgument(projectId != null, "请选择对应的差旅项目");
		Project record = projectDao.selectByPrimaryKey(projectId);
		Preconditions.checkArgument(record != null, "差旅项目不存在");
		Preconditions.checkArgument(DeleteEnum.DELETE.getCode() != record.getIsDeleted(), "差旅项目已经被删除,请不要重复操作");

		if (StringUtils.isNotBlank(project.getName())) {
			record.setName(project.getName());
		}
		if (project.getStartTime() != null) {
			record.setStartTime(project.getStartTime());
		}
		if (project.getEndTime() != null) {
			record.setEndTime(project.getEndTime());
		}
		if (project.getRemark() != null) {
			record.setRemark(project.getRemark());
		}
		if (project.getStatus() != null) {
			record.setStatus(project.getStatus());
		}
		if (project.getSortId() != null) {
			record.setSortId(project.getSortId());
		}
		Long modifyTime = project.getModifyTime();
		if (modifyTime != null) {
			record.setModifyTime(modifyTime);
		}
		projectDao.update(record);
	}

	public Long createProject(Project project) {
		LogUtils.i("新建项目 project:" + JacksonUtil.toPrettyString(project));
		Preconditions.checkArgument(project != null, "param can not be null");
		String name = project.getName();
		Preconditions.checkArgument(StringUtils.isNotBlank(name), "project name can not be null");
		Project record = projectDao.selectByName(name);
		Preconditions.checkArgument(record == null, "项目已存在");
		
		project.setCreateTime(DateTimeUtil.timestamp());
		project.setModifyTime(DateTimeUtil.timestamp());
		projectDao.insert(project);
		record = projectDao.selectByName(name);
		return record.getId();
	}
}
