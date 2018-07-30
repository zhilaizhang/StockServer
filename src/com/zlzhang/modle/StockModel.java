package com.zlzhang.modle;

import java.io.Serializable;

public class StockModel  implements Serializable {


    private int    id;              //数据库存储id
    private String code;           //股票代码
    private String name;           //股票名称
    private float todayOpen;       //今开
    private float yesterdayClose;  //昨收
    private float nowPrice;        //当前价格
    private float todayHighest;    //最高
    private float todayLowest;    //最低
    private long  dealNum;        //成交股票数  由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百
    private double  OBV;            //成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
    private String date;          //日期
    private String time;          //时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTodayOpen() {
        return todayOpen;
    }

    public void setTodayOpen(float todayOpen) {
        this.todayOpen = todayOpen;
    }

    public float getYesterdayClose() {
        return yesterdayClose;
    }

    public void setYesterdayClose(float yesterdayClose) {
        this.yesterdayClose = yesterdayClose;
    }

    public float getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(float nowPrice) {
        this.nowPrice = nowPrice;
    }

    public float getTodayHighest() {
        return todayHighest;
    }

    public void setTodayHighest(float todayHighest) {
        this.todayHighest = todayHighest;
    }

    public float getTodayLowest() {
        return todayLowest;
    }

    public void setTodayLowest(float todayLowest) {
        this.todayLowest = todayLowest;
    }

    public long getDealNum() {
        return dealNum;
    }

    public void setDealNum(long dealNum) {
        this.dealNum = dealNum;
    }

    public double getOBV() {
        return OBV;
    }

    public void setOBV(double OBV) {
        this.OBV = OBV;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
