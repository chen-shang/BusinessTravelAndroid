package com.business.travel.app.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Context;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.dal.dao.MemberDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.app.enums.ItemIconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
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
	public List<ImageIconInfo> queryAllMembersImageIconInfo() {
		List<Member> members = memberDao.selectAll(DeleteEnum.NOT_DELETE.getCode());
		if (CollectionUtils.isEmpty(members)) {
			return Collections.emptyList();
		}

		return members.stream().map(member -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setId(member.getId());
			imageIconInfo.setName(member.getName());
			imageIconInfo.setIconDownloadUrl(member.getIconDownloadUrl());
			imageIconInfo.setIconName(member.getIconName());
			imageIconInfo.setItemType(ItemTypeEnum.MEMBER.name());
			imageIconInfo.setSortId(member.getSortId());
			imageIconInfo.setSelected(false);
			return imageIconInfo;
		}).collect(Collectors.toList());
	}

	public void updateMemberSort(Long id, Long sortId) {
		memberDao.updateSort(id, sortId);
	}

	public void softDeleteMember(Long id) {
		memberDao.softDelete(id);
	}

	public void initMember() {
		if (memberDao.count() > 0) {
			return;
		}

		Member member = new Member();
		member.setName("我");
		member.setIconDownloadUrl(ItemIconEnum.ItemIconMe.getIconDownloadUrl());
		member.setIconName("我");
		member.setSortId(0L);
		member.setCreateTime(DateTimeUtil.format(new Date()));
		member.setModifyTime(DateTimeUtil.format(new Date()));
		memberDao.insert(member);
	}
}
