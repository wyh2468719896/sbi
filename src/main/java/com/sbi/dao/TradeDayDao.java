package com.sbi.dao;


import com.sbi.model.TradeDay;

import java.util.List;

public interface TradeDayDao {
    public String TABLE_NAME = "trade_day";
    public void batchInsert(List<TradeDay> tradeDayList);
    public List<TradeDay> queryList();
    public String getTradeDay(String tradeDay);
//    //补入trade_day数据后，重排rank_no
//    public void updateTradeDayRankNo();
}
