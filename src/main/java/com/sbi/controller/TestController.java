package com.sbi.controller;

import com.sbi.dao.StockCodeDao;
import com.sbi.dao.TradeDayDao;
import com.sbi.model.StockCode;
import com.sbi.model.TradeDay;
import com.sbi.service.GeneraterService;
import com.sbi.service.ReadDataFromTUShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
public class TestController {
    @Autowired
    private TradeDayDao tradeDayDao;

    @Autowired
    private StockCodeDao stockCodeDao;
    @Autowired
    ReadDataFromTUShareService readDataFromTUShareService;
    @Autowired
    private GeneraterService generaterService;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public ResponseEntity<Integer> test (){

//        List<StockCode> list = stockCodeDao.getAllStockTsCode("19910404");

        return ResponseEntity.ok(1);
    }
    @RequestMapping(value = "test2", method = RequestMethod.GET)
    public ResponseEntity<Integer> test2 () throws ParseException {


//        generaterService.genAllStockCode();
//        generaterService.genFutureYearTradeDay();
//        generaterService.genHistoryUnrestorationData("20210721");
//        generaterService.genHistoryBeforeRestorationData();
//        generaterService.updateHistoryBeforeRestorationKDJData();
//        generaterService.updateHistoryBeforeRestorationAverageData();
        generaterService.syncDailyAllStockData("20210727","20210726");
        return ResponseEntity.ok(1);

    }



}
