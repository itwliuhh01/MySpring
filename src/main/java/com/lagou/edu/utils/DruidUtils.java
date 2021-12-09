package com.lagou.edu.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author 应癫
 */
public class DruidUtils {

    private DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://47.94.221.48:3306/dnhd");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("open123!");
//        druidDataSource.setDefaultAutoCommit();

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
