package com.franc.app.util;

import java.util.HashMap;
import java.util.Map;

public class PageUtil {

    /**
     * 페이지 관련 계산하여 리턴
     * @param pageNo
     * @param pageLimit
     * @return resultMap
     * @throws Exception
     */
    public static Map<String, Integer> getPageMap(Integer pageNo, Integer pageLimit) throws Exception {
        int offset = 0;
        int limit = 20;

        if(pageNo != null && pageLimit != null) {
            offset = Math.abs(pageNo -1) * pageLimit;
            limit = pageLimit;
        }

        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("offset", offset);
        resultMap.put("limit", limit);

        return resultMap;
    }

}
