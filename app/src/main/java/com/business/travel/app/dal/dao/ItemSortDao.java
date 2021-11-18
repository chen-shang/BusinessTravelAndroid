package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.ItemSort;

/**
 * @author chenshang
 */
@Dao
public interface ItemSortDao extends BaseDao<ItemSort> {
	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM itemSort")
	List<ItemSort> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM itemSort limit 1")
	ItemSort selectOne();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM itemSort where itemType=:type limit 1")
	ItemSort selectOneByType(String type);
}
