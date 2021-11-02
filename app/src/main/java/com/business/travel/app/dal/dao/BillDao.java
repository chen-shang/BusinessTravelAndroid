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
}
