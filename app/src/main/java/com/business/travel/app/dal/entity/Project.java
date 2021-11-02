package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * @author chenshang
 * 差旅项目表
 */
@Entity(indices = {@Index(value = "name", unique = true)})
@Data
public class Project {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 项目名称
	 */
	private String name;
	/**
	 * 项目开始时间,yyyy-MM-dd HH:mm:ss
	 */
	private String startTime;
	/**
	 * 项目结束时间,yyyy-MM-dd HH:mm:ss
	 */
	private String endTime;
	
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
