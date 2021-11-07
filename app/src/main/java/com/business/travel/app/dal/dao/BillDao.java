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
	@Query("SELECT * FROM bill")
	List<Bill> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM bill limit 1")
	Bill selectOne();

	@Query("SELECT sum(amount) FROM bill where projectId=:projectId")
	Long sumTotalMoney(Long projectId);

	@Query("SELECT * FROM bill where projectId=:projectId")
	List<Bill> selectByProjectId(Long projectId);

	@Query("SELECT distinct consumeDate FROM bill where projectId=:projectId")
	List<String> selectConsumeDateByProjectId(Long projectId);

	@Query("SELECT * FROM bill where projectId=:projectId and consumeDate=:consumeDate")
	List<Bill> selectByProjectIdAndConsumeDate(Long projectId, String consumeDate);
}
