package com.zlzhang.server;

import com.google.gson.Gson;
import com.zlzhang.stockmodel.ContinueRiseModel;
import com.zlzhang.stockmodel.ResultData;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.util.DateUtil;
import com.zlzhang.utils.ContinueRiseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;

/**
 * 根据天数获取连涨的股票
 */
@WebServlet("/GetContinueRiseStocksAction")
public class GetContinueRiseStocksAction extends HttpServlet {

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
        String[] days = (String[]) map.get("days");
        int  day = Integer.parseInt(days[0]);
        try {
            getAllStocks(day, resp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private void getAllStocks(int day, HttpServletResponse resp) throws IOException, SQLException {
        Date nowDate = new Date(System.currentTimeMillis());
        Date beforeDate = DateUtil.getBeforeDate(nowDate, day);
        String startTime = DateUtil.getDateString(beforeDate, "yyyy-MM-dd");
        String endTime = DateUtil.getDateString(nowDate, "yyyy-MM-dd");
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
        List<ContinueRiseModel> continueRiseModels = ContinueRiseUtils.calculateContinueRiseStock(listMap);
        ResultData resultData = new ResultData();
        if (listMap != null) {
            resultData.setCode(0);
            String result = mGson.toJson(continueRiseModels);
            resultData.setResult(result);
        } else {
            resultData.setCode(-1);
            resultData.setResult("");
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
