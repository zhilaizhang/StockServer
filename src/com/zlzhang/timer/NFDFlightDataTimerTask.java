package com.zlzhang.timer;

import com.zlzhang.server.DBDao;
import com.zlzhang.stockmodel.ResultData;
import com.zlzhang.stockmodel.StockInfo;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.stockmodel.StockType;
import com.zlzhang.utils.StockUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class NFDFlightDataTimerTask  extends TimerTask {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void run() {
        try {
            List<StockInfo> stockInfos = getAllStockCodes();
            List<StockModel> stockModels = null;
            for (StockInfo stockInfo : stockInfos) {
                stockModels = new ArrayList<StockModel>();
                StockType stockType = StockUtils.judgeDetailExchange(stockInfo.getCode());
                String position;
                if (stockType == StockType.SZ) {
                    position = "sz";
                } else {
                    position = "sh";
                }
                String url = "http://hq.sinajs.cn/list=" + position + stockInfo.getCode();
                String result =  getHttpURLConnection(url, "");
                if (result != null) {
                    StockModel stockModel = StockUtils.changeToModel(result);
                    if (stockModel != null) {
                        stockModels.add(stockModel);
                    }
                }
            }
            if (stockModels != null) {
                //TODO 上传操作, 写一个测试数据表
//                StockUtils.addStocksAction(stockModels);
            }
        } catch (Exception e) {
            System.out.println("-------------解析信息发生异常--------------");
        }
    }

    private List<StockInfo> getAllStockCodes() throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        connection = DBDao.getConnection();
        // 获取Statement
        Statement stmt = connection.createStatement();
        int addNum = -1;

        String basesql = "SELECT * FROM `db_stock`.`tb_stock_info` ";
        String resultSql = basesql;

        ResultSet resultSet =  stmt.executeQuery(resultSql);
        resultSet.getFetchSize();
        List stockModels = convertList(resultSet);
        List<StockInfo> listMap = changeToStockModel(stockModels);
        stmt.close();
        connection.close();
        return listMap;
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


    private static List<StockInfo> changeToStockModel(List stocks){
        if (stocks != null) {
            List<StockInfo> allStockList = new ArrayList<StockInfo>();
            for (Object stock : stocks) {
                HashMap stockMap = (HashMap) stock;
                StockInfo stockInfo = mapToStockInfo(stockMap);
               allStockList.add(stockInfo);
            }
            return allStockList;
        }
        return null;
    }

    private static StockInfo mapToStockInfo(HashMap map){
        StockInfo stockInfo = new StockInfo();
        stockInfo.setCode((String) map.get("code"));
        stockInfo.setName((String) map.get("name"));
        return stockInfo;
    }

    public static String getHttpURLConnection(String urlString,String params){

        String result = null;

        try {

            URL url = new URL(urlString);

            HttpURLConnection connection=(HttpURLConnection) url.openConnection();

            connection.setDoInput(true);//设置可输入

            connection.setDoOutput(true);//设置可以输出


            connection.setRequestProperty("contentType", "GBK");

            connection.setRequestMethod("GET");//设置请求方式//必须是大写

            connection.setConnectTimeout(50000);//设置连接超时时间。

            connection.setReadTimeout(50000);//设置读数据的超时时间

            // Post 请求不能使用缓存

            connection.setUseCaches(true);

            connection.connect();//连接服务器
            BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));

            String data;

            StringBuilder builder=new StringBuilder();

            while((data=reader.readLine())!=null){

                builder.append(data);

            }

            result=builder.toString();

        }catch(Exception e) {

            e.printStackTrace();

        }

        return result;

    }


}
