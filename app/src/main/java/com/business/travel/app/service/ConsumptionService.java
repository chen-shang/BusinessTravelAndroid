package com.business.travel.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.utils.DateTimeUtil;

public class ConsumptionService {

	private final ConsumptionDao consumptionDao;

	public ConsumptionService(Context context) {
		consumptionDao = AppDatabase.getInstance(context).consumptionDao();
	}

	/**
	 * 根据消费项类型查询消费项列表
	 *
	 * @param consumptionTypeEnum
	 * @return
	 */
	public List<Consumption> queryConsumptionItemByType(ConsumptionTypeEnum consumptionTypeEnum) {
		return consumptionDao.selectByType(consumptionTypeEnum.name());
	}

	public void updateMemberSort(Long id, Long sortId) {
		consumptionDao.updateSort(id, sortId);
	}

	public void softDeleteConsumption(Long id) {
		consumptionDao.softDelete(id);
	}

	/**
	 * 初次使用app的时候,数据库中是没有消费项图标数据的,因此需要初始化一些默认的图标
	 */
	public void initConsumption() {
		if (consumptionDao.count() > 0) {
			return;
		}
		//先获取远程的默认消费项列表,然后插入数据库,注意sortId todo
		//收入和支出都需要默认图标 todo
		List<Consumption> consumptions = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Consumption consumption = new Consumption();
			consumption.setName("name" + i);
			consumption.setIconDownloadUrl("url" + i);
			consumption.setIconName("iconName" + i);
			consumption.setConsumptionType(ConsumptionTypeEnum.SPENDING.name());
			consumption.setSortId((long)i);
			consumption.setCreateTime(DateTimeUtil.format(new Date()));
			consumption.setModifyTime(DateTimeUtil.format(new Date()));
			consumptions.add(consumption);
		}
		consumptionDao.batchInsert(consumptions);
	}
}
