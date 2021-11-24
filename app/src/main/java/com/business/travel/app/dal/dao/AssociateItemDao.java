package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.AssociateItem;

/**
 * @author chenshang
 */
@Dao
public interface AssociateItemDao extends BaseDao<AssociateItem> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM associateItem")
	List<AssociateItem> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM associateItem limit 1")
	AssociateItem selectOne();

	/**
	 * 查询全部
	 */
	@Query("SELECT max(sortId) FROM associateItem where isDeleted=1")
	Long selectMaxSort();
}
