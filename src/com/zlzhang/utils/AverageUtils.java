package com.zlzhang.utils;

import com.zlzhang.stockmodel.AverageModel;
import com.zlzhang.stockmodel.StockModel;

import java.util.*;

/**
 * 均线计算工具类
 */
public class AverageUtils {

    /**
     * 计算已经站上均线的股票
     * @param stockModelMap
     * @return
     */
    public static List<AverageModel>  calculateStockAverage(Map<String, List<StockModel>> stockModelMap) {
        Iterator map1it = stockModelMap.entrySet().iterator();
        List<AverageModel>  mContinueRiseModels = new ArrayList<AverageModel>();
        while (map1it.hasNext()){
            Map.Entry entry = (Map.Entry) map1it.next();
            String code = (String) entry.getKey();
            List<StockModel> stockModels = (List<StockModel>) entry.getValue();
            int days = stockModels.size();
            StockModel stockModel = stockModels.get(0);
            sortStocks(stockModels);
            float averagePrice = getAverage(stockModels);
            boolean isUpAverage = isUpAverage(stockModels.get(days - 1), averagePrice);
            if (isUpAverage) {
                AverageModel averageModel = new AverageModel();
                averageModel.setCode(code);
                averageModel.setDays(stockModels.size());
                averageModel.setName(stockModel.getName());
                averageModel.setAveragePrice(averagePrice);
                List<Float> prices =  getContinueRisePrices(stockModels);
                averageModel.setPrices(prices);
                mContinueRiseModels.add(averageModel);
            }
        }
        if (mContinueRiseModels.size() > 0) {
            return mContinueRiseModels;
        }
        return null;
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

    /**
     * 计算均线
     * @return
     */
    private static float getAverage(List<StockModel> stockModels){
        int size = stockModels.size();
        float sum = 0;
        for (StockModel stockModel : stockModels) {
            sum += stockModel.getNowPrice();
        }
        return sum / size;
    }

    /**
     *  是否站上均线
     * @param lastStockModel
     * @param averagePrice
     * @return
     */
    private static boolean isUpAverage(StockModel lastStockModel, float averagePrice){
        if (lastStockModel.getNowPrice() > averagePrice) {
            return true;
        }
        return false;
    }

    /**
     * 获取连涨天数内每天的收盘价
     * @param stockModels
     * @return
     */
    private static List<Float> getContinueRisePrices(List<StockModel> stockModels){
        List<Float> prices = new ArrayList<Float>();
        for (StockModel stockModel : stockModels) {
            prices.add(stockModel.getNowPrice());
        }
        return prices;
    }

}
