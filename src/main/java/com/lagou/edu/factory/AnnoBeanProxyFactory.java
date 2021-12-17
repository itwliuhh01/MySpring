package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * 功能描述:给bean产生代理对象 <br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-09 09:43
 */
@Service("annoBeanProxyFactory")
public class AnnoBeanProxyFactory {


//    private BeanProxyFactory() {
//    }
//
//    private static BeanProxyFactory beanProxyFactory = new BeanProxyFactory();
//
//    public static BeanProxyFactory getInstance() {
//        return beanProxyFactory;
//    }

    @Autowired
    private TranscationManager transcationManager;


    public TranscationManager isTranscationManager() {
        return transcationManager;
    }

    public void setTranscationManager(TranscationManager transcationManager) {
        this.transcationManager = transcationManager;
    }

    /***
     * 功能描述:获取jdk动态代理对象 <br>
     * @Param: target 委托对象
     * @Return: java.lang.Object
     * @Author: LiuHaihua
     * @Version: 1.0
     * @Date: 2021-12-09 09:45
     */
    public Object getJdkProxy(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object invoke = null;
                try {

                    //校验方法上是否添加注解，给指定的方法才走事务增强逻辑
                    if (Objects.isNull(method.getAnnotation(Transactional.class))) {
                        //开启事务
                        transcationManager.start();
                        invoke = method.invoke(target, args);
                        transcationManager.commit();
                    } else {
                        invoke = method.invoke(target, args);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    transcationManager.rollback();
                    throw e.getCause();
                }
                return invoke;
            }
        });
    }

    public Object getCglibProxy(Object target) {
        return Enhancer.create(target.getClass(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object invoke = null;
                try {
                    //校验方法上是否添加注解，给指定的方法才走事务增强逻辑
                    if (Objects.nonNull(method.getAnnotation(Transactional.class))) {
                        //开启事务
                        transcationManager.start();
                        invoke = method.invoke(target, objects);
                        transcationManager.commit();
                    }else {
                        invoke = method.invoke(target, objects);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    transcationManager.rollback();
                    throw e.getCause();
                }
                return invoke;
            }
        });
    }


}
