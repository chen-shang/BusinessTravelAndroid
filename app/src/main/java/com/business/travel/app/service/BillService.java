package com.business.travel.app.service;

import java.util.List;

import android.content.Context;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.utils.JacksonUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * 账单相关业务逻辑
 */
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
	 * 查询一个项目下所有的账单
	 *
	 * @param projectId
	 * @return
	 */
	public List<Bill> queryBillByProjectId(Long projectId) {
		return billDao.selectByProjectId(projectId);
	}

	/**
	 * 删除账单
	 *
	 * @param id
	 */
	public void deleteBillById(Long id) {
		billDao.softDeleteByPrimaryKey(id);
		LogUtils.i("删除账单:" + id + "成功");
	}

	/**
	 * 创建账单
	 *
	 * @param bill
	 */
	public void creatBill(Bill bill) {
		billDao.insert(bill);
		LogUtils.i("创建账单:" + JacksonUtil.toPrettyString(bill) + "成功");
	}

	/**
	 * 统计项目的总支出
	 *
	 * @param projectId
	 * @return
	 */
	public Long sumTotalSpendingMoney(Long projectId) {
		Long totalSpendingMoney = billDao.sumTotalSpendingMoney(projectId);
		LogUtils.i("统计项目的总支出:" + projectId + "->" + totalSpendingMoney);
		return totalSpendingMoney;
	}

	/**
	 * 统计项目的总收入
	 *
	 * @param projectId
	 * @return
	 */
	public Long sumTotalIncomeMoney(Long projectId) {
		Long totalIncomeMoney = billDao.sumTotalIncomeMoney(projectId);
		LogUtils.i("统计项目的总支出:" + projectId + "->" + totalIncomeMoney);
		return totalIncomeMoney;
	}

	/**
	 * 统计项目某天的总支出
	 *
	 * @param projectId
	 * @param consumeDate
	 * @return
	 */
	public Long sumTotalSpendingMoney(Long projectId, Long consumeDate) {
		Long totalSpendingMoney = billDao.sumTotalSpendingMoney(projectId, consumeDate);
		LogUtils.i("统计项目某天的总支出:" + projectId + "->" + totalSpendingMoney);
		return totalSpendingMoney;
	}

	/**
	 * 统计项目某天的总收入
	 *
	 * @param projectId
	 * @param consumeDate
	 * @return
	 */
	public Long sumTotalIncomeMoney(Long projectId, Long consumeDate) {
		Long totalIncomeMoney = billDao.sumTotalIncomeMoney(projectId, consumeDate);
		LogUtils.i("统计项目某天的总收入:" + projectId + "->" + totalIncomeMoney);
		return totalIncomeMoney;
	}

	/**
	 * 更新账单
	 *
	 * @param id
	 * @param bill
	 */
	public void updateBill(Long id, Bill bill) {
		//参数检查
		Preconditions.checkArgument(id != null && id > 0, "id can not be null");
		Preconditions.checkArgument(bill != null, "bill can not be null");

		Bill record = billDao.selectByPrimaryKey(id);
		Preconditions.checkArgument(record != null, "账单不存在");
		//业务逻辑
		String remark = bill.getRemark();
		boolean change = false;
		if (StringUtils.isNotBlank(remark) && !remark.equals(record.getRemark())) {
			record.setRemark(remark);
			change = true;
		}

		Long amount = bill.getAmount();
		if (amount != null && amount != record.getAmount()) {
			record.setAmount(amount);
			change = true;
		}

		Long consumeDate = bill.getConsumeDate();
		if (consumeDate != null && consumeDate != record.getConsumeDate()) {
			record.setConsumeDate(consumeDate);
			change = true;
		}
		String consumptionIds = bill.getConsumptionIds();
		if (StringUtils.isNotBlank(consumptionIds) && !consumptionIds.equals(record.getConsumptionIds())) {
			record.setConsumptionIds(consumptionIds);
			change = true;
		}

		String memberIds = bill.getMemberIds();
		if (StringUtils.isNotBlank(memberIds) && !memberIds.equals(record.getMemberIds())) {
			record.setMemberIds(memberIds);
			change = true;
		}
		String iconDownloadUrl = bill.getIconDownloadUrl();
		if (StringUtils.isNotBlank(iconDownloadUrl) && !iconDownloadUrl.equals(record.getIconDownloadUrl())) {
			record.setIconDownloadUrl(iconDownloadUrl);
			change = true;
		}

		Long projectId = bill.getProjectId();
		if (projectId != null && projectId != record.getProjectId()) {
			record.setProjectId(projectId);
			change = true;
		}

		String name = bill.getName();
		if (StringUtils.isNotBlank(name) && !name.equals(record.getName())) {
			record.setName(name);
			change = true;
		}

		String consumptionType = bill.getConsumptionType();
		if (StringUtils.isNotBlank(consumptionType) && !consumptionType.equals(record.getConsumptionType())) {
			record.setConsumptionIds(consumptionType);
			change = true;
		}
		
		//结果处理
		if (change) {
			billDao.update(record);
		}
	}
}
