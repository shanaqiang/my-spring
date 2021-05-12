package com.spring;

/**
 * @version 1.0
 * @author: guoli
 * @description:
 * @date: 2021-04-30 10:38
 **/
public class BeanDefinition {

    private Class clazz;
    private String scope;

    public BeanDefinition() {
    }

    public BeanDefinition(Class clazz, String scope) {
        this.clazz = clazz;
        this.scope = scope;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
