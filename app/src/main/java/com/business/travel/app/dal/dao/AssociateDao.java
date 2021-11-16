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
public interface AssociateDao extends BaseDao<AssociateItem> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM AssociateItem")
	List<AssociateItem> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM AssociateItem limit 1")
	AssociateItem selectOne();

}
