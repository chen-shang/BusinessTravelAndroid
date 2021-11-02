package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.business.travel.app.dal.entity.Associate;

/**
 * @author chenshang
 */
@Dao
public interface AssociateDao {
	/**
	 * 批量添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> batchInsert(List<Associate> records);

	/**
	 * 添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Long insert(Associate record);

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

	/**
	 * 删除用户
	 */
	@Delete
	void delete(Associate record);

	@Delete
	void batchDelete(List<Associate> records);

	@Update
	void update(Associate record);

	@Update
	void batchUpdate(List<Associate> records);
}
