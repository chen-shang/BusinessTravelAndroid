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
     * 更新用户信息
     */
    public void upsertUser(User record) {
        User user = userDao.selectOne();
        //不存在则新增
        if (user == null) {
            initUser(record);
            return;
        }
        boolean update = false;
        //存在则更新
        if (record.getAgree() != null) {
            user.setAgree(record.getAgree());
            update = true;
        }

        if (update) {
            userDao.update(user);
        }
    }

    private void initUser(User record) {
        userDao.insert(record);
    }
}
