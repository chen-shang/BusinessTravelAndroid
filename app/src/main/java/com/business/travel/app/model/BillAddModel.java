package com.business.travel.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增账单页面的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillAddModel {

    /**
     * 账单所属的项目
     */
    private String projectName;

    /**
     * 消费日期
     */
    private Long consumeDate;

    /**
     * 消费类型
     *
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    private String consumptionType;
}
