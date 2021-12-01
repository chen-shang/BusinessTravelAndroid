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
public interface ConsumptionDao extends BaseDao<Consumption> {
	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM Consumption where isDeleted=1")
	List<Consumption> selectAll();

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

	@Query("update consumption set sortId=:sortId where id=:id")
	void updateSort(Long id, Long sortId);

	@Query("update consumption set isDeleted=0 where id=:id")
	void softDelete(Long id);

	@Query("SELECT * from consumption where id in (:ids)")
	List<Consumption> selectByIds(List<Long> ids);
}
