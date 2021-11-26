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
	@Query("SELECT * FROM project where isDeleted=1")
	List<Project> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM project where name=:projectName and isDeleted=1")
	Project selectByName(String projectName);

	@Query("SELECT * FROM project where isDeleted=1 order by modifyTime asc limit 1")
	Project selectLatestModify();

	@Query("update project set isDeleted=0 where id=:id")
	void softDelete(Long id);
}
