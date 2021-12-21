package com.sbi.model;

import lombok.Data;

@Data
public class DailyBeforeRestorationStockData {

    //基础数据
    //统计日（交易日）
    public String statDate;
    public Integer tradeDayRankNo;
    //股票编号
    public String stockId;
    //开盘价
    public float open;
    //收盘价
    public float close;
    //最高价
    public float high;
    //最低价
    public float low;
    //成交量
    public float vol;
    //成交额
    public float amount;
    //5日均线
    public float average_5_line;
    //250日均线
    public float average_250_line;
    //KDJ_K
    public float kdj_k;
    //KDJ_D
    public float kdj_d;
    //KDJ_J
    public float kdj_j;

    public DailyBeforeRestorationStockData() {

    }

    public DailyBeforeRestorationStockData(String stockId,Integer tradeDayRankNo,  float kdj_k, float kdj_d, float kdj_j) {
        this.tradeDayRankNo = tradeDayRankNo;
        this.stockId = stockId;
        this.kdj_k = kdj_k;
        this.kdj_d = kdj_d;
        this.kdj_j = kdj_j;
    }
}
