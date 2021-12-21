package com.sbi.dao;

import com.sbi.model.DailyUnrestorationStockData;

import java.util.List;

public interface DailyUnrestorationStockDataDao {
    public String TABLE_NAME = "daily_un_restoration_stock_data";
    public void batchInsert(List<DailyUnrestorationStockData> dailyUnrestorationStockDataList);
    public void updateTradeDayRankNo(String tsStockCode);
    public List<String> getAllStockId();
    public DailyUnrestorationStockData getDailyUnrestorationStockData(String stockId,String statDate);
    public List<DailyUnrestorationStockData> getDailyUnrestorationStockDataByStatDate(String statDate);
}
