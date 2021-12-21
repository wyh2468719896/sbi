package com.sbi.dao;

import com.sbi.model.StockCode;

import java.util.List;

public interface StockCodeDao {
    public String TABLE_NAME = "stock_code";
    public void batchInsert(List<StockCode> stockCodeList);
    //获取所有处于上市状态的股票代码
    public List<StockCode> getAllListStockTsCode();
    public void insertOrUpdate(StockCode stockCode);
    public StockCode getStockCode(String stockId);
}
