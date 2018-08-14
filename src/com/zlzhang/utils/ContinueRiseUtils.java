package com.zlzhang.utils;


import com.zlzhang.stockmodel.ContinueRiseModel;
import com.zlzhang.stockmodel.StockModel;

import java.util.*;

public class ContinueRiseUtils {

    public static List<ContinueRiseModel> calculateContinueRiseStock(Map<String, List<StockModel>> stockModelMap) {
        Iterator map1it = stockModelMap.entrySet().iterator();
        List<ContinueRiseModel> mContinueRiseModels; mContinueRiseModels = new ArrayList<ContinueRiseModel>();
        while (map1it.hasNext()){
            Map.Entry entry = (Map.Entry) map1it.next();
            String code = (String) entry.getKey();
            List<StockModel> stockModels = (List<StockModel>) entry.getValue();
            StockModel stockModel = stockModels.get(0);
            sortStocks(stockModels);
            boolean isContinueRise = isContinueRise(stockModels);
            if (isContinueRise) {
                float riseRate =  getRiseRate(stockModels);
                ContinueRiseModel continueRiseModel = new ContinueRiseModel();
                continueRiseModel.setCode(code);
                continueRiseModel.setContinueDays(stockModels.size());
                continueRiseModel.setName(stockModel.getName());
                continueRiseModel.setRiseRate(riseRate);
                List<Float> prices =  getContinueRisePrices(stockModels);
                continueRiseModel.setPrices(prices);
                mContinueRiseModels.add(continueRiseModel);
            }
        }
        if (mContinueRiseModels.size() > 0) {
            sortStocksByRiseRate(mContinueRiseModels);
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
     * 对当前天数内股票进行时间排序
     * @param stockModels
     */
    private static void sortStocksByRiseRate(List<ContinueRiseModel> stockModels){
        Collections.sort(stockModels, new Comparator<ContinueRiseModel>() {
            @Override
            public int compare(ContinueRiseModel stockModel, ContinueRiseModel t1) {
                if (stockModel.getRiseRate() < t1.getRiseRate()) {
                    return 1;
                } else if(stockModel.getRiseRate() > t1.getRiseRate()){
                    return -1;
                }
                return 0;
            }
        });
    }

    /**
     * 判断在当前的天数内，是否为连涨
     * @param stockModels
     * @return
     */
    private static boolean isContinueRise(List<StockModel> stockModels){
        float lastPrice = 0;
        for (StockModel stockModel : stockModels) {
            if (stockModel.getNowPrice() > lastPrice) {
                lastPrice = stockModel.getNowPrice();
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算涨幅
     * @param stockModels
     * @return
     */
    private static float getRiseRate(List<StockModel> stockModels){
        int length = stockModels.size();
        float startPrice = stockModels.get(0).getYesterdayClose();
        float endPrice = stockModels.get(length -1).getNowPrice();
        float riseRate = (endPrice - startPrice) / startPrice * 100;
        int rise = (int) (riseRate * 100);
        return (float) (rise * 1.0 / 100);
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
