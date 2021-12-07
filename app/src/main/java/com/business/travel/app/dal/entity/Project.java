package com.business.travel.app.dal.entity;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import com.business.travel.utils.DateTimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenshang
 * 差旅项目表
 */
@Entity(indices = {@Index(value = "name", unique = true)})
@Data
@EqualsAndHashCode(callSuper = false)
public class Project extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 项目名称
	 */
	private String name;
	/**
	 * 项目开始时间
	 */
	@NotNull
	private Long startTime;
	/**
	 * 项目结束时间
	 */
	private Long endTime;
	/**
	 * 备注
	 */
	private String remark;
	//下面为冗余字段,留待以后使用
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 顺序id
	 */
	private Long sortId;

	/**
	 * 项目开始时间-结束时间
	 *
	 * @return
	 */
	public String getProductTime() {
		//开始时间
		String startTime = Optional.ofNullable(this.getStartTime()).map(DateTimeUtil::toDate).map(datetime -> DateTimeUtil.format(datetime, "MM月dd日")).orElse("");

		//结束时间
		String endTime = Optional.ofNullable(this.getEndTime()).map(DateTimeUtil::toDate).map(datetime -> DateTimeUtil.format(datetime, "MM月dd日")).orElse("");

		//拼接
		if (StringUtils.isBlank(endTime)) {
			return startTime;
		}
		return startTime + " - " + endTime;
	}
}
