package com.exchange.app.stock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.exchange.app.common.constants.Constants;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.service.StockInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping(value = "/api/v1/stock")
public class StockController
{
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    private StockInfoService stockInfoService;

    @Autowired
    public StockController(StockInfoService stockInfoService)
    {
        this.stockInfoService = stockInfoService;
    }

    @PostMapping
    public ResponseEntity<String> createStockInfo(@RequestBody StockInfoDTO stockInfoDTO) throws JsonProcessingException
    {
        stockInfoService.createStock(stockInfoDTO);
        return new ResponseEntity<>(Constants.STOCK_CREATED_SUCCESSFULLY, HttpStatus.CREATED);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateStockInfo(@RequestBody StockInfoDTO stockInfoDTO) throws JsonProcessingException
    {
        stockInfoService.updateStock(stockInfoDTO);
        return new ResponseEntity<>(Constants.STOCK_UPDATED_SUCCESSFULLY, HttpStatus.OK);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteStockInfo(@RequestBody StockInfoDTO stockInfoDTO)
    {
        stockInfoService.deleteStock(stockInfoDTO.getName());
        return new ResponseEntity<>(Constants.STOCK_DELETED_SUCCESSFULLY, HttpStatus.OK);
    }
}
