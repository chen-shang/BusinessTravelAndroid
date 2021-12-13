package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Bill;

/**
 * @author chenshang
 */
@Dao
public interface BillDao extends BaseDao<Bill> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM bill where isDeleted=1")
	List<Bill> selectAll();

	/**
	 * 统计项目的总支出
	 *
	 * @param projectId
	 * @return
	 * @see com.business.travel.vo.enums.ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='SPENDING' and isDeleted=1")
	Long sumTotalSpendingMoney(Long projectId);

	/**
	 * 统计项目的总收入
	 *
	 * @param projectId
	 * @return
	 * @see com.business.travel.vo.enums.ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='INCOME' and isDeleted=1")
	Long sumTotalIncomeMoney(Long projectId);

	/**
	 * 统计项目某天的总支出
	 *
	 * @param projectId
	 * @return
	 * @see com.business.travel.vo.enums.ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='SPENDING' and isDeleted=1 and consumeDate=:consumeDate")
	Long sumTotalSpendingMoney(Long projectId, Long consumeDate);

	/**
	 * 统计项目某天的总收入
	 *
	 * @param projectId
	 * @return
	 * @see com.business.travel.vo.enums.ConsumptionTypeEnum
	 */
	@Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='INCOME' and isDeleted=1 and consumeDate=:consumeDate")
	Long sumTotalIncomeMoney(Long projectId, Long consumeDate);

	/**
	 * 查询一个项目下所有的账单
	 *
	 * @param projectId
	 * @return
	 */
	@Query("SELECT * FROM bill where projectId=:projectId and isDeleted=1")
	List<Bill> selectByProjectId(Long projectId);

	@Query("update bill set isDeleted=0 where projectId=:id and isDeleted!=0")
	void softDeleteByProjectId(Long id);

	@Query("update bill set isDeleted=0 where id=:id and isDeleted!=0")
	void softDeleteByPrimaryKey(Long id);

	@Query("SELECT * FROM bill where id=:id limit 1")
	Bill selectByPrimaryKey(Long id);
}
