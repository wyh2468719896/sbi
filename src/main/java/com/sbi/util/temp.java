package com.sbi.util;


import com.google.gson.*;
import com.sbi.model.StockCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class temp {
    public static void main(String[] args) {

        testGetStockCode();
//        testGetDailyStockBasicData("20210711","20210718");
//        testGetTradeDayList("20190413","20210718");

    }

    public static void testGetStockCode(){
        Gson gson = new Gson();
        String queryStockCodeUrl = "http://api.tushare.pro";
        Map<String,Object> paramMap = new HashMap<>();
        //1.接口名
        paramMap.put("api_name","stock_basic");
        //2.token
        paramMap.put("token","eb1af3bb9e2280783dcde4930237691c3331b1bc7a229519e93f5f80");
        //3.接口的输入参数
        Map<String,String> params = new HashMap<>();
        params.put("list_status","L");
        paramMap.put("params",params);
        //4.返回的字段列表
        paramMap.put("fields","ts_code,symbol,name");

        String resultJson = HttpClientUtil.doPostJson(queryStockCodeUrl, gson.toJson(paramMap));
        System.out.println(resultJson);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultJson);
        JsonObject root = element.getAsJsonObject();
        JsonArray items = root.getAsJsonObject("data").getAsJsonArray("items");



        List<StockCode> stockCodeList = new ArrayList<>(550);




        for (int i = 0; i < items.size(); i++) {

            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            StockCode stockCode = new StockCode();
            stockCode.setTsStockCode(asJsonArray.get(0).getAsString());
            stockCode.setStockId(asJsonArray.get(1).getAsString());
            stockCode.setStockName(asJsonArray.get(2).getAsString());


            stockCodeList.add(stockCode);
            System.out.println();
        }


    }


    public static void testGetDailyStockBasicData(String startDate,String endDate){
        Gson gson = new Gson();
        String queryStockCodeUrl = "http://api.tushare.pro";
        Map<String,Object> paramMap = new HashMap<>();
        //1.接口名
        paramMap.put("api_name","daily");
        //2.token
        paramMap.put("token","eb1af3bb9e2280783dcde4930237691c3331b1bc7a229519e93f5f80");
        //3.接口的输入参数
        Map<String,String> params = new HashMap<>();
        params.put("ts_code","000333.SZ");
        params.put("start_date",startDate);
        params.put("end_date",endDate);
        paramMap.put("params",params);
        //4.返回的字段列表
        paramMap.put("fields","");

        String resultJson = HttpClientUtil.doPostJson(queryStockCodeUrl, gson.toJson(paramMap));
        System.out.println(resultJson);
    }

    public static void testGetTradeDayList(String startDate,String endDate){
        Gson gson = new Gson();
        String queryStockCodeUrl = "http://api.tushare.pro";
        Map<String,Object> paramMap = new HashMap<>();
        //1.接口名
        paramMap.put("api_name","trade_cal");
        //2.token
        paramMap.put("token","eb1af3bb9e2280783dcde4930237691c3331b1bc7a229519e93f5f80");
        //3.接口的输入参数
        Map<String,String> params = new HashMap<>();
        params.put("exchange","");
        params.put("start_date",startDate);
        params.put("end_date",endDate);
        params.put("is_open","1");
        paramMap.put("params",params);
        //4.返回的字段列表
        paramMap.put("fields","cal_date");

        String resultJson = HttpClientUtil.doPostJson(queryStockCodeUrl, gson.toJson(paramMap));
        System.out.println(resultJson);
//        Map map = JsonUtil.jsonStrToObject(resultJson, Map.class);

        //提取响应json中的data.item值
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultJson);
        JsonObject root = element.getAsJsonObject();
        JsonArray items = root.getAsJsonObject("data").getAsJsonArray("items");



        List<String> tradeDayList = new ArrayList<>(550);




        for (int i = 0; i < items.size(); i++) {

            JsonArray asJsonArray = items.get(i).getAsJsonArray();
            JsonPrimitive asJsonPrimitive = asJsonArray.get(0).getAsJsonPrimitive();
            tradeDayList.add(asJsonPrimitive.getAsString());
            System.out.println(asJsonPrimitive.getAsString());
        }
//        System.out.println("hello");




    }
}
