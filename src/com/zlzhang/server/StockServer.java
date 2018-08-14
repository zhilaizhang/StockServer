package com.zlzhang.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

import com.zlzhang.stockmodel.StockModel;
import org.apache.commons.io.IOUtils;


@WebServlet("/StockServer")
public class StockServer extends HttpServlet{

    private String message;
    private Gson mGson;

    @Override
    public void init() throws ServletException {
        message = "Hello world, this message is from servlet123!";
        mGson = new Gson();
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
        String parameter = request.getParameter("data");
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


        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }
        try {
            addStocksAction(jb.toString(), resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        String sb = jb.toString();
//        String uname=request.getParameter("name");
//        try {
//          message =  connection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        //设置逻辑实现
//        PrintWriter out = resp.getWriter();
//        out.println("<h3>" + message + test + "</h3>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        doGet(req, resp);
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

    private void addStocksAction(String stocks, HttpServletResponse resp) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        List<StockModel> stockModels = mGson.fromJson(stocks, new TypeToken<List<StockModel>>(){}.getType());
        boolean isAddSucceed = false;
        if (stockModels != null) {
            for (StockModel stockModel : stockModels) {
                String insertSql1 = "INSERT INTO `db_stock`.`tb_stock` (`code`, `name`, `todayOpen`, `yesterdayClose`, `nowPrice`, `todayHighest`, `todayLowest`, `dealNum`, `OBV`, `date`, `time`) VALUES ";
                String vulueSql = "'"  + stockModel.getCode() + "','" + stockModel.getName() + "','" + stockModel.getTodayOpen() + "','" +
                        stockModel.getYesterdayClose() + "','" + stockModel.getNowPrice() + "','" + stockModel.getTodayHighest() + "','" + stockModel.getTodayLowest() + "','" + stockModel.getDealNum() +
                        "','" + stockModel.getOBV() + "','" + stockModel.getDate() + "','" + stockModel.getTime() + "'";
                String resultSql = insertSql1 + "(" +vulueSql + ")";
                isAddSucceed =   stmt.execute(resultSql);
            }
        }
        stmt.close();
        connection.close();
        int code = -1;
        if (isAddSucceed) {
            code = 0;
        }
        String response = "{\"code\":\"" + code + "\"}";
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.write(response);
//        out.println("<h3>" + message + "ok" + "</h3>");
        out.close();
    }

    private void addStockAction(){

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
