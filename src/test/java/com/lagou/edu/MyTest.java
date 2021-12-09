package com.lagou.edu;

import com.lagou.edu.factory.BeanProxyFactory;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.service.impl.TransferServiceImpl;
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
        Connection currentThreadConn = ConnectionUtils.getInstance().getCurrentThreadConn();
        Connection currentThreadConn2 = ConnectionUtils.getInstance().getCurrentThreadConn();
        System.out.println("");

    }

    @Test
    public void testProxy() throws Exception {
//        TransferServiceImpl cglibProxy = (TransferServiceImpl)BeanProxyFactory.getInstance().getCglibProxy(new TransferServiceImpl());
//        System.out.println(cglibProxy.isAccountDao());
        TransferService cglibProxy = (TransferService) BeanProxyFactory.getInstance().getJdkProxy(new TransferServiceImpl());
        cglibProxy.transfer("","",1);
    }




}
