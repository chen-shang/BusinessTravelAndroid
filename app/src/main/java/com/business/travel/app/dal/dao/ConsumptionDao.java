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

	@Query("SELECT * FROM Consumption where name=:name and isDeleted=1")
	Consumption selectByName(String name);

	/**
	 * 查询最大的sortId
	 */
	@Query("SELECT max(sortId) FROM Consumption where consumptionType=:consumptionType and isDeleted=1")
	Long selectMaxSortIdByType(String consumptionType);

	/**
	 * 统计总数,包括删除的
	 *
	 * @return
	 */
	@Query("SELECT count(*) FROM Consumption")
	Long count();

	/**
	 * 更新排序
	 *
	 * @param id
	 * @param sortId
	 */
	@Query("update consumption set sortId=:sortId where id=:id")
	void updateSort(Long id, Long sortId);

	/**
	 * 逻辑删除
	 *
	 * @param id
	 */
	@Query("update consumption set isDeleted=0 where id=:id")
	void softDelete(Long id);

	/**
	 * 按照主键id批量查询
	 *
	 * ps:这里会把删除的也查询出来,因为详情展示的时候还要展示的
	 *
	 * @param ids
	 * @return
	 */
	@Query("SELECT * from consumption where id in (:ids)")
	List<Consumption> selectByPrimaryKeys(List<Long> ids);
}
