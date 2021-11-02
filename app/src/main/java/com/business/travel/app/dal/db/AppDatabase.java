package com.business.travel.app.dal.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.business.travel.app.dal.dao.AssociateDao;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.entity.Associate;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.dal.entity.User;

/**
 * @author chenshang
 */
@Database(entities = {
		User.class,
		Bill.class,
		Project.class,
		Associate.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase INSTANCE;

	public static synchronized AppDatabase getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE =
					Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app.db")
							.allowMainThreadQueries()
							.build();
		}
		return INSTANCE;
	}

	public abstract UserDao userDao();

	public abstract AssociateDao associateDao();

	public abstract BillDao billDao();

	public abstract ProjectDao projectDao();

}
