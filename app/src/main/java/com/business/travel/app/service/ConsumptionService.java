package com.business.travel.app.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.constant.BusinessTravelResourceConstant;
import com.business.travel.app.dal.dao.ConsumptionDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Consumption;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.converter.ConsumptionConverter;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.DeleteEnum;
import com.business.travel.vo.enums.ItemTypeEnum;
import org.jetbrains.annotations.NotNull;

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

	/**
	 * 更新顺序
	 *
	 * @param id
	 * @param sortId
	 */
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
			LogUtils.d("已经初始化默认图标");
			return;
		}

		if (!NetworkUtils.isAvailable()) {
			LogUtils.e("网络不稳定,请稍后重试");
			LogToast.infoShow("网络不稳定");
			return;
		}

		LogUtils.i("开始初始化默认图标");

		//先获取远程的默认消费项列表,然后插入数据库,注意sortId
		List<Consumption> consumptions = new ArrayList<>();

		FutureUtil.supplyAsync(() -> {
			List<GiteeContent> v5ReposOwnerRepoContentsIncome = BusinessTravelResourceApi.getRepoContents(BusinessTravelResourceConstant.INCOME_ICON_PATH);
			LogUtils.i("开始初始化默认图标: 收入图标共计:" + v5ReposOwnerRepoContentsIncome.size());
			if (CollectionUtils.isNotEmpty(v5ReposOwnerRepoContentsIncome)) {
				List<Consumption> IncomeConsumptionList = getConsumptions(v5ReposOwnerRepoContentsIncome, ConsumptionTypeEnum.INCOME);
				consumptions.addAll(IncomeConsumptionList);
			}
			List<GiteeContent> v5ReposOwnerRepoContentsSpending = BusinessTravelResourceApi.getRepoContents(BusinessTravelResourceConstant.SPENDING_ICON_PATH);
			LogUtils.i("开始初始化默认图标: 支出图标共计:" + v5ReposOwnerRepoContentsSpending.size());
			if (CollectionUtils.isNotEmpty(v5ReposOwnerRepoContentsSpending)) {
				List<Consumption> IncomeConsumptionList = getConsumptions(v5ReposOwnerRepoContentsSpending, ConsumptionTypeEnum.SPENDING);
				consumptions.addAll(IncomeConsumptionList);
			}
			return consumptions;
		}).thenAccept(consumptionDao::batchInsert);
	}

	@NotNull
	private List<Consumption> getConsumptions(List<GiteeContent> v5ReposOwnerRepoContentsSpending, ConsumptionTypeEnum consumptionTypeEnum) {
		return v5ReposOwnerRepoContentsSpending.stream()
		                                       //后缀为.svg
		                                       .filter(v5ReposOwnerRepoContent -> v5ReposOwnerRepoContent.getName().endsWith(".svg"))
		                                       //按照序号排序
		                                       .sorted(Comparator.comparingInt(GiteeContent::getItemSort))
		                                       //映射成Consumption
		                                       .map(v5ReposOwnerRepoContent -> convert(v5ReposOwnerRepoContent, consumptionTypeEnum))
		                                       //to list
		                                       .collect(Collectors.toList());
	}

	private Consumption convert(GiteeContent v5ReposOwnerRepoContent, ConsumptionTypeEnum spending) {
		Consumption consumption = new Consumption();
		consumption.setName(v5ReposOwnerRepoContent.showName());
		consumption.setIconDownloadUrl(v5ReposOwnerRepoContent.getDownloadUrl());
		consumption.setIconName(v5ReposOwnerRepoContent.getPath());
		consumption.setConsumptionType(spending.name());
		consumption.setSortId((long)v5ReposOwnerRepoContent.getItemSort());
		consumption.setCreateTime(DateTimeUtil.timestamp());
		consumption.setModifyTime(DateTimeUtil.timestamp());
		consumption.setIsDeleted(DeleteEnum.NOT_DELETE.getCode());
		return consumption;
	}

	public List<ImageIconInfo> queryByIds(List<Long> ids) {
		return convert(consumptionDao.selectByPrimaryKeys(ids));
	}

	public List<ImageIconInfo> queryAllConsumptionIconInfo(ConsumptionTypeEnum consumptionType) {
		//根据是支出还是收入获取消费项列表
		return convert(consumptionDao.selectByType(consumptionType.name()));
	}

	private List<ImageIconInfo> convert(List<Consumption> consumptions) {
		if (CollectionUtils.isEmpty(consumptions)) {
			consumptions = new ArrayList<>();
		}

		return consumptions.stream().map(consumptionItem -> {
			ImageIconInfo imageIconInfo = ConsumptionConverter.INSTANCE.convertImageIconInfo(consumptionItem);
			imageIconInfo.setItemType(ItemTypeEnum.CONSUMPTION.name());
			return imageIconInfo;
		}).collect(Collectors.toList());
	}
}
