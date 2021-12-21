package com.sbi.service.impl;

import com.google.gson.*;
import com.sbi.dao.DailyBeforeRestorationStockDataDao;
import com.sbi.dao.DailyUnrestorationStockDataDao;
import com.sbi.dao.StockCodeDao;
import com.sbi.dao.TradeDayDao;
import com.sbi.model.*;
import com.sbi.service.GeneraterService;
import com.sbi.service.ReadDataFromTUShareService;
import com.sbi.strategy.Strategy;
import com.sbi.util.HttpClientUtil;
import com.sbi.util.ToolUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneraterServiceImpl implements GeneraterService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StockCodeDao stockCodeDao;
    @Autowired
    TradeDayDao tradeDayDao;
    @Autowired
    DailyBeforeRestorationStockDataDao dailyBeforeRestorationStockDataDao;
    @Autowired
    DailyUnrestorationStockDataDao dailyUnrestorationStockDataDao;
    @Autowired
    ReadDataFromTUShareService readDataFromTUShareService;

    public static Gson gson = new Gson();




    //写入策略分析表

    //8.根据分析结果筛选备选股，并发送邮件
    public void sendNeedManualReviewStocksExcelByEmail(String getNeedManualReviewStocksSql){
        //todo 从库中获取某个统计日的 需要人工审查的股票数据
        List<StockContainedAnalysisiData> stockList = new ArrayList<StockContainedAnalysisiData>();
        //todo 生成excel
        //todo 发送邮件
    }


    //7.根据策略生成统计日股票的分析结果
    public void genStockAnalysisByStrategies(String statDate,String getStrategyNeedBasicDataSql,List<Strategy> strategyList){
        //从库中获取某个统计日的股票数据
        List<StockContainedAnalysisiData> stockList = new ArrayList<StockContainedAnalysisiData>();
        //通过策略类来进行筛选，并把符合的和不符合的策略标出

        for(StockContainedAnalysisiData stock : stockList){

            //全部策略指标结果赋初值 ,把策略列表生成map,键为策略名，值均为“--”
            for (Strategy strategy : strategyList){
                strategy.getAllSelectedStrategyNameList().forEach(strategyName -> stock.strategyResultMap.put(strategyName,"--"));
            }
            //策略检验并得出分析结果
            for (Strategy strategy : strategyList){

                boolean strongStrategy = strategy.filterByStrategy(stock);
                if(!strongStrategy){
                    break;
                }
            }
            //将分析结果写入每日分析表 todo

        }
        //将所有票的分析情况写入excel中，并单独列出一页为满足所有传入策略的股票的数据,以及当日分析所用的所有策略及策略详情 todo 拆出
    }





    // 6.每日同步当天所有股票的数据（定时任务：每日15:30调用）
    public void syncDailyAllStockData(String curTradeDate , String preTradeDay) throws ParseException {
//        String preTradeDay = ToolUtil.strDateAdd(curTradeDate, -1);
        //1. 同步新票的stock_code
        List<StockCode> allStockCodeList = readDataFromTUShareService.getAllStockCodeList();
        allStockCodeList.forEach(item -> stockCodeDao.insertOrUpdate(item));
        //2. 同步每只票的当日基础未复权数据及前复权因子，找出当日因除权至复权因子改变的票
        //2.1 获取截止当日所有处于上市状态的股票代码
        allStockCodeList = stockCodeDao.getAllListStockTsCode();
        //今日无除权的的股票今日未复权数据，在生成前复权数据时，直接生成今天的前复权数据即可
        List<DailyUnrestorationStockData> unchangedAdjFactorStockDatalist = new ArrayList<>();
        //今日有除权的的股票今日未复权数据，在生成前复权数据时，需要调整这只票的历史所有前复权数据
        List<DailyUnrestorationStockData> changedAdjFactorStockDatalist = new ArrayList<>();


        //获取今日所有票的日线未复权数据
        List<DailyUnrestorationStockData> tradeDayAllUnRestorationData = readDataFromTUShareService.getTradeDayAllUnRestorationData(curTradeDate);
        //获取今日所有票的复权因子
        List<StockAdjFactor> tradeDayAllStockAdjFactor = readDataFromTUShareService.getTradeDayAllStockAdjFactor(curTradeDate);
        //给日线未复权数据设置复权因子
        Map<String, StockAdjFactor> stockAdjFactorMap = tradeDayAllStockAdjFactor.stream().collect(Collectors.toMap(StockAdjFactor::getTsStockCode, item -> item));
        tradeDayAllUnRestorationData.forEach(item -> item.setAdjFactor(stockAdjFactorMap.get(item.getStockId()).getAdjFactor()));
        //将所有日线未复权数据插入未复权数据表中
        dailyUnrestorationStockDataDao.batchInsert(tradeDayAllUnRestorationData);
        //将所有新插入的今日日线未复权数据设置trade_day_rank_no
        String updateTodayUnrestorationDataTradeDayRankNoSql =
                "UPDATE daily_un_restoration_stock_data AS d,(\n" +
                "  SELECT \n" +
                "    d1.stat_date AS stat_date,\n" +
                "    d1.stock_id AS stock_id,\n" +
                "    d2.trade_day_rank_no+1 AS trade_day_rank_no\n" +
                "  FROM daily_un_restoration_stock_data AS d1,\n" +
                "       daily_un_restoration_stock_data AS d2\n" +
                "  WHERE \n" +
                "    d1.stock_id = d2.stock_id\n" +
                "    AND d1.stat_date = ? \n" +
                "    AND d2.stat_date = ? \n" +
                ") AS g\n" +
                "SET d.trade_day_rank_no = g.trade_day_rank_no\n" +
                "WHERE d.stock_id = g.stock_id AND d.stat_date = g.stat_date";
        jdbcTemplate.update(updateTodayUnrestorationDataTradeDayRankNoSql,curTradeDate,preTradeDay);
        //从未复权数据表中获取今日的日线未复权数据
        List<DailyUnrestorationStockData> dailyUnrestorationStockDataList = dailyUnrestorationStockDataDao.getDailyUnrestorationStockDataByStatDate(curTradeDate);
        //将今日所有未复权数据插入前复权数据表中
        List<DailyBeforeRestorationStockData> dailyBeforeRestorationStockDataList = DailyUnrestorationStockData.transferToBeforeRestorationList(dailyUnrestorationStockDataList);
        dailyBeforeRestorationStockDataDao.batchInsert(dailyBeforeRestorationStockDataList);
        //找出所有今日除权的股票标号
        String getTodayRestorationStockIdSql =
                "SELECT DISTINCT d1.stock_id AS stock_id\n" +
                "FROM daily_un_restoration_stock_data AS d1,daily_un_restoration_stock_data AS d2\n" +
                "WHERE \n" +
                "  d1.stock_id = d2.stock_id \n" +
                "  AND d1.stat_date = ? \n" +
                "  AND d2.stat_date = ? \n" +
                "  AND d1.adj_factor != d2.adj_factor";
        List<String> todayRestorationStockIdList = jdbcTemplate.queryForList(getTodayRestorationStockIdSql, String.class, curTradeDate, preTradeDay);
        //调整前复权表中今日除权股票的历史数据
        for (String stockId : todayRestorationStockIdList) {
            String updateStockHistoryDprsql =
                    "UPDATE daily_pre_restoration_stock_data AS dpr, (\n" +
                            "  SELECT  \n" +
                            "    d1.stat_date,d1.trade_day_rank_no,d1.stock_id, \n" +
                            "    ROUND(d1.`open` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `open`, \n" +
                            "    ROUND(d1.`close` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `close`, \n" +
                            "    ROUND(d1.`low` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `low`, \n" +
                            "    ROUND(d1.`high` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `high`, \n" +
                            "    `vol` AS `vol`, \n" +
                            "    `amount` AS `amount` \n" +
                            "  FROM \n" +
                            "    daily_un_restoration_stock_data AS d1, ( \n" +
                            "       \n" +
                            "      SELECT stock_id,max(`adj_factor`) AS max_adj_factor  \n" +
                            "      FROM daily_un_restoration_stock_data GROUP BY stock_id \n" +
                            "       \n" +
                            "    ) AS d2 \n" +
                            "  WHERE \n" +
                            "    d1.stock_id = d2.stock_id AND d1.stock_id = ?\n" +
                            ") AS g\n" +
                            "SET dpr.trade_day_rank_no = g.trade_day_rank_no ,\n" +
                            "    dpr.`open` = g.`open` ,\n" +
                            "    dpr.`close` = g.`close` ,\n" +
                            "    dpr.`low` = g.`low` ,\n" +
                            "    dpr.`high` = g.`high` ,\n" +
                            "    dpr.`vol` = g.`vol` ,\n" +
                            "    dpr.`amount` = g.`amount` \n" +
                            "WHERE dpr.stock_id = g.stock_id AND dpr.stat_date = g.stat_date ";
            jdbcTemplate.update(updateStockHistoryDprsql, stockId);
            //4. 更新当日每只票的kdj数据
//                List<DailyBeforeRestorationStockData> updateHistoryDprStockDataList = calStockHistoryKDJData(stockCode.getTsStockCode());
            //批量更新这只票的kdj数据
            List<KDJAndAverage> historyStockKDJList = readDataFromTUShareService.getHistoryStockKDJ(stockId);
            dailyBeforeRestorationStockDataDao.batchUpdateKDJ(historyStockKDJList);
            //5. 更新一只票历史的均线数据
            List<KDJAndAverage> historyStockAverageLineList = readDataFromTUShareService.getHistoryStockAverageLine(stockId);
            dailyBeforeRestorationStockDataDao.batchUpdateAVG(historyStockAverageLineList);
        }

//        统一设置今天的前复权数据的kdj、均线数据
        String getTradeDayAllStockKdjSql =
                "SELECT dpr1.stat_date AS stat_date,dpr1.stock_id AS stock_id, \n" +
                "      IFNULL(ROUND( (2/3*dpr2.kdj_k + 1/3*(dpr1.`close` - min(dpr3.`low`))/(max(dpr3.`high`) - min(dpr3.`low`))*100),2),0)   AS kdj_k, \n" +
                "      IFNULL(ROUND((2/3*dpr2.kdj_d + 1/3*(2/3*dpr2.kdj_k + 1/3*(dpr1.`close` - min(dpr3.`low`))/(max(dpr3.`high`) - min(dpr3.`low`))*100)),2),0) AS kdj_d, \n" +
                "      IFNULL(ROUND((3*(2/3*dpr2.kdj_k + 1/3*(dpr1.`close` - min(dpr3.`low`))/(max(dpr3.`high`) - min(dpr3.`low`))*100) - 2* (2/3*dpr2.kdj_d + 1/3*(2/3*dpr2.kdj_k + 1/3*(dpr1.`close` - min(dpr3.`low`))/(max(dpr3.`high`) - min(dpr3.`low`))*100))),2),0) AS kdj_j \n" +
                "    FROM daily_pre_restoration_stock_data AS dpr1, \n" +
                "         daily_pre_restoration_stock_data AS dpr2, \n" +
                "         daily_pre_restoration_stock_data AS dpr3 \n" +
                "    WHERE \n" +
                "      dpr1.stock_id = dpr2.stock_id \n" +
                "      AND dpr2.stock_id = dpr3.stock_id \n" +
                "      AND dpr1.stat_date = ?  -- 统计日 \n" +
                "      AND dpr2.stat_date = ?  -- 统计日前日 \n" +
                "      AND dpr1.trade_day_rank_no - dpr3.trade_day_rank_no >= 0 \n" +
                "      AND dpr1.trade_day_rank_no - dpr3.trade_day_rank_no < 9 \n" +
                "    GROUP BY dpr1.stat_date,dpr1.stock_id ";
        List<KDJAndAverage> updateKDJList = jdbcTemplate.query(getTradeDayAllStockKdjSql, new Object[]{curTradeDate, preTradeDay}, new BeanPropertyRowMapper<>(KDJAndAverage.class));
        dailyBeforeRestorationStockDataDao.batchUpdateKDJ(updateKDJList);

        //统一设置今天的前复权数据的均线数据
        //更新统计日所有股票的5日均线数据
        String getTradeDayAllStockAVG5LineSql =
                "  SELECT dpr1.stat_date AS stat_date,\n" +
                "         dpr1.stock_id AS stock_id,\n" +
                "         ROUND(((dpr2.average_5_line*5 - dpr3.`close` + dpr1.`close`)/5),2) AS average_5_line\n" +
                "  FROM daily_pre_restoration_stock_data AS dpr1,\n" +
                "       daily_pre_restoration_stock_data AS dpr2,\n" +
                "       daily_pre_restoration_stock_data AS dpr3\n" +
                "  WHERE \n" +
                "    dpr1.stock_id = dpr2.stock_id\n" +
                "    AND dpr2.stock_id = dpr3.stock_id\n" +
                "    AND dpr1.stat_date = ?  -- 统计日\n" +
                "    AND dpr2.stat_date = ?  -- 统计日前日\n" +
                "    AND dpr3.trade_day_rank_no = dpr1.trade_day_rank_no - 5\n" +
                "  GROUP BY dpr1.stat_date,dpr1.stock_id\n" ;
        List<KDJAndAverage> updateAVG5List = jdbcTemplate.query(getTradeDayAllStockAVG5LineSql, new Object[]{curTradeDate, preTradeDay}, new BeanPropertyRowMapper<>(KDJAndAverage.class));
        dailyBeforeRestorationStockDataDao.batchUpdateAVG5Line(updateAVG5List);

        //更新统计日所有股票的250日均线数据
        String getTradeDayAllStockAVG250LineSql =
                "  SELECT dpr1.stat_date AS stat_date,\n" +
                "         dpr1.stock_id AS stock_id,\n" +
                "         ROUND(((dpr2.average_250_line*250 - dpr3.`close` + dpr1.`close`)/250),2) AS average_250_line\n" +
                "  FROM daily_pre_restoration_stock_data AS dpr1,\n" +
                "       daily_pre_restoration_stock_data AS dpr2,\n" +
                "       daily_pre_restoration_stock_data AS dpr3\n" +
                "  WHERE \n" +
                "    dpr1.stock_id = dpr2.stock_id\n" +
                "    AND dpr2.stock_id = dpr3.stock_id\n" +
                "    AND dpr1.stat_date = ?  -- 统计日\n" +
                "    AND dpr2.stat_date = ?  -- 统计日前日\n" +
                "    AND dpr3.trade_day_rank_no = dpr1.trade_day_rank_no - 250\n" +
                "  GROUP BY dpr1.stat_date,dpr1.stock_id\n" ;
        List<KDJAndAverage> updateAVG250List = jdbcTemplate.query(getTradeDayAllStockAVG250LineSql, new Object[]{curTradeDate, preTradeDay}, new BeanPropertyRowMapper<>(KDJAndAverage.class));
        dailyBeforeRestorationStockDataDao.batchUpdateAVG250Line(updateAVG250List);

    }

    //5.给存量前复权数据设置5日均线和250日均线
    @Override
    public void updateHistoryBeforeRestorationAverageData(){
        //获取前复权表中所有stock_id
        List<String> allStockIdList = dailyBeforeRestorationStockDataDao.getAllStockId();

        for (String stockId : allStockIdList){
            List<KDJAndAverage> historyStockAVGList = readDataFromTUShareService.getHistoryStockAverageLine(stockId);
            dailyBeforeRestorationStockDataDao.batchUpdateAVG(historyStockAVGList);
        }
    }


    //4.给存量前复权数据设置kdj
    @Override
    public void updateHistoryBeforeRestorationKDJData(){
        //kdj数据计算需要依赖前一天的kdj数据，所以只能一天一天依次计算
        //1.获取前复权表中所有stock_id
        List<String> allStockIdList = dailyBeforeRestorationStockDataDao.getAllStockId();
        for (String stockId : allStockIdList){
            //这种通过sql计算kdj的方法暂时废弃，原因是计算时间太长，一只票大约要计算15秒，5000只票就是20小时
//            List<DailyBeforeRestorationStockData> batchUpdateList = calStockHistoryKDJData(stockId);
            //通过接口获取kdj数据
            List<KDJAndAverage> historyStockKDJList = readDataFromTUShareService.getHistoryStockKDJ(stockId);
            //批量更新这只票的kdj数据
            dailyBeforeRestorationStockDataDao.batchUpdateKDJ(historyStockKDJList);
        }
    }


    // 3. 生成存量前复权基础数据 todo
    @Override
    public void genHistoryBeforeRestorationData(){
        List<String> allStockId = dailyUnrestorationStockDataDao.getAllStockId();
        for (String stockId : allStockId) {
            //根据未复权存量数据生成前复权存量数据
            String queryHistoryBeforeRestorationDataSql =
                    "SELECT \n" +
                            "  d1.stat_date,d1.trade_day_rank_no,d1.stock_id,\n" +
                            "  ROUND(d1.`open` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `open`,\n" +
                            "  ROUND(d1.`close` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `close`,\n" +
                            "  ROUND(d1.`low` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `low`,\n" +
                            "  ROUND(d1.`high` * d1.`adj_factor` / d2.`max_adj_factor`,2) AS `high`,\n" +
                            "  `vol` AS `vol`,\n" +
                            "  `amount` AS `amount`\n" +
                            "FROM\n" +
                            "  daily_un_restoration_stock_data AS d1, (\n" +
                            "    \n" +
                            "    SELECT stock_id,max(`adj_factor`) AS max_adj_factor \n" +
                            "    FROM daily_un_restoration_stock_data WHERE stock_id = ? \n" +
                            "    \n" +
                            "  ) AS d2\n" +
                            "WHERE\n" +
                            "  d1.stock_id = d2.stock_id  AND d1.stock_id = ?\n";
            List<DailyBeforeRestorationStockData> historyBeforeRestorationDataList = jdbcTemplate.query(queryHistoryBeforeRestorationDataSql, new Object[]{stockId,stockId}, new BeanPropertyRowMapper(DailyBeforeRestorationStockData.class));
            dailyBeforeRestorationStockDataDao.batchInsert(historyBeforeRestorationDataList);
            System.out.println(stockId);
        }
    }

    //2. 生成存量未复权数据
    @Override
    public void genHistoryUnrestorationData(String statDate) throws ParseException {
        //1.获取所有处于上市状态的股票编码及上市日期
        List<StockCode> allStockTsCodeList = stockCodeDao.getAllListStockTsCode();

        for (int i = 0; i<allStockTsCodeList.size();i++){
            StockCode stockCode  = allStockTsCodeList.get(i);
            Set<StockAdjFactor>  allAdjFactorSet = new HashSet<>(10000);
            String endDate = statDate;
            int count = 0;
            do{
                count++;
                List<StockAdjFactor> adjFactorList = readDataFromTUShareService.getAdjFactorList(stockCode.getTsStockCode(), endDate);
                StockAdjFactor earliestStockAdjFactor = adjFactorList.stream().filter(Objects::nonNull).min(Comparator.comparing(StockAdjFactor::getTradeDate)).get();
                allAdjFactorSet.addAll(adjFactorList);
                endDate = earliestStockAdjFactor.getTradeDate();
            }while(ToolUtil.strDateAfter(endDate,stockCode.getListDate()) && count < 4);

            Set<DailyUnrestorationStockData> allUnRestorationDataSet = new TreeSet<>();
            endDate = statDate;
            count = 0;
            do{
                count++;
                List<DailyUnrestorationStockData> unRestorationDataList = readDataFromTUShareService.getUnRestorationDataList(stockCode.getTsStockCode(), endDate);
                DailyUnrestorationStockData earlestDailyUnrestorationStockData = unRestorationDataList.stream().filter(Objects::nonNull).min(Comparator.comparing(DailyUnrestorationStockData::getStatDate)).get();
                allUnRestorationDataSet.addAll(unRestorationDataList);
                endDate = earlestDailyUnrestorationStockData.getStatDate();
            }while(ToolUtil.strDateDecrement(endDate,stockCode.getListDate()) > 1 && count < 4);

            Map<String, Float> adjFactorMap = allAdjFactorSet.stream().collect(Collectors.toMap(StockAdjFactor::getTradeDate, StockAdjFactor::getAdjFactor));
            try {
                allUnRestorationDataSet.forEach(item -> item.setAdjFactor(adjFactorMap.get(item.statDate)));
            }catch (Exception e){
                System.out.println( "问题股票编号" + stockCode.getStockId() );
                continue;
            }
            dailyUnrestorationStockDataDao.batchInsert(new ArrayList<>(allUnRestorationDataSet ));
            //更新设置这只票的trade_day_rank_no
            dailyUnrestorationStockDataDao.updateTradeDayRankNo(stockCode.getTsStockCode());
        }
    }


    //1.同步存在的全部股票编号 完成
    @Override
    public void genAllStockCode() {
        List<StockCode> allStockCodeList = readDataFromTUShareService.getAllStockCodeList();
        //写入表中
        stockCodeDao.batchInsert(allStockCodeList);
    }

    //0.同步直到明年的所有交易日
    @Override
    public void genFutureYearTradeDay() {
        String nextYear = "20220101";
        List<TradeDay> allTradeDayList = readDataFromTUShareService.getBeforeNextYearAllTradeDayList(nextYear);
        //写入表中
        tradeDayDao.batchInsert(allTradeDayList);
//        //设置rank_no
//        tradeDayDao.updateTradeDayRankNo();
    }

    //工具方法
    //计算一只票历史的KDJ数据
    @Deprecated
    public List<DailyBeforeRestorationStockData> calStockHistoryKDJData(String stockId){
        int stockTradeDayCount = dailyBeforeRestorationStockDataDao.getStockTradeDayCount(stockId);
        Map<Integer,DailyBeforeRestorationStockData> kdjDataMap = new TreeMap<>();
        //每只股票第一个交易日的kdj数据均为50
        kdjDataMap.put(1,new DailyBeforeRestorationStockData(stockId,1,50,50,50));
        //获取这只股票从第一天之后的所有计算每日kdj的参数
        String getAfterFirstTradeDayCalDailyKDJParamSql =
                "SELECT \n" +
                        "  dpr1.stat_date AS stat_date ,\n" +
                        "  dpr1.trade_day_rank_no AS trade_day_rank_no ,\n" +
                        "  dpr1.stock_id AS stock_id, \n" +
                        "  dpr1.`close` AS `close`, \n" +
                        "  min(dpr2.low) AS ln,max(dpr2.high) AS hn\n" +
                        "FROM daily_pre_restoration_stock_data AS dpr1,daily_pre_restoration_stock_data AS dpr2\n" +
                        "WHERE \n" +
                        "  dpr1.stock_id = dpr2.stock_id\n" +
                        "  AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no < 9\n" +
                        "  AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no >= 0\n" +
                        "  AND dpr1.trade_day_rank_no >1 \n" +
                        "  AND dpr1.stock_id = ? \n" +
                        "GROUP BY dpr1.stat_date,dpr1.stock_id ";
        List<KDJParam> paramList = jdbcTemplate.query(getAfterFirstTradeDayCalDailyKDJParamSql, new Object[]{stockId}, new BeanPropertyRowMapper(KDJParam.class));
        Map<Integer, KDJParam> paramMap = paramList.stream().collect(Collectors.toMap(KDJParam::getTradeDayRankNo, item -> item));

        for (int i = 1; i < stockTradeDayCount; i++){
            float pre_kdj_k = kdjDataMap.get(i).getKdj_k();
            float pre_kdj_d = kdjDataMap.get(i).getKdj_d();
            KDJParam curKDJParam = paramMap.get(i + 1);
            //按照kdj计算公式计算今日 kdj_k,kdj_d,kdj_j
            float rsv = curKDJParam.getRsv();

            float cur_kdj_k = (float)(Math.round((2.0/3.0*pre_kdj_k + 1.0/3.0*rsv)*100.0)/100.0);
            float cur_kdj_d = (float)(Math.round((2.0/3.0*pre_kdj_d + 1.0/3.0*cur_kdj_k)*100.0)/100.0);
            float cur_kdj_j = (float)(Math.round((3.0*cur_kdj_k - 2.0*cur_kdj_d)*100.0)/100.0);
            kdjDataMap.put(i+1,new DailyBeforeRestorationStockData(stockId,i+1,cur_kdj_k,cur_kdj_d,cur_kdj_j));
        }
        List<DailyBeforeRestorationStockData> batchUpdateList = new ArrayList<>(kdjDataMap.values());
        return batchUpdateList;

    }
    //计算一只票某一天的KDJ数据，依赖前一天数据，所以前一天数据必须准确
    @Deprecated
    public DailyBeforeRestorationStockData calStockSomedayKDJData(String stockId,String tradeDay) throws ParseException {
        String preTradeDay = ToolUtil.strDateAdd(tradeDay,-1);


        DailyBeforeRestorationStockData preDprdata = dailyBeforeRestorationStockDataDao.getDailyBeforeRestorationStockData(preTradeDay);
        //获取这只股票从某个交易日的所有计算每日kdj的参数
        String getAfterFirstTradeDayCalDailyKDJParamSql =
                "SELECT \n" +
                        "  dpr1.stat_date AS stat_date ,\n" +
                        "  dpr1.trade_day_rank_no AS trade_day_rank_no ,\n" +
                        "  dpr1.stock_id AS stock_id, \n" +
                        "  dpr1.`close` AS `close`, \n" +
                        "  min(dpr2.low) AS ln,max(dpr2.high) AS hn\n" +
                        "FROM daily_pre_restoration_stock_data AS dpr1,daily_pre_restoration_stock_data AS dpr2\n" +
                        "WHERE \n" +
                        "  dpr1.stock_id = dpr2.stock_id\n" +
                        "  AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no < 9\n" +
                        "  AND dpr1.trade_day_rank_no - dpr2.trade_day_rank_no >= 0\n" +
                        "  AND dpr1.trade_day_rank_no >1 \n" +
                        "  AND dpr1.stock_id = ? AND dpr1.stat_date = ? \n" +
                        "GROUP BY dpr1.stat_date,dpr1.stock_id ";
        List<KDJParam> paramList = jdbcTemplate.query(getAfterFirstTradeDayCalDailyKDJParamSql, new Object[]{stockId,tradeDay}, new BeanPropertyRowMapper(KDJParam.class));
        Map<Integer, KDJParam> paramMap = paramList.stream().collect(Collectors.toMap(KDJParam::getTradeDayRankNo, item -> item));


        float pre_kdj_k = preDprdata.getKdj_k();
        float pre_kdj_d = preDprdata.getKdj_d();
        KDJParam curKDJParam = paramList.get(0);
        //按照kdj计算公式计算今日 kdj_k,kdj_d,kdj_j
        float rsv = curKDJParam.getRsv();

        float cur_kdj_k = (float)(Math.round((2.0/3.0*pre_kdj_k + 1.0/3.0*rsv)*100.0)/100.0);
        float cur_kdj_d = (float)(Math.round((2.0/3.0*pre_kdj_d + 1.0/3.0*cur_kdj_k)*100.0)/100.0);
        float cur_kdj_j = (float)(Math.round((3.0*cur_kdj_k - 2.0*cur_kdj_d)*100.0)/100.0);

        DailyBeforeRestorationStockData updateDprData = new DailyBeforeRestorationStockData();
        updateDprData.setStatDate(tradeDay);
        updateDprData.setStockId(stockId);
        updateDprData.setKdj_k(cur_kdj_k);
        updateDprData.setKdj_d(cur_kdj_d);
        updateDprData.setKdj_j(cur_kdj_j);
        return updateDprData;
    }

}