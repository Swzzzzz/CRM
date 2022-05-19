package com.bjpowernode.crm.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String forMateDateTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(date);
        return timeStr;
    }
}
