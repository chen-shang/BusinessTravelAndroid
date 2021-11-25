package com.business.travel.app.service;

import java.util.List;

import android.content.Context;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.enums.ConsumptionTypeEnum;

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

	public void initConsumption() {
		if (consumptionDao.count() > 0) {
			return;
		}
		//先获取远程的默认消费项列表,然后插入数据库,注意sortId todo
	}
}
