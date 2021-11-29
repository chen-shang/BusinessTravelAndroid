package com.business.travel.app.service;

import java.util.Date;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.room.Transaction;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.utils.DateTimeUtil;
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
		projectDao.softDelete(projectId);
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
			project.setModifyTime((DateTimeUtil.format(new Date())));
			projectDao.update(project);
			return project;
		}

		//如果不存在的项目则直接新建
		project = new Project();
		project.setName(projectName);
		project.setStartTime(null);
		project.setEndTime(null);
		project.setRemark(null);
		project.setCreateTime(DateTimeUtil.format(new Date()));
		project.setModifyTime(DateTimeUtil.format(new Date()));
		project.setIsDeleted(DeleteEnum.NOT_DELETE.getCode());
		projectDao.insert(project);

		//然后在查询刚才的项目,这里主要目的在于查询主键出来
		return projectDao.selectByName(projectName);
	}
}
