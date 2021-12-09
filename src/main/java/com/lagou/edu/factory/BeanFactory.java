package com.lagou.edu.factory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述: 生产bean的工厂类<br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-08 22:03
 */
public class BeanFactory {

    /**构造一个单例的eanFactory*/
    public BeanFactory(){}
    private static BeanFactory beanFactory = new BeanFactory();

    public static BeanFactory getInstance(){
        return beanFactory;
    }

    /**key是ID，value对象的是对象bean*/
    private static Map<String, Object> beanMap = new HashMap<>();

    static {
        //解析xml生成bean对象
        InputStream in = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        //解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(in);
            Element rootElement = document.getRootElement();
            List<Element> elementList = rootElement.selectNodes("//bean");

            for (Element element : elementList) {
                //获取id和class
                String id = element.attributeValue("id");
                String clazz = element.attributeValue("class");

                //使用反射技术生成bean对象
                Class<?> beanClazz = Class.forName(clazz);
                Object beanObect = beanClazz.newInstance();
                //放入到beanMap容器中
                beanMap.put(id,beanObect);
            }

            //生成完bean之后判断是否有依赖关系，有的话注入依赖的bean，通过set方法
            //获取所有的property属性，再找到上级
            List<Element> refList = rootElement.selectNodes("//property");
            for (Element element : refList) {
                //<property name="accountDao" ref="accountDao"></property>
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                Object refBean = beanMap.get(ref);

                //找到上级标签
                Element parent = element.getParent();
                String parentId = parent.attributeValue("id");

                //获取到parent对象
                Object parentBean = beanMap.get(parentId);
                Class<?> parentClazz = parentBean.getClass();
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, parentClazz);
                //调用set方法设置依赖
                propertyDescriptor.getWriteMethod().invoke(parentBean,refBean);

                //将bean重新放入到beanMap中去
                beanMap.put(parentId,parentBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Object getBean(String id){
        return beanMap.get(id);
    }


}
