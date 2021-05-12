package com.shanaq;

import com.shanaq.service.UserService;
import com.spring.ShanaqApplicationContext;

/**
 * @version 1.0
 * @author: guoli
 * @description:
 * @date: 2021-04-30 09:51
 **/
public class Test {

    public static void main(String[] args) {
        ShanaqApplicationContext shanaqApplicationContext=new ShanaqApplicationContext(AppConfig.class);

        UserService userService = (UserService)shanaqApplicationContext.getBean("userService");

        userService.test();


    }
}
