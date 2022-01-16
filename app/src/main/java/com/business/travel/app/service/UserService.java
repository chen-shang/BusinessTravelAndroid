package com.business.travel.app.service;

import android.content.Context;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.db.AppDatabase;

public class UserService {

    private final UserDao userDao;

    public UserService(Context context) {
        userDao = AppDatabase.getInstance(context).userDao();
    }
}
