package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Project;

/**
 * @author chenshang
 */
@Dao
public interface ProjectDao extends BaseDao<Project> {
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
	 * 查询单条
	 */
	@Query("SELECT * FROM project where name=:projectName")
	Project selectByName(String projectName);

	@Query("SELECT * FROM project order by modify_time asc limit 1")
	Project selectLatestModify();
}
