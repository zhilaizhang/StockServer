package com.zlzhang.hello;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/HelloWorld")
public class HelloWorld extends HttpServlet{

    private String message;

    @Override
    public void init() throws ServletException {
        message = "Hello world, this message is from servlet!";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置响应内容类型
        resp.setContentType("text/html");
        try {
          message =  connection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.println("<h3>" + message + "</h3>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    private String connection() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        // 添加图书信息的SQL语句
        String sql = "select * from tb_stock";
        // 执行查询
        ResultSet rs = stmt.executeQuery(sql);
        String test = "";
        while (rs.next()){
            String name = rs.getString("name");
            test += name;
            System.out.println("test" + name);
        }
        rs.close();
        stmt.close();
        connection.close();
        return test;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
