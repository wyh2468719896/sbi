package com.sbi.model;

import lombok.Data;

@Data
public class KDJAndAverage {
    public String stockId;
    public String statDate;
    public float kdj_k;
    public float kdj_d;
    public float kdj_j;
    public float average_5_line;
    public float average_250_line;
}
