package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.enums.ConsumptionTypeEnum;

/**
 * @author chenshang
 */
@Dao
public interface BillDao extends BaseDao<Bill> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM bill")
	List<Bill> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM bill limit 1")
	Bill selectOne();

	/**
	 * 统计项目的总支出
	 *
	 * @param projectId
	 * @return
	 * @see ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='SPENDING' and isDeleted=1")
	Long sumTotalSpendingMoney(Long projectId);

	/**
	 * 统计项目的总收入
	 *
	 * @param projectId
	 * @return
	 * @see ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='INCOME' and isDeleted=1")
	Long sumTotalIncomeMoney(Long projectId);

	@Query("SELECT * FROM bill where projectId=:projectId and isDeleted=1")
	List<Bill> selectByProjectId(Long projectId);
}
