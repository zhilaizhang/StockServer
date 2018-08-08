package com.zlzhang.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zlzhang.modle.ResultData;
import com.zlzhang.modle.StockModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/GetAllStocksAction")
public class GetAllStocksAction extends HttpServlet {

    private Gson mGson;

    @Override
    public void init() throws ServletException {
        super.init();
        mGson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/json");
        Map map = request.getParameterMap();
        String[] startTimes = (String[]) map.get("startTime");
        String startTime = startTimes[0];
        String[] endTimes = (String[]) map.get("endTime");
        String endTime = endTimes[0];
        try {
            getAllStocks(startTime, endTime, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void getAllStocks(String startTime, String endTime, HttpServletResponse resp) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        int addNum = -1;

        String basesql = "SELECT * FROM `db_stock`.`tb_stock` where  date BETWEEN ";
        String valueSql = "'"  + startTime + "'" + " and " + "'" +  endTime + "'";
        String resultSql = basesql + valueSql;

        ResultSet resultSet =  stmt.executeQuery(resultSql);
        resultSet.getFetchSize();
        List stockModels = convertList(resultSet);
        Map<String, List<StockModel>> listMap = changeToStockModel(stockModels);
        ResultData resultData = new ResultData();
        if (listMap != null) {
            resultData.setCode(0);
            resultData.setResult(listMap);
        } else {
            resultData.setCode(-1);
            resultData.setResult(null);
        }
        String result = mGson.toJson(resultData);

        stmt.close();
        connection.close();
//        String response = "{\"code\":\"" + code + "\"}";
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.write(result);
//        out.println("<h3>" + message + "ok" + "</h3>");
        out.close();
    }


    private static List convertList(ResultSet rs) throws SQLException{
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        while (rs.next()) {
            Map rowData = new HashMap();//声明Map
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
        }
        return list;
    }

    private static Map<String, List<StockModel>> changeToStockModel(List stocks){
        if (stocks != null) {
            Map<String, List<StockModel>> allStockMap = new HashMap<String, List<StockModel>>();
            for (Object stock : stocks) {
                HashMap stockMap = (HashMap) stock;
                String code = (String) stockMap.get("code");
                StockModel stockModel = mapToStockModel(stockMap);
                if (allStockMap.containsKey(code)) {
                    allStockMap.get(code).add(stockModel);
                } else {
                    List<StockModel> stockModels = new ArrayList<StockModel>();
                    stockModels.add(stockModel);
                    allStockMap.put(code, stockModels);
                }
            }
            return allStockMap;
        }
        return null;
    }

    private static StockModel mapToStockModel(HashMap map){
        StockModel stockModel = new StockModel();
        stockModel.setCode((String) map.get("code"));
        stockModel.setDate((String) map.get("date"));
        stockModel.setYesterdayClose((Float) map.get("yesterdayClose"));
        stockModel.setNowPrice((Float) map.get("nowPrice"));
        stockModel.setOBV((Double) map.get("OBV"));
        stockModel.setTodayOpen((Float) map.get("todayOpen"));
        stockModel.setName((String) map.get("name"));
        stockModel.setId((Integer) map.get("id"));
        stockModel.setDealNum(Long.parseLong((String)map.get("dealNum")));
        stockModel.setTime((String) map.get("time"));
        stockModel.setTodayHighest((Float) map.get("todayHighest"));
        stockModel.setTodayLowest((Float) map.get("todayLowest"));
        return stockModel;
    }
}
