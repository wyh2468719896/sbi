package com.sbi.model;

import lombok.Data;

import java.util.List;

@Data
public class StockAdjFactor {
    String tsStockCode;
    String tradeDate;
    Float adjFactor;

    //是否今日是除权日
    public static boolean isTodayRestoration(List<StockAdjFactor> stockAdjFactorList){
        if (stockAdjFactorList == null
            || stockAdjFactorList.size() <= 1
            || stockAdjFactorList.size()>2
        ) {
            throw new RuntimeException("传入参数数量异常");
        }
        if(!stockAdjFactorList.get(0).getTsStockCode().equals(stockAdjFactorList.get(1).getTsStockCode()) ){
            throw new RuntimeException("传入参数股票代码不一致");
        }

        if(stockAdjFactorList.get(0).getAdjFactor().equals(stockAdjFactorList.get(1).getAdjFactor()) ){
            //近两日复权因子无变化，说明没有除权，不需要调整历史前复权数据
            return false;
        }else{
            //近两日复权因子有变化，说明有除权，需要调整历史前复权数据
            return true;
        }
    }
}
