package com.sbi.service.impl;

import com.google.gson.*;
import com.sbi.dao.DailyUnrestorationStockDataDao;
import com.sbi.dao.StockCodeDao;
import com.sbi.dao.TradeDayDao;
import com.sbi.model.*;
import com.sbi.service.ReadDataFromTUShareService;
import com.sbi.util.HttpClientUtil;
import com.sbi.util.ToolUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReadDataFromTUShareServiceImpl implements ReadDataFromTUShareService {
    @Autowired
    DailyUnrestorationStockDataDao dailyUnrestorationStockDataDao;
    @Autowired
    StockCodeDao stockCodeDao;
    @Autowired
    TradeDayDao tradeDayDao;

    public static final String baseUrl = "http://api.tushare.pro";
    public static final String token = "eb1af3bb9e2280783dcde4930237691c3331b1bc7a229519e93f5f80";
    public static Gson gson = new Gson();


    public static JsonArray readDataItemsFromTUShare(String apiName, Map<String,String> params, String field){

        Map<String,Object> paramMap = new HashMap<>();
        //1.接口名
        paramMap.put("api_name",apiName);
        //2.token
        paramMap.put("token",token);
        //3.接口的输入参数
        paramMap.put("params",params);
        //4.返回的字段列表
        paramMap.put("fields",field);

        String resultJson = HttpClientUtil.doPostJson(baseUrl, gson.toJson(paramMap));
//        System.out.println(resultJson);
        //解析并计算股票相关数据 ，
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultJson);
        JsonObject root = element.getAsJsonObject();
        JsonArray items = root.getAsJsonObject("data").getAsJsonArray("items");
        return items;
    }

    //获取一只票的存量kdj数据
    @Override
    public List<KDJAndAverage> getHistoryStockKDJ(String stockId){
        StockCode stockCode = stockCodeDao.getStockCode(stockId);
        String prefixStockId = stockCode.getStockId();
        String kdjUrl = "http://ig507.com/data/time/history/kdj/" + prefixStockId + "/Day_qfq?licence=";
        String licence = "EEC00CCC-1FD1-AEC9-89BA-1B6D37FB0A24";
        kdjUrl = kdjUrl + licence;
        String resultJson = HttpClientUtil.doGet(kdjUrl);

        List<KDJAndAverage> historyKdjList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultJson);
        JsonArray rootArray = null;
        try {
            rootArray = element.getAsJsonArray();
        }catch (Exception e){
            System.out.println(resultJson);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                e.printStackTrace();
            }
            return  null;
        }

        for (int i = 0; i < rootArray.size(); i++){
            JsonObject asJsonObject = rootArray.get(i).getAsJsonObject();
            KDJAndAverage kdj = new KDJAndAverage();
            kdj.setStockId(stockCode.getTsStockCode());
            kdj.setStatDate(StringUtils.remove(asJsonObject.get("t").getAsString(),'-'));
            kdj.setKdj_k(asJsonObject.get("k").getAsFloat());
            kdj.setKdj_d(asJsonObject.get("d").getAsFloat());
            kdj.setKdj_j(asJsonObject.get("j").getAsFloat());
            historyKdjList.add(kdj);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return historyKdjList;
    }

    //获取一只票的某个统计日的kdj数据
    @Override
    public KDJAndAverage getStatDateStockKDJ(String stockId,String statDate){
        Map<String, KDJAndAverage> statDateStockKdjMap = getHistoryStockKDJ(stockId).stream().collect(Collectors.toMap(KDJAndAverage::getStatDate, item -> item));
        return statDateStockKdjMap.get(statDate);
    }

    //获取一只票的存量均线数据
    @Override
    public List<KDJAndAverage> getHistoryStockAverageLine(String stockId){
        StockCode stockCode = stockCodeDao.getStockCode(stockId);
        String prefixStockId = stockCode.getStockId();
        String kdjUrl = "http://ig507.com/data/time/history/ma/" + prefixStockId + "/Day_qfq?licence=";
        String licence = "EEC00CCC-1FD1-AEC9-89BA-1B6D37FB0A24";
        kdjUrl = kdjUrl + licence;
        String resultJson = HttpClientUtil.doGet(kdjUrl);

        List<KDJAndAverage> historyAVGList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultJson);
        JsonArray rootArray = null;
        try{
            rootArray = element.getAsJsonArray();
        }catch (Exception e){
            System.out.println(stockId + resultJson);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                e.printStackTrace();
            }
            return null;
        }

        for (int i = 0; i < rootArray.size(); i++){
            JsonObject asJsonObject = rootArray.get(i).getAsJsonObject();
            KDJAndAverage average = new KDJAndAverage();
            average.setStockId(stockCode.getTsStockCode());
            average.setStatDate(StringUtils.remove(asJsonObject.get("t").getAsString(),'-'));
            average.setAverage_5_line(asJsonObject.get("ma5").isJsonNull() ? 0F : asJsonObject.get("ma5").getAsFloat());
            average.setAverage_250_line(asJsonObject.get("ma250").isJsonNull() ? 0F : asJsonObject.get("ma250").getAsFloat());
            historyAVGList.add(average);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return historyAVGList;
    }

    //获取一只票的某个统计日的均线数据
    @Override
    public KDJAndAverage getStatDateStockAverageLine(String stockId,String statDate){
        Map<String, KDJAndAverage> statDateStockKdjMap = getHistoryStockAverageLine(stockId).stream().collect(Collectors.toMap(KDJAndAverage::getStatDate, item -> item));
        return statDateStockKdjMap.get(statDate);
    }

    //获取一只票上市第一天至统计日期间全部未复权数据
    @Override
    public List<DailyUnrestorationStockData> getUnRestorationDataList(String tsStockCode, String statDate){
        Map<String,String> params = new HashMap<>();
        params.put("ts_code", tsStockCode);
        params.put("end_date",statDate);
        String fields = "trade_date,ts_code,open,close,high,low,pre_close,change,vol,amount";
        JsonArray items = readDataItemsFromTUShare("daily", params, fields);

        List<DailyUnrestorationStockData> dailyUnrestorationStockDataList = new ArrayList<>();
        String tradeDay = null;
        for (int i = 0; i < items.size(); i++) {
            int j = 0;
            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            DailyUnrestorationStockData dailyUnrestorationStockData = new DailyUnrestorationStockData();
            dailyUnrestorationStockData.setStockId(asJsonArray.get(j++).getAsString());
            dailyUnrestorationStockData.setStatDate(asJsonArray.get(j++).getAsString());
            dailyUnrestorationStockData.setOpen(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setHigh(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setLow(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setClose(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setPreClose(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setChange(asJsonArray.get(j++).getAsFloat());

            if(asJsonArray.get(j).isJsonNull()){
                dailyUnrestorationStockData.setVol(0F);
                j++;
            }else {
                dailyUnrestorationStockData.setVol(asJsonArray.get(j++).getAsFloat());
            }

            if(asJsonArray.get(j).isJsonNull()){
                dailyUnrestorationStockData.setAmount( 0F );
                j++;
            }else {
                dailyUnrestorationStockData.setAmount(asJsonArray.get(j++).getAsFloat());
            }
            dailyUnrestorationStockDataList.add(dailyUnrestorationStockData);
        }
        return dailyUnrestorationStockDataList;
    }

    //获取某个交易日所有票的未复权数据
    @Override
    public List<DailyUnrestorationStockData> getTradeDayAllUnRestorationData(String tradeDate){
        Map<String,String> params = new HashMap<>();
        params.put("trade_date",tradeDate);
        String fields = "trade_date,ts_code,open,close,high,low,pre_close,change,vol,amount";
        JsonArray items = readDataItemsFromTUShare("daily", params, fields);

        List<DailyUnrestorationStockData> resultList = new ArrayList<>();
        for(int i = 0; i < items.size(); i++){
            int j = 0;
            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            DailyUnrestorationStockData dailyUnrestorationStockData = new DailyUnrestorationStockData();
            dailyUnrestorationStockData.setStockId(asJsonArray.get(j++).getAsString());
            dailyUnrestorationStockData.setStatDate(asJsonArray.get(j++).getAsString());
            dailyUnrestorationStockData.setOpen(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setHigh(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setLow(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setClose(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setPreClose(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setChange(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setVol(asJsonArray.get(j++).getAsFloat());
            dailyUnrestorationStockData.setAmount(asJsonArray.get(j++).getAsFloat());

            resultList.add(dailyUnrestorationStockData);
        }



        return resultList;
    }

    //获取一只票某个交易日的未复权数据
    @Override
    public DailyUnrestorationStockData getTradeDayUnRestorationData(String tsStockCode, String tradeDate){
        Map<String,String> params = new HashMap<>();
        params.put("ts_code", tsStockCode);
        params.put("start_date",tradeDate);
        params.put("end_date",tradeDate);
        String fields = "trade_date,ts_code,open,close,high,low,pre_close,change,vol,amount";
        JsonArray items = readDataItemsFromTUShare("daily", params, fields);

        int j = 0;
        JsonArray asJsonArray = items.get(0).getAsJsonArray();
        DailyUnrestorationStockData dailyUnrestorationStockData = new DailyUnrestorationStockData();
        dailyUnrestorationStockData.setStockId(asJsonArray.get(j++).getAsString());
        dailyUnrestorationStockData.setStatDate(asJsonArray.get(j++).getAsString());
        dailyUnrestorationStockData.setOpen(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setHigh(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setLow(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setClose(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setPreClose(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setChange(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setVol(asJsonArray.get(j++).getAsFloat());
        dailyUnrestorationStockData.setAmount(asJsonArray.get(j++).getAsFloat());


        return dailyUnrestorationStockData;
    }

    //获取某个交易日所有票的复权因子
    @Override
    public List<StockAdjFactor> getTradeDayAllStockAdjFactor(String tradeDate){
        Map<String,String> params = new HashMap<>();
        params.put("trade_date", tradeDate);
        JsonArray items = readDataItemsFromTUShare("adj_factor", params, "");
        List<StockAdjFactor> stockAdjFactorList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            int j = 0;
            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            StockAdjFactor stockAdjFactor = new StockAdjFactor();
            stockAdjFactor.setTsStockCode(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setTradeDate(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setAdjFactor(asJsonArray.get(j++).getAsFloat());

            stockAdjFactorList.add(stockAdjFactor);
        }
        return stockAdjFactorList;
    }

    //获取一只票上市第一天至统计日每个交易日的复权因子
    @Override
    public List<StockAdjFactor> getAdjFactorList(String tsStockCode, String statDate){
        Map<String,String> params = new HashMap<>();
        params.put("ts_code", tsStockCode);
        params.put("end_date",statDate);
        JsonArray items = readDataItemsFromTUShare("adj_factor", params, "");
        List<StockAdjFactor> stockAdjFactorList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            int j = 0;
            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            StockAdjFactor stockAdjFactor = new StockAdjFactor();
            stockAdjFactor.setTsStockCode(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setTradeDate(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setAdjFactor(asJsonArray.get(j++).getAsFloat());

            stockAdjFactorList.add(stockAdjFactor);
        }
        return stockAdjFactorList;
    }

    //获取一只票某个交易日范围的复权因子
    @Override
    public List<StockAdjFactor> getAdjFactorList(String tsStockCode, String startTradeDay,String endTradeDay){
        Map<String,String> params = new HashMap<>();
        params.put("ts_code", tsStockCode);
        params.put("start_date",startTradeDay);
        params.put("end_date",endTradeDay);
        JsonArray items = readDataItemsFromTUShare("adj_factor", params, "");
        List<StockAdjFactor> stockAdjFactorList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            int j = 0;
            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            StockAdjFactor stockAdjFactor = new StockAdjFactor();
            stockAdjFactor.setTsStockCode(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setTradeDate(asJsonArray.get(j++).getAsString());
            stockAdjFactor.setAdjFactor(asJsonArray.get(j++).getAsFloat());

            stockAdjFactorList.add(stockAdjFactor);
        }
        stockAdjFactorList.sort(new Comparator<StockAdjFactor>() {
            @SneakyThrows
            @Override
            public int compare(StockAdjFactor o1, StockAdjFactor o2) {
                return ToolUtil.strDateDecrement(o1.getTradeDate(),o2.getTradeDate()) ;
            }
        });
        return stockAdjFactorList;
    }

    //获取股票编码
    @Override
    public List<StockCode> getAllStockCodeList(){
        Map<String,String> params = new HashMap<>();
        params.put("list_status","L");
        String fields = "ts_code,symbol,name,list_date,industry,market,list_status";
        JsonArray items = readDataItemsFromTUShare("stock_basic", params, fields);

        List<StockCode> stockCodeList = new ArrayList<>(550);
        for (int i = 0; i < items.size(); i++) {
            JsonArray asJsonArray = items.get(i).getAsJsonArray();

            StockCode stockCode = new StockCode();
            stockCode.setTsStockCode(asJsonArray.get(0).getAsString());
            stockCode.setStockId(asJsonArray.get(1).getAsString());
            stockCode.setStockName(asJsonArray.get(2).getAsString());
            stockCode.setIndustry(asJsonArray.get(3).isJsonNull() ? "null" : asJsonArray.get(3).getAsString());
            stockCode.setMarket(asJsonArray.get(4).getAsString());
            stockCode.setListStatus(asJsonArray.get(5).getAsString());
            stockCode.setListDate(asJsonArray.get(6).getAsString());

            stockCodeList.add(stockCode);

        }

        return stockCodeList;
    }

    //获取直到明年之前的所有交易日
    @Override
    public List<TradeDay> getBeforeNextYearAllTradeDayList(String nextYear){
        Map<String,String> params = new HashMap<>();
        params.put("exchange","");
        params.put("end_date",nextYear);
        params.put("is_open","1");
        //4.返回的字段列表
        String fields = "cal_date";
        JsonArray items = readDataItemsFromTUShare("trade_cal", params, fields);
        List<TradeDay> tradeDayList = new ArrayList<>(550);

        for (int i = 0; i < items.size(); i++) {

            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            TradeDay tradeDay = new TradeDay();
            JsonPrimitive asJsonPrimitive = asJsonArray.get(0).getAsJsonPrimitive();
            tradeDay.setTradeDay(asJsonPrimitive.getAsString());
            tradeDayList.add(tradeDay);
        }
        return tradeDayList;
    }
}
