package com.sbi.model;

import lombok.Data;

@Data
public class TradeDay {
    String tradeDay;
    Integer rankNo;

    public TradeDay() {
    }

    public TradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }

    public TradeDay(String tradeDay, Integer rankNo) {
        this.tradeDay = tradeDay;
        this.rankNo = rankNo;
    }

    public String getTradeDay() {
        return tradeDay;
    }

    public void setTradeDay(String tradeDay) {
        this.tradeDay = tradeDay;
    }
}
