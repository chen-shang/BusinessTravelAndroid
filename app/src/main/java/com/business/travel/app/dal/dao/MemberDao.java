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
	@Query("SELECT * FROM Member where isDeleted=1")
	List<Member> selectByPrimaryKeys();

	/**
	 * 查询全部
	 */
	@Query("SELECT max(sortId) FROM Member where isDeleted=1")
	Long selectMaxSort();

	/**
	 * 数量统计
	 *
	 * @return
	 */
	@Query("SELECT count(*) FROM Member")
	Long count();

	/**
	 * 更新排序
	 *
	 * @param id
	 * @param sortId
	 */
	@Query("update member set sortId=:sortId where id=:id")
	void updateSort(Long id, Long sortId);

	/**
	 * 逻辑删除
	 *
	 * @param id
	 */
	@Query("update member set isDeleted=0 where id=:id")
	void softDelete(Long id);

	/**
	 * 按照主键id批量查询
	 *
	 * @param ids
	 * @return
	 */
	@Query("SELECT * FROM member where id in (:idList)")
	List<Member> selectByPrimaryKeys(List<Long> idList);
}
