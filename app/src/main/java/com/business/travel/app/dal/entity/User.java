package com.business.travel.app.dal.entity;

import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import lombok.Data;

@Data
public class User extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 账单图标 默认展示第一个消费项的图标 这里先冗余一个字段,防止以后需求要求自定义
	 */
	private String iconDownloadUrl;
	/**
	 * 唯一编码
	 */
	private String code;
	/**
	 * 用户名字,逗号分隔
	 */
	private String name;
}
