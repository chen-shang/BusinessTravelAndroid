package com.business.travel.app.service;

import java.util.List;

import android.content.Context;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;

public class BillService {

	private final BillDao billDao;

	public BillService(Context context) {
		billDao = AppDatabase.getInstance(context).billDao();
	}

	/**
	 * 根据账单id查询账单信息
	 *
	 * @param id
	 * @return
	 */
	public Bill queryBillById(Long id) {
		return billDao.selectByPrimaryKey(id);
	}

	/**
	 * 删除账单
	 *
	 * @param id
	 */
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

	public Long sumTotalSpendingMoney(Long projectId) {
		return billDao.sumTotalSpendingMoney(projectId);
	}

	public Long sumTotalIncomeMoney(Long projectId) {
		return billDao.sumTotalIncomeMoney(projectId);
	}

	public Long sumTotalSpendingMoney(Long projectId, Long consumeDate) {
		return billDao.sumTotalSpendingMoney(projectId, consumeDate);
	}

	public Long sumTotalIncomeMoney(Long projectId, Long consumeDate) {
		return billDao.sumTotalIncomeMoney(projectId, consumeDate);
	}

	public List<Bill> selectByProjectId(Long projectId) {
		return billDao.selectByProjectId(projectId);
	}
}
