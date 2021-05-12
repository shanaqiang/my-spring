package com.shanaq.service;

import com.spring.*;

/**
 * @version 1.0
 * @author: guoli
 * @description:
 * @date: 2021-04-30 09:58
 **/
@Component("userService")
@Scope("protoType")
public class UserService implements BeanNameAware, InitializingBean {

    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }

    private String name;
    @Override
    public void setName(String name) {
        name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化");
    }
}
