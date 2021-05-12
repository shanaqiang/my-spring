package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version 1.0
 * @author: guoli
 * @description:
 * @date: 2021-04-30 09:52
 **/
public class ShanaqApplicationContext {

    private Class configClass;

    //单例池
    private ConcurrentHashMap<String,Object> singletonObjects=new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap=new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList=new ArrayList<>();

    public ShanaqApplicationContext(Class configClass){
        this.configClass = configClass;

        //解析配置类
        //ComponentScan注解-->扫描路径-->扫描-->beanDefinition-->beanDefinitionMap
        //根据configClass扫描出所有的class类和scope,存储到beanDefinitonMap中
        scan(configClass);

        //将单例的类创建好并存放到singletonObjects中
        for (Map.Entry<String,BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue() ;
            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanName,beanDefinition);
                singletonObjects.put(beanName,bean);
            }
        }
    }

    public Object createBean(String beanName,BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if(declaredField.isAnnotationPresent(Autowired.class)){
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }

            //Aware回调
            if(instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setName(beanName);
            }

            //beanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessorBeforeInitializationBean(instance,beanName);
            }

            //初始化
            if(instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //beanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessorAfterInitializationBean(instance,beanName);
            }


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass) {
        //获取ComponentScan注解中的指定的扫描路径
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        path=path.replace(".","/");

        //扫描 获取路径下面的所有类名称
        //Bootstrap-->jre/lib
        //Ext-->jre/ext/lib
        //App-->classpath-->
        //App
        ClassLoader classLoader= ShanaqApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        File file=new File(resource.getFile());
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File f:files){
                String fileName = f.getAbsolutePath();
                if(fileName.endsWith(".class")){
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);

                        if(clazz.isAnnotationPresent(Component.class)){
                            //表示当前这个类是一个Bean
                            //class->Bean
                            //解析类-->BeanDefinition


                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor instance = (BeanPostProcessor)clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }


                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition=new BeanDefinition();
                            beanDefinition.setClazz(clazz);

                            if(clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName,beanDefinition);
                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName){
        if(beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                Object o = singletonObjects.get(beanName);
                return o;
            }else {
                Object bean = createBean(beanName,beanDefinition);
                return bean;
            }
        }else{
            //不存在对应的Bean
            throw new NullPointerException();
        }
    }
}
