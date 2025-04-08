package com.geekzhou.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

// 调用示例：OrderIdUtil.generate("EC") → EC202311091423571234
public class OrderUtils {
    public static String generate(String businessCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        int random = (int)(Math.random()*9000 + 1000);
        return businessCode + timestamp + random;
    }
}

