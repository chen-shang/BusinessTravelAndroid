package com.business.travel.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 编辑账单的时候的页面参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillEditeModel {
    /**
     * 账单ID
     */
    private Long billId;
}
