package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Consumption;

/**
 * @author chenshang
 */
@Dao
public interface ConsumptionItemDao extends BaseDao<Consumption> {
	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM Consumption")
	List<Consumption> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM Consumption limit 1")
	Consumption selectOne();

	/**
	 * 查询全部 按照类型并排序
	 */
	@Query("SELECT * FROM Consumption where consumptionType=:consumptionType and isDeleted=1 order by sortId asc")
	List<Consumption> selectByType(String consumptionType);

	/**
	 * 查询最大的sortId
	 */
	@Query("SELECT max(sortId) FROM Consumption where consumptionType=:consumptionType and isDeleted=1")
	Long selectMaxSortIdByType(String consumptionType);

	@Query("SELECT count(*) FROM Consumption")
	Long count();
}
