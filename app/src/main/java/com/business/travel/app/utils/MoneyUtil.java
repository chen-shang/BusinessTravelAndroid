package com.business.travel.app.utils;

import java.math.BigDecimal;

/**
 * 金钱相关的工具类
 */
public class MoneyUtil {

    private static final BigDecimal divisor = new BigDecimal(100);

    /**
     * 分 转 元
     *
     * @param money
     * @return
     */
    public static double toYuan(Long money) {
        if (money == null) {
            return 0;
        }
        return BigDecimal.valueOf(money).divide(divisor).doubleValue();
    }

    /**
     * 分 转 元
     *
     * @param money
     * @return
     */
    public static String toYuanString(Long money) {
        return String.valueOf(toYuan(money));
    }

    /**
     * 元 转 分
     *
     * @param money
     * @return
     */
    public static Long toFen(Double money) {
        if (money == null) {
            return 0L;
        }
        return BigDecimal.valueOf(money).multiply(divisor).longValue();
    }

    /**
     * 元 转 分
     *
     * @param money
     * @return
     */
    public static Long toFen(String money) {
        return toFen(Double.valueOf(money));
    }

    /**
     * 元 转 分
     *
     * @param money
     * @return
     */
    public static String toFenString(Double money) {
        return String.valueOf(toFen(money));
    }
}
