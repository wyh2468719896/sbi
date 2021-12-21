package com.sbi.model;

import com.sbi.util.ToolUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DailyUnrestorationStockData implements Comparable<DailyUnrestorationStockData> {
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
    //昨日收盘
    public float preClose;
    //涨跌额
    public float change;
    //成交量
    public float vol;
    //成交额
    public float amount;
    //复权因子
    public float adjFactor;
    //status 是否需要处理历史前复权数据，1为需要，0为不需要
    public int restorationStatus;


    @Override
    public int compareTo(DailyUnrestorationStockData o) {
        return this.statDate.compareTo(o.statDate);
    }


    public static List<DailyBeforeRestorationStockData> transferToBeforeRestorationList(List<DailyUnrestorationStockData> list){
        if (list == null || list.size() == 0){
            return new ArrayList<>();
        }
        List<DailyBeforeRestorationStockData> resultList = new ArrayList<>();
        for (DailyUnrestorationStockData item : list) {
            DailyBeforeRestorationStockData beforeRestorationStockData = new DailyBeforeRestorationStockData();
            BeanUtils.copyProperties(item,beforeRestorationStockData);
            resultList.add(beforeRestorationStockData);
        }
        return resultList;
    }
}
