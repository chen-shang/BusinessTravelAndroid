package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.User;

/**
 * @author chenshang
 */
@Dao
public interface UserDao extends BaseDao<User> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM user")
	List<User> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM user limit 1")
	User selectOne();
}
