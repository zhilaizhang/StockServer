package com.zlzhang.server;

import com.google.gson.Gson;
import com.zlzhang.stockmodel.BIASModel;
import com.zlzhang.stockmodel.ResultData;
import com.zlzhang.stockmodel.StockModel;
import com.zlzhang.utils.BollUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

/**
 * 根据天数计算 乖离率
 */
@WebServlet("/GetAllStocksBIASAction")
public class GetAllStocksBIASAction extends HttpServlet {

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
        List<BIASModel> biasModels = getAllBIASModels(listMap);
        ResultData resultData = new ResultData();
        if (listMap != null) {
            resultData.setCode(0);
            String result = mGson.toJson(biasModels);
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


    private List<BIASModel> getAllBIASModels(Map<String, List<StockModel>> listMap){
        List<BIASModel> biasModels = new ArrayList<BIASModel>();
        Iterator map1it = listMap.entrySet().iterator();
        while (map1it.hasNext()){
            Map.Entry entry = (Map.Entry) map1it.next();
            String code = (String) entry.getKey();
            List<StockModel> stockModels = (List<StockModel>) entry.getValue();
            if (stockModels == null || stockModels.size() == 0) {
                continue;
            }
            StockModel stockModel = stockModels.get(0);
            sortStocks(stockModels);
            float BIAS = BollUtil.getBIAS(stockModels);
            BIASModel biasModel = new BIASModel();
            biasModel.setBIAS(BIAS);
            biasModel.setCode(code);
            biasModel.setName(stockModel.getName());
            biasModel.setDays(stockModels.size());
            System.out.println("test------" + BIAS + " " + code + " " + stockModel.getName() + " " + stockModels.size());
            biasModels.add(biasModel);
        }
        return biasModels;
    }


    /**
     * 对当前天数内股票进行时间排序
     * @param stockModels
     */
    private static void sortStocks(List<StockModel> stockModels){
        Collections.sort(stockModels, new Comparator<StockModel>() {
            @Override
            public int compare(StockModel stockModel, StockModel t1) {
                if (stockModel.getId() > t1.getId()) {
                    return 1;
                } else if(stockModel.getId() < t1.getId()){
                    return -1;
                }
                return 0;
            }
        });
    }

}
