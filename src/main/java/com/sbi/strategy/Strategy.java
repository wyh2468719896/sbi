package com.sbi.strategy;

import com.sbi.model.StockContainedAnalysisiData;

import java.util.List;

/**
 * 策略类种类：
 * 1.均线策略类
 * 2.成交量策略类
 * 3.股价策略类
 * 4.kdj策略类
 * 5.macd策略类
 */
public interface Strategy {
    //返回值代表是否满足硬指标策略，如果不满足硬指标策略，后续策略不在进行下去
    public boolean filterByStrategy(StockContainedAnalysisiData stock);
    public List<String> getAllSelectedStrategyNameList();
}
