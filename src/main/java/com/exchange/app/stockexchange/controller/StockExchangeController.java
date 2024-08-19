package com.exchange.app.stockexchange.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.exchange.app.stockexchange.model.dto.StockExchangeInfoDTO;
import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;
import com.exchange.app.stockexchange.service.StockExchangeInfoService;

@RestController
@RequestMapping(value = "/api/v1/stock-exchange")
public class StockExchangeController
{

    private StockExchangeInfoService stockExchangeInfoService;

    @Autowired
    public StockExchangeController(StockExchangeInfoService stockExchangeInfoService)
    {
        this.stockExchangeInfoService = stockExchangeInfoService;
    }

    @GetMapping(value = "/{exchangeName}", produces = APPLICATION_JSON_VALUE)
    public StockExchangeInfo getAllStocksOfExchange(@PathVariable("exchangeName") final String exchangeName)
    {
        return stockExchangeInfoService.getStockExchangeInfo(exchangeName);
    }

    @PostMapping(value = "/{exchangeName}/{stockName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addExchangeInfo(@PathVariable("exchangeName") final String exchangeName,
            @PathVariable("stockName") final String stockName,
            @RequestBody StockExchangeInfoDTO stockExchangeInfoDTO)
    {
        stockExchangeInfoService.addStockToExchange(exchangeName, stockExchangeInfoDTO,stockName);

    }

    @DeleteMapping(value = "/{exchangeName}/{stockName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStockFromExchange(@PathVariable("exchangeName") final String exchangeName,
            @PathVariable("stockName") final String stockName)
    {
        stockExchangeInfoService.deleteStockFromExchange(exchangeName, stockName);
    }
}
