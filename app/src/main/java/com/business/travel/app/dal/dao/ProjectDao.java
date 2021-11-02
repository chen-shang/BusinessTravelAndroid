package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.business.travel.app.dal.entity.Project;

/**
 * @author chenshang
 */
@Dao
public interface ProjectDao {

	/**
	 * 批量添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	List<Long> batchInsert(List<Project> records);

	/**
	 * 添加
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	Long insert(Project record);

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM project")
	List<Project> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM project limit 1")
	Project selectOne();

	/**
	 * 删除用户
	 */
	@Delete
	void delete(Project record);

	@Delete
	void batchDelete(List<Project> records);

	@Update
	void update(Project record);

	@Update
	void batchUpdate(List<Project> records);
}
