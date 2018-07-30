package com.zlzhang.hello;

import com.zlzhang.server.DBDao;

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
        message = "Hello world, this message is from servlet123!";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        //设置响应内容类型
        resp.setContentType("text/html");
        //TODO 获取请求的方法

        String requestUrl = request.getRequestURL().toString();//得到请求的URL地址
        String requestUri = request.getRequestURI();//得到请求的资源
        String queryString = request.getQueryString();//得到请求的URL地址中附带的参数
        String remoteAddr = request.getRemoteAddr();//得到来访者的IP地址
        String remoteHost = request.getRemoteHost();
        int remotePort = request.getRemotePort();
        String remoteUser = request.getRemoteUser();
        String method = request.getMethod();//得到请求URL地址时使用的方法
        String pathInfo = request.getPathInfo();
        String localAddr = request.getLocalAddr();//获取WEB服务器的IP地址
        String localName = request.getLocalName();//获取WEB服务器的主机名
        String test = "test " + requestUrl + " " + requestUri + " " +
                queryString + " " + remoteAddr + " " + remoteHost + " " +
                remotePort + " " + remoteUser + " " + method + " " + pathInfo + " " +
                localAddr + " " + localName;
        try {
          message =  connection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.println("<h3>" + message + test + "</h3>");
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
