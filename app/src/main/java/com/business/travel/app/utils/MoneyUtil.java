package com.business.travel.app.utils;

import java.math.BigDecimal;

public class MoneyUtil {

	private static final BigDecimal divisor = new BigDecimal(100);

	public static double toYuan(Long money) {
		if (money == null) {
			return 0;
		}
		return BigDecimal.valueOf(money).divide(divisor).doubleValue();
	}

	public static String toYuanString(Long money) {
		return String.valueOf(toYuan(money));
	}

	public static Long toFen(Double money) {
		if (money == null) {
			return 0L;
		}
		return BigDecimal.valueOf(money).multiply(divisor).longValue();
	}

	public static Long toFen(String money) {
		return toFen(Double.valueOf(money));
	}

	public static String toFenString(Double money) {
		return String.valueOf(toFen(money));
	}
}
