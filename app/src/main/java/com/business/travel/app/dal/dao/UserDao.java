package com.business.travel.app.dal.dao;

import androidx.room.Dao;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Project;

/**
 * @author chenshang
 */
@Dao
public interface UserDao extends BaseDao<Project> {
}
