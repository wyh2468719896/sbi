package com.sbi.service;

import com.sbi.model.StockCode;

import java.text.ParseException;
import java.util.List;

public interface GeneraterService {

    public void genAllStockCode();

    public void genHistoryUnrestorationData(String statDate) throws ParseException;
    public void genHistoryBeforeRestorationData();
    public void syncDailyAllStockData(String curTradeDate,String preTradeDate) throws ParseException;
    public void genFutureYearTradeDay();
    public void updateHistoryBeforeRestorationKDJData();
    public void updateHistoryBeforeRestorationAverageData();
}
