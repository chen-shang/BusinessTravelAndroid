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
public interface ConsumerItemDao extends BaseDao<ConsumptionItem> {
    /**
     * 查询全部
     */
    @Query("SELECT * FROM ConsumptionItem")
    List<ConsumptionItem> selectAll();

    /**
     * 查询单条
     */
    @Query("SELECT * FROM ConsumptionItem limit 1")
    ConsumptionItem selectOne();
}
