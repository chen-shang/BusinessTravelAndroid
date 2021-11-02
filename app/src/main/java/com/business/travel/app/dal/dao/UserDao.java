package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.business.travel.app.dal.entity.User;

/**
 * @author chenshang
 */
@Dao
public interface UserDao {
	/**
	 * 批量添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> batchInsert(List<User> records);

	/**
	 * 添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Long insert(User record);

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

	/**
	 * 删除用户
	 */
	@Delete
	void delete(User record);

	@Delete
	void batchDelete(List<User> records);

	@Update
	void update(User record);

	@Update
	void batchUpdate(List<User> records);
}
