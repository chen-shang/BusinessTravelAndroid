package com.business.travel.app.dal.dao.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

@Dao
public interface BaseDao<T> {

	/**
	 * 批量添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> batchInsert(List<T> records);

	/**
	 * 添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Long insert(T record);

	/**
	 * 删除用户
	 */
	@Delete
	void delete(T record);

	/**
	 * 逻辑删除
	 *
	 * @param record
	 */
	@Update
	void softDelete(T record);

	@Delete
	void batchDelete(List<T> records);

	@Update
	void update(T record);

	@Update
	void batchUpdate(List<T> records);

}
