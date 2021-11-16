package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.ConsumptionItem;

/**
 * @author chenshang
 */
@Dao
public interface ConsumptionItemDao extends BaseDao<ConsumptionItem> {
	/**
	 * 查询全部
	 */
	@Query("SELECT * FROM consumptionItem")
	List<ConsumptionItem> selectAll();

	/**
	 * 查询单条
	 */
	@Query("SELECT * FROM consumptionItem limit 1")
	ConsumptionItem selectOne();
}
