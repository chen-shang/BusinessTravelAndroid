package com.business.travel.app.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.dao.MemberDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.converter.MemberConverter;
import com.business.travel.utils.DateTimeUtil;

public class MemberService {
	private final MemberDao memberDao;

	public MemberService(Context context) {
		memberDao = AppDatabase.getInstance(context).memberDao();
	}

	/**
	 * 查询所有人员的图标信息
	 *
	 * @return
	 */
	public List<ImageIconInfo> queryAllMembersIconInfo() {
		return convert(memberDao.selectAll());
	}

	/**
	 * 更新人员排序
	 *
	 * @param id
	 * @param sortId
	 */
	public void updateMemberSort(Long id, Long sortId) {
		memberDao.updateSort(id, sortId);
	}

	/**
	 * 删除人员
	 *
	 * @param id
	 */
	public void softDeleteMember(Long id) {
		memberDao.softDelete(id);
	}

	/**
	 * 初次使用app的时候,数据库中是没有人员图标数据的,因此需要初始化一些默认的图标
	 */
	public void initMember() {
		LogUtils.i("开始初始化人员默认图标");
		if (memberDao.count() > 0) {
			LogUtils.i("已经初始化人员默认图标");
			return;
		}

		Member member = new Member();
		member.setName("我");
		member.setIconDownloadUrl(ItemIconEnum.ItemIconMe.getIconDownloadUrl());
		member.setIconName("我");
		member.setSortId(0L);
		member.setCreateTime(DateTimeUtil.timestamp());
		member.setModifyTime(DateTimeUtil.timestamp());
		memberDao.insert(member);
	}

	public List<ImageIconInfo> queryAll(List<Long> ids) {
		List<Member> members = memberDao.selectAll(ids);
		return convert(members);
	}

	private List<ImageIconInfo> convert(List<Member> members) {
		if (CollectionUtils.isEmpty(members)) {
			return Collections.emptyList();
		}

		return members.stream().map(member -> {
			ImageIconInfo imageIconInfo = MemberConverter.INSTANCE.convertImageIconInfo(member);
			imageIconInfo.setItemType(ItemTypeEnum.MEMBER.name());
			imageIconInfo.setSelected("我".equals(member.getName()));
			return imageIconInfo;
		}).collect(Collectors.toList());
	}
}
