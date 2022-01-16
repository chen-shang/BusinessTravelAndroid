package com.business.travel.app.dal.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.User;

import java.util.List;

/**
 * @author chenshang
 */
@Dao
public interface UserDao extends BaseDao<User> {

    /**
     * 查询全部
     */
    @Query("SELECT * FROM User where isDeleted=1")
    List<User> selectAll();

    /**
     * 查询全部
     */
    @Query("SELECT * FROM User where isDeleted=1 limit 1")
    User selectOne();
}
