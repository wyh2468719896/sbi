package com.sbi.model;

import lombok.Data;

@Data
public class KDJParam {
    public String stockId;
    public String statDate;
    public Integer tradeDayRankNo;
    public float close;
    public float ln;
    public float hn;

    public float getRsv(){
        return (this.close - this.ln)/(this.hn - this.ln)*100;
    }
}
