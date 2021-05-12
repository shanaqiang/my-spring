package com.shanaq.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

/**
 * @version 1.0
 * @author: guoli
 * @description:
 * @date: 2021-04-30 14:51
 **/
@Component
public class ShanaqBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessorBeforeInitializationBean(Object bean, String beanName) {
        System.out.println("初始化前");
        if(beanName.equals("userService")){
            ((UserService)bean).setName("SNQ!");
        }
        return null;
    }

    @Override
    public Object postProcessorAfterInitializationBean(Object bean, String beanName) {
        System.out.println("初始化后");
        if(beanName.equals("userService")){
            ((UserService)bean).setName("SNQ!");
        }
        return null;
    }
}
