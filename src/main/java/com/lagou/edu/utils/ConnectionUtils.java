package com.lagou.edu.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 功能描述: 保证在一个线程中获取的是同一个数据库连接<br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-09 09:04
 */
public class ConnectionUtils {

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

//    private ConnectionUtils(){}
//    private static ConnectionUtils connectionUtils= new ConnectionUtils();
//    public static ConnectionUtils getInstance(){
//        return connectionUtils;
//    }

    public Connection getCurrentThreadConn() throws SQLException {
        Connection connection = threadLocal.get();
        //为空，从数据库获取连接
        if(Objects.isNull(connection)){
            connection = DruidUtils.getInstance().getConnection();
            threadLocal.set(connection);
        }
        return connection;
    }

}
