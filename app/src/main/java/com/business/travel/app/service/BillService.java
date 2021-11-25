package com.business.travel.app.service;

import android.content.Context;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;

public class BillService {

	private final Context context;
	private BillDao billDao;

	public BillService(Context context) {
		this.context = context;
		billDao = AppDatabase.getInstance(context).billDao();
	}

}
