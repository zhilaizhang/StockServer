package com.zlzhang.utils;


import com.zlzhang.server.DBDao;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.stockmodel.StockType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class StockUtils {


    /**
     * 判断中小，创
     * @param code
     * @return
     */
    public static StockType judgeDetailExchange(String code){
        if (code.contains("sh")) {
            return StockType.SH;
        }
        if (code.contains("sz")) {
            return StockType.SZ;
        }
        String startCode = code.substring(0, 2);
        if (startCode.equals("60")) {
            return StockType.SH;
        } else {
            startCode = code.substring(0, 3);
            if (startCode.equals("000")) {
                return StockType.SZ000;
            } else if(startCode.equals("002")){
                return StockType.SZ000;
            } else if(startCode.equals("300")){
                return StockType.SZ300;
            }
            return StockType.SZ;
        }
    }

    public static StockModel changeToModel(String rawStock){
        String stockCode = rawStock.substring(13, 19);
        String stockInfo = rawStock.substring(21);
        String[] strings  = stockInfo.split(",");
        if (strings.length < 30) {
            return null;
        }
        StockModel stockModel = new StockModel();
        stockModel.setCode(stockCode);
        stockModel.setName(strings[0]);
        stockModel.setTodayOpen(Float.parseFloat(strings[1]));
        stockModel.setYesterdayClose(Float.parseFloat(strings[2]));
        stockModel.setNowPrice(Float.parseFloat(strings[3]));
        stockModel.setTodayHighest(Float.parseFloat(strings[4]));
        stockModel.setTodayLowest(Float.parseFloat(strings[5]));
        stockModel.setDealNum(Long.parseLong(strings[8]));
        stockModel.setOBV(Double.parseDouble(strings[9]));
        stockModel.setDate(strings[30]);
        stockModel.setTime(strings[31]);

        return stockModel;
    }

    public static boolean addStocksAction(List<StockModel> stockModels) throws IOException, SQLException {
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

}
