package com.zlzhang.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBDao {
    private static String USER = "root";
    private static String PASSWORD = "111111";
    private static String DB_URL = "jdbc:mysql://localhost:3306/db_stock";
    private static String DB_DRIVER = "com.mysql.jdbc.Driver";
    //  private static String SQL = "";
    private static Connection connection = null;

    //连接数据库
    public static Connection getConnection(){

        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("数据库连接异常");
            e.printStackTrace();
        }
        return connection;
    }
    public  static void closeConnection(Connection connection){

        if(connection != null){
            try {
                connection.close(); // 关闭数据库连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
