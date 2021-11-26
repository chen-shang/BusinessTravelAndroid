package com.business.travel.app.service;

import android.content.Context;
import androidx.room.Transaction;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;

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
}
