package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Member;

/**
 * @author chenshang
 */
@Dao
public interface MemberDao extends BaseDao<Member> {

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM Member")
	List<Member> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM Member limit 1")
	Member selectOne();

	/**
	 * 查询全部
	 */
	@Query("SELECT max(sortId) FROM Member where isDeleted=1")
	Long selectMaxSort();

	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM Member where isDeleted=:isDeleted")
	List<Member> selectAll(Integer isDeleted);

	@Query("SELECT count(*) FROM Member")
	Long count();

	@Query("update member set sortId=:sortId where id=:id")
	void updateSort(Long id, Long sortId);
}
