package com.lagou.edu1;

import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.factory.AnnoBeanFactory;
import com.lagou.edu.utils.ConnectionUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 功能描述: 测试类<br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-09 09:11
 */
public class MyTest {

    @Test
    public void test1() throws SQLException {
        Connection currentThreadConn = new ConnectionUtils().getCurrentThreadConn();
//        Connection currentThreadConn2 = ConnectionUtils.getInstance().getCurrentThreadConn();
        System.out.println("");

    }

    @Test
    public void testProxy() throws Exception {
//        TransferServiceImpl cglibProxy = (TransferServiceImpl)BeanProxyFactory.getInstance().getCglibProxy(new TransferServiceImpl());
//        System.out.println(cglibProxy.isAccountDao());
//        TransferService cglibProxy = (TransferService) BeanProxyFactory.getInstance().getJdkProxy(new TransferServiceImpl());
//        cglibProxy.transfer("","",1);
    }

    @Test
    public void test2() throws Exception {
        Object transferServiceImpl1 = AnnoBeanFactory.getInstance().getBeanByClassType(AccountDao.class);
        System.out.println("");

    }

    public static void main(String[] args) {
        String clazzName = "com.haihua.Test";
        String name = clazzName.substring(clazzName.lastIndexOf(".")+1);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        System.out.println(name);
    }



}
