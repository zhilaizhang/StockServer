package com.zlzhang.server;

import com.google.gson.Gson;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.util.DateUtil;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 数据库管理类
 */
public class DBManager {

    private Gson mGson;

    public DBManager(){
        mGson = new Gson();
    }


    /**
     * 查询多少天数以内的所有股票
     * @param day
     * @return
     * @throws SQLException
     */
    public Map<String, List<StockModel>> getAllStocksByDays(int day) throws SQLException {
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
        stmt.close();
        connection.close();
        return listMap;
    }

    /**
     * 获取数据库中最后一条数据的添加时间
     * @return
     */
    public String getLastStockAddTime(){
        String sqlValue = "SELECT * FROM `db_stock`.`test_tb_stock` order by id DESC limit 1";
        try {
            List stockModels = getResultSetBySql(sqlValue);
            StockModel stockModel = null;
            for (Object stock : stockModels) {
                HashMap stockMap = (HashMap) stock;
                stockModel = mapToStockModel(stockMap);
            }
            if (stockModel != null) {
                return stockModel.getDate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 传入sql语句，List
     * @param resultSql
     * @return
     * @throws SQLException
     */
    private List getResultSetBySql(String resultSql) throws SQLException {
        Connection connection = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        ResultSet resultSet =  stmt.executeQuery(resultSql);
        resultSet.getFetchSize();
        List stockModels = convertList(resultSet);
        stmt.close();
        connection.close();
        return stockModels;
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

    /**
     * 添加股票列表到数据库
     * @param stockModels
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public  boolean addStocksAction(List<StockModel> stockModels) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        int addNum = -1;
        if (stockModels != null) {
            for (StockModel stockModel : stockModels) {
                String insertSql1 = "INSERT INTO `db_stock`.`test_tb_stock` (`code`, `name`, `todayOpen`, `yesterdayClose`, `nowPrice`, `todayHighest`, `todayLowest`, `dealNum`, `OBV`, `date`, `time`) VALUES ";
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
            return true;
        }
        return false;
    }


    /**
     * 添加股票列表到数据库
     * @param stockModels
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public  boolean addStocksAction1(List<StockModel> stockModels) throws IOException, SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        int addNum = -1;
        if (stockModels != null) {
            String insertSql1 = "INSERT INTO `db_stock`.`test_tb_stock` (`code`, `name`, `todayOpen`, `yesterdayClose`, `nowPrice`, `todayHighest`, `todayLowest`, `dealNum`, `OBV`, `date`, `time`) VALUES ";
            String values = "";
            int length = stockModels.size();
            for (int i = 0; i < stockModels.size(); i++) {
                StockModel stockModel = stockModels.get(i);
                String vulueSql = "'"  + stockModel.getCode() + "','" + stockModel.getName() + "','" + stockModel.getTodayOpen() + "','" +
                        stockModel.getYesterdayClose() + "','" + stockModel.getNowPrice() + "','" + stockModel.getTodayHighest() + "','" + stockModel.getTodayLowest() + "','" + stockModel.getDealNum() +
                        "','" + stockModel.getOBV() + "','" + stockModel.getDate() + "','" + stockModel.getTime() + "'";

                values += "(" +vulueSql + ")";
                if (i == length - 1) {
                    values += ";";
                } else {
                    values += ",";
                }
            }
            String resultSql = insertSql1 + values;
            addNum =   stmt.executeUpdate(resultSql);
        }
        stmt.close();
        connection.close();
        int code = -1;
        if (addNum == 1) {
            return true;
        }
        return false;
    }

}
