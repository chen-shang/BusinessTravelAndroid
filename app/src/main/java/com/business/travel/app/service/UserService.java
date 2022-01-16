package com.business.travel.app.service;

import android.content.Context;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.User;

/**
 * 用户相关的业务逻辑
 */
public class UserService {

    private final UserDao userDao;

    public UserService(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
    }

    /**
     * 查询当前用户信息
     *
     * @return
     */
    public User queryUser() {
        return userDao.selectOne();
    }

    /**
     * 初始化本机用户
     */
    public void initUser() {

    }
}
