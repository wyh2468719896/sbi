package com.sbi.strategy;

public class StrategyName {
    //均线策略名
    public static enum AverageLineStrategyName{
        fiveAverageLineIncrement,//硬指标策略
        twoFiveZeroAverageLineIncrement,
        fiveAverageLineGTtwoFiveZeroAverageLine
    }

//    //成交量策略名
//    public static enum TurnoverStrategyName{
//        fiveAverageLineIncrement,//硬指标策略
//        twoFiveZeroAverageLineIncrement,
//        fiveAverageLineGTtwoFiveZeroAverageLine
//    }
}
