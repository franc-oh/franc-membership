package com.franc.app.util;

public class NumberUtil {


    /**
     * 퍼센트 금액 구하기
     * @param amount
     * @param per
     * @return
     */
    public static int getCalcPerAmt(int amount, int per) {
        int result = 0;

        if(amount > 0) {
            result = amount * per / 100;
        }

        return result;
    }

}
