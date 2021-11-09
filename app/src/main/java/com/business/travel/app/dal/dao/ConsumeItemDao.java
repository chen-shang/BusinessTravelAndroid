package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.ConsumeItem;

/**
 * @author chenshang
 */
@Dao
public interface ConsumeItemDao extends BaseDao<ConsumeItem> {
    /**
     * 查询全部
     */
    @Query("SELECT * FROM consumeitem")
    List<ConsumeItem> selectAll();

    /**
     * 查询单条
     */
    @Query("SELECT * FROM consumeitem limit 1")
    ConsumeItem selectOne();
}
