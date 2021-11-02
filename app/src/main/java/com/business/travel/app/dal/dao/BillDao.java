package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.business.travel.app.dal.entity.Bill;

/**
 * @author chenshang
 */
@Dao
public interface BillDao {
	/**
	 * 批量添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> batchInsert(List<Bill> records);

	/**
	 * 添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Long insert(Bill record);

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

	/**
	 * 删除用户
	 */
	@Delete
	void delete(Bill record);

	@Delete
	void batchDelete(List<Bill> records);

	@Update
	void update(Bill record);

	@Update
	void batchUpdate(List<Bill> records);
}
