package com.sbi.controller;

import com.sbi.model.TradeDay;
import com.sbi.service.GeneraterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/gen/")
public class GeneraterController {
    @Autowired
    GeneraterService generaterService;


    @RequestMapping(value = "stock_code", method = RequestMethod.GET)
    public void genStockCode (){
        generaterService.genAllStockCode();
    }


}
