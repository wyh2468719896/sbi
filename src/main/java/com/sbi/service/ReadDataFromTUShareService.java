package com.sbi.service;

import com.sbi.model.*;

import java.util.List;

public interface ReadDataFromTUShareService {

    //获取一只票的存量均线数据
    public List<KDJAndAverage> getHistoryStockAverageLine(String stockId);

    //获取一只票的某个统计日的均线数据
    public KDJAndAverage getStatDateStockAverageLine(String stockId,String statDate);

    //获取一只票的存量kdj数据
    public List<KDJAndAverage> getHistoryStockKDJ(String stockId);

    //获取一只票的某个统计日的kdj数据
    public KDJAndAverage getStatDateStockKDJ(String stockId,String statDate);

    //获取一只票上市第一天至统计日期间全部未复权数据
    public List<DailyUnrestorationStockData> getUnRestorationDataList(String tsStockCode, String statDate);

    //获取一只票某个交易日的未复权数据
    public DailyUnrestorationStockData getTradeDayUnRestorationData(String tsStockCode, String tradeDate);

    //获取某个交易日所有票的未复权数据
    public List<DailyUnrestorationStockData> getTradeDayAllUnRestorationData(String tradeDate);

    //获取某个交易日所有票的复权因子
    public List<StockAdjFactor> getTradeDayAllStockAdjFactor(String tradeDate);

    //获取一只票上市第一天至统计日每个交易日的复权因子
    public List<StockAdjFactor> getAdjFactorList(String tsStockCode, String statDate);

    //获取一只票某个交易日范围的复权因子
    public List<StockAdjFactor> getAdjFactorList(String tsStockCode, String startTradeDay,String endTradeDay);

    //获取全部股票编码
    public List<StockCode> getAllStockCodeList();

    //生成直到明年之前的所有交易日
    public List<TradeDay> getBeforeNextYearAllTradeDayList(String nextYear);
}
