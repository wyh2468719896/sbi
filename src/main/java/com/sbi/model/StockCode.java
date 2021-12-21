package com.sbi.model;

import lombok.Data;

@Data
public class StockCode {

    public String stockId;
    public String TsStockCode;
    public String stockName;
    //行业
    public String industry;
    //板块
    public String market;
    //上市状态 L上市 D退市 P暂停上市
    public String listStatus;
    //上市日期
    public String listDate;
    //退市日期
    public String delistDate;


}
