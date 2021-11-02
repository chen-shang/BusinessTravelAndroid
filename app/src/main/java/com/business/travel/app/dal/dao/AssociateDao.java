package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Associate;

/**
 * @author chenshang
 */
@Dao
public interface AssociateDao extends BaseDao<Associate> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM associate")
	List<Associate> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM associate limit 1")
	Associate selectOne();

}
