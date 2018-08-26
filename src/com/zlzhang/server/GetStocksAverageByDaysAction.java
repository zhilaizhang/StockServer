package com.zlzhang.server;

import com.google.gson.Gson;
import com.zlzhang.stockmodel.AverageModel;
import com.zlzhang.stockmodel.ContinueRiseModel;
import com.zlzhang.stockmodel.ResultData;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.util.DateUtil;
import com.zlzhang.utils.AverageUtils;
import com.zlzhang.utils.ContinueRiseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 根据天数获取连涨的股票
 */
@WebServlet("/GetStocksAverageByDaysAction")
public class GetStocksAverageByDaysAction extends HttpServlet {

    private Gson mGson;
    private DBManager mDBManager;

    @Override
    public void init() throws ServletException {
        super.init();
        mGson = new Gson();
        mDBManager = new DBManager();
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
        Map<String, List<StockModel>> listMap = mDBManager.getAllStocksByDays(day);
        List<AverageModel> continueRiseModels = AverageUtils.calculateStockAverage(listMap);
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

//        String response = "{\"code\":\"" + code + "\"}";
        //设置逻辑实现
        PrintWriter out = resp.getWriter();
        out.write(result);
//        out.println("<h3>" + message + "ok" + "</h3>");
        out.close();
    }

}
