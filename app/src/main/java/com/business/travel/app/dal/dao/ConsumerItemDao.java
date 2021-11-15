package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.ConsumerItem;

/**
 * @author chenshang
 */
@Dao
public interface ConsumerItemDao extends BaseDao<ConsumerItem> {
    /**
     * 查询全部
     */
    @Query("SELECT * FROM ConsumerItem")
    List<ConsumerItem> selectAll();

    /**
     * 查询单条
     */
    @Query("SELECT * FROM ConsumerItem limit 1")
    ConsumerItem selectOne();
}
