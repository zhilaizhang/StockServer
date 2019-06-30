package com.zlzhang.utils;

import com.zlzhang.stockmodel.StockModel;

import java.util.List;

public class BollUtil {
    /**
     * N日平均线
     * @return
     */
    public static float getMA(List<StockModel> stockModels){
        if (stockModels == null) {
            return 0;
        }
        int days = stockModels.size();
        float sum = 0;
        for (StockModel stockModel : stockModels) {
            sum += stockModel.getNowPrice();
        }
        return sum / days;
    }

    /**
     * 计算上轨线
     * @return
     */
    public static float getUP(List<StockModel> stockModels){
        float MB = getMA(stockModels);
        float MD = getMD(stockModels);
        return MB + 2 * MD;
    }

    /**
     * 计算下轨线
     * @param stockModels
     * @return
     */
    public static float getDN(List<StockModel> stockModels){
        float MB = getMA(stockModels);
        float MD = getMD(stockModels);
        return MB + 2 * MD;
    }

    /**
     * 计算标准差
     * @return
     */
    public static float getMD(List<StockModel> stockModels){
        int days = stockModels.size();
        float MA = getMA(stockModels);
        float sum = 0;
        for (StockModel stockModel : stockModels) {
            sum += Math.pow(stockModel.getNowPrice(), 2);
        }
        return (float) Math.sqrt(sum / days);
    }



    /**
     * 计算 乖离率=[(当日收盘价-N日平均价)/N日平均价]*100%
     * @param stockModels
     * @return
     */
    public static float getBIAS(List<StockModel> stockModels){
        if (stockModels == null || stockModels.size() == 0) {
            return 0;
        }
        int length = stockModels.size();
        float MA = getMA(stockModels);
        if (MA == 0) {
            return 0;
        }
        StockModel stockModel = stockModels.get(length - 1);
        return  (stockModel.getNowPrice() - MA) / MA;
    }
}
