package com.sbi.dao;

import com.sbi.model.DailyBeforeRestorationStockData;
import com.sbi.model.DailyUnrestorationStockData;
import com.sbi.model.KDJAndAverage;

import java.util.List;

public interface DailyBeforeRestorationStockDataDao {
    public String TABLE_NAME = "daily_pre_restoration_stock_data";
    public void batchInsert(List<DailyBeforeRestorationStockData> dailyBeforeRestorationStockDataList);
    public int getStockTradeDayCount(String stockId);
    public DailyBeforeRestorationStockData getDailyBeforeRestorationStockData(String statDate);

    public List<String> getAllStockId();
    public void batchUpdateKDJ(List<KDJAndAverage> kdjAndAverageList);
    public void batchUpdateAVG(List<KDJAndAverage> averageList);

    public void batchUpdateAVG5Line(List<KDJAndAverage> averageList);
    public void batchUpdateAVG250Line(List<KDJAndAverage> averageList);
}
