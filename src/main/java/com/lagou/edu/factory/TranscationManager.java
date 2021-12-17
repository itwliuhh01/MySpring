package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 功能描述: 事务管理类<br>
 *
 * @Author: LiuHaihua
 * @Version: 1.0
 * @Date: 2021-12-09 09:33
 */
@Service("transcationManager")
public class TranscationManager {

    @Autowired
    private ConnectionUtils connectionUtils;

    public ConnectionUtils getConnectionUtils() {
        return connectionUtils;
    }

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    public  void start() throws SQLException {
        //开启数据库事务
        Connection connection = connectionUtils.getCurrentThreadConn();
        //开启事务
        connection.setAutoCommit(false);
    }

    public  void commit() throws SQLException {
        //开启数据库事务
        Connection connection = connectionUtils.getCurrentThreadConn();
        //开启事务
        connection.commit();
    }
    public void rollback() throws SQLException {
        //开启数据库事务
        Connection connection = connectionUtils.getCurrentThreadConn();
        //开启事务
        connection.rollback();
    }

}
