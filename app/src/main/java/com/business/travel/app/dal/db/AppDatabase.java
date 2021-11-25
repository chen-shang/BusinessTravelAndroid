package com.business.travel.app.dal.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.dao.MemberDao;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;

/**
 * @author chenshang
 */
@Database(entities = {
		Member.class,
		Bill.class,
		Consumption.class,
		Project.class
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase INSTANCE;

	//todo DCL
	public static synchronized AppDatabase getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE =
					Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app.db")
							//FIXME 后续改成异步,所有用到数据库的地方不能用UI线程
							.allowMainThreadQueries()
							.build();
		}
		return INSTANCE;
	}

	/**
	 * 人员表
	 *
	 * @return AssociateItemDao
	 */
	public abstract MemberDao memberDao();

	/**
	 * 账单表
	 *
	 * @return
	 */
	public abstract BillDao billDao();

	/**
	 * 消费项
	 *
	 * @return
	 */
	public abstract ConsumptionDao consumptionDao();

	/**
	 * 项目表
	 *
	 * @return
	 */
	public abstract ProjectDao projectDao();
}
