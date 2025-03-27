package com.face.sign.common;

import com.face.sign.common.util.SecurityUtils;
import org.junit.Test;

public class CommonTest {

    @Test
    public void SecurityUtilsTest() {
        String password = "123456";
        String encodePassword = SecurityUtils.encodePassword(password);
        System.out.println(encodePassword);
    }
}
