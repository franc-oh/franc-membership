package com.franc.app.config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class H2FunctionConfig {

    /**
     * STR_TO_DATE()
     * @param s
     * @param f
     * @return
     * @throws Exception
     */
    public static Date strToDate(String s, String f) throws Exception {
        if("".equals(s))
            return null;

        String format = convertMySqlFormat(f);

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date result = formatter.parse(s);

        return result;
    }

    public static String convertMySqlFormat(String s) throws Exception {
        StringBuilder result = new StringBuilder();

        if(s.indexOf("%Y") > -1)
            result.append("yyyy");
        if(s.indexOf("%m") > -1)
            result.append("MM");
        if(s.indexOf("%d") > -1)
            result.append("dd");
        if(s.indexOf("%H") > -1)
            result.append("HH");
        if(s.indexOf("%i") > -1)
            result.append("mm");
        if(s.indexOf("%s") > -1)
            result.append("ss");

        return result.toString();
    }

}
