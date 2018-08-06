package com.zlzhang.server;

import com.google.gson.Gson;
import com.zlzhang.modle.StockModel;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetStockByIdAction")
public class GetStockByIdAction extends HttpServlet {

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
            getStockById(resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getStockById(HttpServletResponse resp) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        List<StockModel> stockModels = new ArrayList<StockModel>();
        boolean isAddSucceed = false;
        int addNum = -1;
        if (stockModels != null) {
            for (StockModel stockModel : stockModels) {
                String insertSql1 = "INSERT INTO `db_stock`.`tb_stock` (`code`, `name`, `todayOpen`, `yesterdayClose`, `nowPrice`, `todayHighest`, `todayLowest`, `dealNum`, `OBV`, `date`, `time`) VALUES ";
                String vulueSql = "'"  + stockModel.getCode() + "','" + stockModel.getName() + "','" + stockModel.getTodayOpen() + "','" +
                        stockModel.getYesterdayClose() + "','" + stockModel.getNowPrice() + "','" + stockModel.getTodayHighest() + "','" + stockModel.getTodayLowest() + "','" + stockModel.getDealNum() +
                        "','" + stockModel.getOBV() + "','" + stockModel.getDate() + "','" + stockModel.getTime() + "'";
                String resultSql = insertSql1 + "(" +vulueSql + ")";
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
