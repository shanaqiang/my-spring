package com.spring;

public interface BeanPostProcessor {

    Object postProcessorBeforeInitializationBean(Object bean,String beanName);

    Object postProcessorAfterInitializationBean(Object bean,String beanName);
}
