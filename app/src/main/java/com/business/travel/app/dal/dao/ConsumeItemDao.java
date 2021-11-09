package com.business.travel.app.dal.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Item;

/**
 * @author chenshang
 */
@Dao
public interface ConsumeItemDao extends BaseDao<Item> {
    /**
     * 查询全部
     */
    @Query("SELECT * FROM Item")
    List<Item> selectAll();

    /**
     * 查询单条
     */
    @Query("SELECT * FROM Item limit 1")
    Item selectOne();
}
