package com.bjpowernode.crm.commons.util;

import java.util.UUID;

public class UUIDUtil {
    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
