package com.business.travel.app.dal.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.business.travel.app.dal.dao.*;
import com.business.travel.app.dal.entity.*;

/**
 * @author chenshang
 */
@Database(entities = {Member.class, Bill.class, Consumption.class, Project.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app.db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

    /**
     * 人员表
     */
    public abstract MemberDao memberDao();

    /**
     * 账单表
     */
    public abstract BillDao billDao();

    /**
     * 消费项
     */
    public abstract ConsumptionDao consumptionDao();

    /**
     * 项目表
     */
    public abstract ProjectDao projectDao();

    /**
     * 用户信息
     */
    public abstract UserDao userDao();
}
