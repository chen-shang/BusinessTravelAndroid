package com.business.travel.app.dal.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.business.travel.app.dal.entity.User;

/**
 * @author chenshang
 */
@Dao
public interface UserDao {
	/**
	 * 添加用户
	 *
	 * @param users
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insertUsers(User... users);

	/**
	 * 查询用户
	 *
	 * @return
	 */
	@Query("SELECT * FROM user")
	User[] loadAllUsers();

	/**
	 * 删除用户
	 *
	 * @param users
	 */
	@Delete
	void deleteUsers(User... users);
}
