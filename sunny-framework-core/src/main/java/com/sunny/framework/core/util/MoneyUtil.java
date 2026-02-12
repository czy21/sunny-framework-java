package com.sunny.framework.core.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {

    /**
     * 元转分
     *
     * @param val 元
     */
    public static Integer yuan2Fen(String val) {
        if (StringUtils.isEmpty(val)) {
            return null;
        }
        BigDecimal val1 = new BigDecimal(val);
        BigDecimal val2 = val1.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
        return Integer.parseInt(val2.stripTrailingZeros().toPlainString());
    }

    /**
     * 分转元
     *
     * @param val 分
     */
    public static BigDecimal fen2Yuan(Integer val) {
        if (val == null) {
            return null;
        }
        BigDecimal var1 = new BigDecimal(val);
        BigDecimal var2 = new BigDecimal(100);
        return var1.divide(var2, 2, RoundingMode.HALF_UP);
    }

    /**
     * 分转元
     *
     * @param val 分
     */
    public static String fen2YuanString(Integer val) {
        if (val == null) {
            return null;
        }
        return fen2Yuan(val).stripTrailingZeros().toPlainString();
    }

    public static void main(String[] args) {
        String y1 = "10043.567";
        Integer f1 = yuan2Fen(y1);

        Integer f2 = 200001;
        String y2 = fen2YuanString(f2);
        System.out.println("a");
    }
}
