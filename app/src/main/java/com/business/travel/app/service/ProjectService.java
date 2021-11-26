package com.business.travel.app.service;

import android.content.Context;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;

/**
 *
 */
public class ProjectService {
	private final ProjectDao projectDao;

	public ProjectService(Context context) {
		projectDao = AppDatabase.getInstance(context).projectDao();
	}

	public void softDeleteProjectWithBill(Long id) {
		projectDao.softDelete(id);
	}
}
