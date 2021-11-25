package com.business.travel.app.service;

import java.util.Date;
import java.util.List;

import android.content.Context;
import com.business.travel.app.dal.dao.AssociateItemDao;
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.utils.DateTimeUtil;

public class ItemService {

	private final AssociateItemDao associateItemDao;
	private final ConsumptionItemDao consumptionItemDao;

	public ItemService(Context context) {
		associateItemDao = AppDatabase.getInstance(context).associateItemDao();
		consumptionItemDao = AppDatabase.getInstance(context).consumptionItemDao();
	}

	/**
	 * 根据消费项类型查询消费项列表
	 *
	 * @param consumptionTypeEnum
	 * @return
	 */
	public List<Consumption> queryConsumptionItemByType(ConsumptionTypeEnum consumptionTypeEnum) {
		return consumptionItemDao.selectByType(consumptionTypeEnum.name());
	}

	public void initItemWhenFirstIn() {
		initConsumption();
		initAssociate();
	}

	private void initAssociate() {
		if (associateItemDao.count() > 0) {
			return;
		}

		Member member = new Member();
		member.setName("我");
		member.setIconDownloadUrl(ItemIconEnum.ItemIconMe.getIconDownloadUrl());
		member.setIconName("我");
		member.setSortId(0L);
		member.setCreateTime(DateTimeUtil.format(new Date()));
		member.setModifyTime(DateTimeUtil.format(new Date()));
		associateItemDao.insert(member);

	}

	private void initConsumption() {
		if (consumptionItemDao.count() > 0) {
			return;
		}
		//先获取远程的默认消费项列表,然后插入数据库,注意sortId todo
	}
}
