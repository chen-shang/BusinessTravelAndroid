package com.business.travel.app.service;

import android.content.Context;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;

public class BillService {

	private final BillDao billDao;

	public BillService(Context context) {
		billDao = AppDatabase.getInstance(context).billDao();
	}

	public Bill queryBillById(Long id) {
		return billDao.selectByPrimaryKey(id);
	}

	public void deleteBillById(Long id) {
		billDao.softDeleteById(id);
	}

	/**
	 * 创建账单
	 *
	 * @param bill
	 */
	public void creatBill(Bill bill) {
		billDao.insert(bill);
	}

	public Long sumTotalSpendingMoney(Long id) {
		return billDao.sumTotalSpendingMoney(id);
	}

	public Long sumTotalIncomeMoney(Long id) {
		return billDao.sumTotalIncomeMoney(id);
	}
}
