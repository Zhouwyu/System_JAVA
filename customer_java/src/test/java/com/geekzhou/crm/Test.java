package com.geekzhou.crm;

import com.geekzhou.crm.utils.PasswordUtils;

public class Test {
    public static void main(String[] args) {
        String encode = PasswordUtils.encode("654321");
        System.out.println(encode);
    }
}
