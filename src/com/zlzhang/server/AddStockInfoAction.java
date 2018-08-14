package com.zlzhang.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zlzhang.stockmodel.StockModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@WebServlet("/AddStockInfoAction")
public class AddStockInfoAction extends HttpServlet {

    private Gson mGson;

    @Override
    public void init() throws ServletException {
        super.init();
        mGson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }
        try {
            addStockInfoAction(jb.toString(), resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addStockInfoAction(String stocks, HttpServletResponse resp) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        List<StockModel> stockModels = mGson.fromJson(stocks, new TypeToken<List<StockModel>>(){}.getType());
        boolean isAddSucceed = false;
        int addNum = -1;
        if (stockModels != null) {
            for (StockModel stockModel : stockModels) {
                String insertSql1 = "INSERT INTO `db_stock`.`tb_stock_info` (`code`, `name`) VALUES ";
                String vulueSql = "'"  + stockModel.getCode() + "','" + stockModel.getName() + "'";
                String resultSql = insertSql1 + "(" +vulueSql + ")";
//                isAddSucceed =   stmt.executeUpdate(resultSql);
                addNum =   stmt.executeUpdate(resultSql);
            }
        }
        stmt.close();
        connection.close();
        int code = -1;
        if (addNum == 1) {
            code = 0;
        }
        String response = "{\"code\":\"" + code + "\"}";
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.write(response);
//        out.println("<h3>" + message + "ok" + "</h3>");
        out.close();
    }
}
