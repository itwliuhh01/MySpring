package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.utils.ClazzUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 功能描述: 基于注解的beanFactory<br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-15 22:51
 */
public class AnnoBeanFactory {

    /**
     * 构造一个单例的eanFactory
     */
    public AnnoBeanFactory() {
    }

    private static AnnoBeanFactory beanFactory = new AnnoBeanFactory();

    public static AnnoBeanFactory getInstance() {
        return beanFactory;
    }

    /**
     * key是ID，value对象的是对象bean
     */
    private static Map<String, Object> beanMap = new HashMap<>();

    static {

        //获取所有的class
        List<String> clazzNames = ClazzUtils.getClazzName("com.lagou.edu", true);
        for (String clazzName : clazzNames) {
            try {
                //获取每一个class
                Class<?> clazz = Class.forName(clazzName);
                //解析Service注解
                //获取类上的注解
                Service service = clazz.getAnnotation(Service.class);
                //不为空说明有service注解
                if (Objects.nonNull(service)) {
                    //生成对象放到容器里面，这里考虑的都是单例的bean
                    Object Object = clazz.newInstance();
                    //value有值就为beanName，没有值取类名小写
                    String value = service.value();
                    String beanName = null;
                    if (value != null && !"".equals(value.trim())) {
                        beanName = value;
                    } else {
                        //取类名首字母小写
                        String name = clazzName.substring(clazzName.lastIndexOf(".") + 1);
                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                        beanName = name;
                    }
                    beanMap.put(beanName, Object);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        //解析Autowired
        for (String clazzName : clazzNames) {
            try {
                //获取每一个class
                Class<?> clazz = Class.forName(clazzName);

                Field[] declaredFields = clazz.getDeclaredFields();
                //遍历属性看看属性上是否有Autowired注解
                for (Field field : declaredFields) {
                    Autowired autowiredAnno = field.getAnnotation(Autowired.class);
                    //不为空的话说明存在需要依赖注入
                    if (Objects.nonNull(autowiredAnno)) {
                        //属性代表的类型
                        Class<?> fieldClass = Class.forName(field.getGenericType().getTypeName());
                        String declaringName = field.getName();

                        String depBeanName = declaringName;
                        //如果没有按类型获取
                        Object depBean = beanMap.get(depBeanName);
                        if (Objects.isNull(depBean)) {
                            depBean = getBeanByClassType(fieldClass);
                        }
                        //获取父类对象
                        //获取类属性上的注解
                        Service service = clazz.getAnnotation(Service.class);
                        String parentBeanName = service.value();
                        if (parentBeanName == null || "".equals(parentBeanName.trim())) {
                            //取类名首字母小写
                            String name = clazzName.substring(clazzName.lastIndexOf(".") + 1);
                            name = name.substring(0, 1).toLowerCase() + name.substring(1);
                            parentBeanName = name;
                        }
                        Object parent = beanMap.get(parentBeanName);
                        field.setAccessible(true);
                        field.set(parent, depBean);
                    }

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //解析Transactional 生成代理对象
        for (String clazzName : clazzNames) {
            //默认先实现方法上加事务注解
            try {
                Class<?> clazz = Class.forName(clazzName);
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    Transactional transactionalAnno = declaredMethod.getAnnotation(Transactional.class);
                    //有注解，开启代理生成相应的对象
                    if (Objects.nonNull(transactionalAnno)) {
                        //判断使用哪一种代理模式（默认使用cglib的代理技术）
                        Class<?>[] interfaces = clazz.getInterfaces();

                        AnnoBeanProxyFactory beanProxyFactory = (AnnoBeanProxyFactory) beanMap.get("annoBeanProxyFactory");
                        Service service = clazz.getAnnotation(Service.class);
                        String targetBeanName = service.value();
                        if (targetBeanName == null || "".equals(targetBeanName.trim())) {
                            //取类名首字母小写
                            String name = clazzName.substring(clazzName.lastIndexOf(".") + 1);
                            name = name.substring(0, 1).toLowerCase() + name.substring(1);
                            targetBeanName = name;
                        }
                        Object targetBean = beanMap.get(targetBeanName);
                        if (Objects.nonNull(interfaces) && interfaces.length > 0) {
                            Object jdkProxy = beanProxyFactory.getJdkProxy(targetBean);
                            //重新放入到beanMap中，这里可以考虑，使用的时候在生成代理对象不然感觉写死了
                            beanMap.put(targetBeanName, jdkProxy);
                        } else {
                            Object cglibProxy = beanProxyFactory.getCglibProxy(targetBean);
                            beanMap.put(targetBeanName, cglibProxy);
                        }
                    }

                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public Object getBean(String id) {
        return beanMap.get(id);
    }

    /***
     * 功能描述: 根据类型获取实例<br>
     * @Param: classType 类型
     * @Return: java.lang.Object 返回具体的实例
     * @Author: LiuHaihua
     * @Version: 1.0
     * @Date: 2021-12-16 12:11
     */
    public static Object getBeanByClassType(Class classType) {
        //记录beanfactory中到底存在多少个指定类型的bean实例，超过一个报错
        List<Object> beanCount = new ArrayList<>();
        Collection<Object> beans = beanMap.values();
        if (Objects.isNull(beans) || beans.isEmpty()){return null;}
        for (Object bean : beans) {
            Class<?> beanClass = bean.getClass();
            if (classType.isAssignableFrom(beanClass)) {
                beanCount.add(bean);
                if(beanCount.size()>1){
                    throw new RuntimeException("期望获得一个"+classType.getName()+"类型的实例，但是存在多个");
                }
            }
        }
        if(!beanCount.isEmpty()){
            return beanCount.get(0);
        }
        return null;
    }

}
