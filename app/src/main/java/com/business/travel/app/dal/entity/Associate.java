package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * @author chenshang
 */
@Entity
@Data
public class Associate {
	/**
	 * 主键id
	 */
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 同行人姓名
	 */
	private String name;
	/**
	 * 头像地址
	 */
	private String avatar;

	/**
	 * 项目创建时间,yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;
	/**
	 * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
	 */
	private String modifyTime;
	/**
	 * 备注
	 */
	private String remark;
}
