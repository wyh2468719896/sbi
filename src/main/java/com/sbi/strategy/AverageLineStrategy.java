package com.sbi.strategy;

import com.sbi.model.StockContainedAnalysisiData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageLineStrategy implements Strategy{
    public static final String fiveAverageLineIncrement = "五日均线挑头";
    public static final String twoFiveZeroAverageLineIncrement = "二五零日均线挑头";
    public static final String fiveAndTwoFiveZeroAverageLineAbsRange = "五日和二五零日均线差的绝对值范围";


    public StrategyName.AverageLineStrategyName[] selectedStrategies;
    public static Map<StrategyName.AverageLineStrategyName,String> strategiesNameMap = new HashMap<>();
    static {
        strategiesNameMap.put(StrategyName.AverageLineStrategyName.fiveAverageLineIncrement,fiveAverageLineIncrement);
        strategiesNameMap.put(StrategyName.AverageLineStrategyName.twoFiveZeroAverageLineIncrement,twoFiveZeroAverageLineIncrement);
        strategiesNameMap.put(StrategyName.AverageLineStrategyName.fiveAverageLineGTtwoFiveZeroAverageLine,fiveAndTwoFiveZeroAverageLineAbsRange);
    }


    public AverageLineStrategy(){}
    public AverageLineStrategy(StrategyName.AverageLineStrategyName... selectedStrategies){
        this.selectedStrategies = selectedStrategies;
    }

//
//
    public boolean filterByStrategy(StockContainedAnalysisiData stock) {
//        for (StrategyName.AverageLineStrategyName selectedStrategy : selectedStrategies) {
//            boolean filterResult = false;
//            switch (selectedStrategy){
//                case fiveAverageLineIncrement:      //硬指标
//                    //策略方法
//                    filterResult = filterFiveAverageLineIncrement(stock);
//                    stock.strategyResultMap.put(fiveAverageLineIncrement,filterResult+"");
//                    //硬指标的处理,硬指标才加这段
//                    if(!filterResult){
//                        return false;
//                    }else{
//                        break;
//                    }
//                case twoFiveZeroAverageLineIncrement:      //硬指标
//                    //策略方法
//                    filterResult = filterTwoFiveZeroAverageLineIncrement(stock);
//                    stock.strategyResultMap.put(twoFiveZeroAverageLineIncrement,filterResult+"");
//                    //硬指标的处理,硬指标才加这段
//                    if(!filterResult){
//                        return false;
//                    }else{
//                        break;
//                    }
//                case fiveAverageLineGTtwoFiveZeroAverageLine:      //硬指标
//                    //策略方法
//                    filterResult = filterFiveAndTwoFiveZeroAverageLineAbsRange(stock);
//                    stock.strategyResultMap.put(fiveAndTwoFiveZeroAverageLineAbsRange,filterResult+"");
//                    //硬指标的处理,硬指标才加这段
//                    if(!filterResult){
//                        return false;
//                    }else{
//                        break;
//                    }
//            }
//        }
//
        return true;
    }

    //具体的策略方法


//    //1.今日的5日均线挑头，即 今日5日均线数据 大于 昨日 5日均线数据
//    public boolean filterFiveAverageLineIncrement(StockContainedAnalysisiData stock){
//        if(stock.fiveAverageLine > stock.preFiveAverageLine){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    //2.今日的250日均线挑头，即 今日250日均线数据 大于 昨日 250日均线数据
//    public boolean filterTwoFiveZeroAverageLineIncrement(StockContainedAnalysisiData stock){
//        if(stock.twoFiveZeroAverageLine > stock.preTwoFiveZeroAverageLine){
//            return true;
//        }else {
//            return false;
//        }
//    }
//
//    //3.今日的5日均线 和 250日均线 的绝对值 处于一个范围（暂定5%）
//    public boolean filterFiveAndTwoFiveZeroAverageLineAbsRange(StockContainedAnalysisiData stock){
//        double abs = Math.abs(stock.twoFiveZeroAverageLine - stock.fiveAverageLine);
//        if(abs < 0.05){
//            return true;
//        }else {
//            return false;
//        }
//    }

    public List<String> getAllSelectedStrategyNameList(){
        List selectedStrategyNameList = new ArrayList();
        for (StrategyName.AverageLineStrategyName selectedStrategy : selectedStrategies) {
            String selectedStrategyName = strategiesNameMap.get(selectedStrategy);
            selectedStrategyNameList.add(selectedStrategyName);
        }
        return selectedStrategyNameList;
    }

}
