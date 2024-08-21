package com.exchange.app.stockexchange.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.exchange.app.common.comparator.StockIdComparator;
import com.exchange.app.common.constants.Constants;
import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.common.error.EntityNotFoundException;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stockexchange.model.dto.StockExchangeInfoDTO;
import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;
import com.exchange.app.stockexchange.repository.StockExchangeInfoRepository;

import jakarta.transaction.Transactional;

@Service
public class StockExchangeInfoService
{

    private static final Logger logger = LoggerFactory.getLogger(StockExchangeInfoService.class);

    @Value("${app.liveRequiredNumber:5}")
    private int liveRequiredNumber;

    private StockExchangeInfoRepository stockExchangeInfoRepository;
    private StockInfoRepository stockInfoRepository;
    private StockIdComparator stockIdComparator;

    @Autowired
    public StockExchangeInfoService(StockExchangeInfoRepository stockExchangeInfoRepository,
            StockInfoRepository stockInfoRepository)
    {
        this.stockExchangeInfoRepository = stockExchangeInfoRepository;
        this.stockInfoRepository = stockInfoRepository;

    }

    @Transactional
    public StockExchangeInfo getStockExchangeInfo(String exchangeName)
    {
        return stockExchangeInfoRepository.findByName(exchangeName)
                .orElseThrow(() -> new EntityNotFoundException(Constants.NOT_EXIST_STOCK_FOR_EXCHANGE + exchangeName));
    }

    public void addStockToExchange(String exchangeName, StockExchangeInfoDTO stockExchangeInfoDTO, String stockName)
    {
        logger.debug(Constants.STEPIN);
        StockInfo stockInfo = stockInfoRepository.findByName(stockName)
                .orElseThrow(() -> new EntityNotFoundException(Constants.NOT_EXIST_STOCK));

        stockExchangeInfoRepository.findByName(exchangeName).ifPresentOrElse(
                exchangeInfo -> updateExchangeInfo(exchangeName, exchangeInfo, stockInfo),
                () -> createNewExchangeInfo(stockExchangeInfoDTO, stockInfo));
        logger.debug(Constants.STEPOUT);

    }

    public void deleteStockFromExchange(String exchangeName, String stockName)
    {
        logger.debug(Constants.STEPIN);

        StockExchangeInfo stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName)
                .orElseThrow(() -> new EntityNotFoundException(Constants.NO_EXCHANGE_INFORMATION + exchangeName));
        StockInfo stockInfo = stockInfoRepository.findByName(stockName).orElseThrow(
                () -> new EntityNotFoundException(Constants.STOCK_IS_NOT_EXIST_ON_THE_EXCHANGE + exchangeName));
        List<StockInfo> stockList = stockExchangeInfo.getStocks();
        if (stockList.stream().anyMatch(item -> stockIdComparator.compare(item, stockInfo) == 0))
        {
            stockList.removeIf(item -> stockIdComparator.compare(item, stockInfo) == 0);
            stockExchangeInfoRepository.save(stockExchangeInfo.toBuilder().stocks(stockList).build());
        }
        else
        {
            throw new EntityNotFoundException(Constants.STOCK_IS_NOT_EXIST_ON_THE_EXCHANGE + exchangeName);
        }

        logger.debug(Constants.STEPOUT);
    }

    private void createNewExchangeInfo(StockExchangeInfoDTO stockExchangeInfoDTO, StockInfo stockInfo)
    {
        List<StockInfo> listOfStocks = new ArrayList<>();
        listOfStocks.add(stockInfo);
        StockExchangeInfo newStockExchangeInfo = StockExchangeInfo.builder().name(stockExchangeInfoDTO.getName())
                .description(stockExchangeInfoDTO.getDescription()).stocks(listOfStocks).build();
        stockExchangeInfoRepository.save(newStockExchangeInfo);
    }

    private void updateExchangeInfo(String exchangeName, StockExchangeInfo stockExchangeInfo, StockInfo stockInfo)
    {
        logger.debug("Updating stock : {} from Exchange : {}", stockInfo.getName(), exchangeName);

        List<StockInfo> stockList = stockExchangeInfo.getStocks();
        boolean contains = stockList.stream().anyMatch(item -> stockIdComparator.compare(item, stockInfo) == 0);
        if (!contains)
        {
            stockList.add(stockInfo);
            checkLiveness(stockExchangeInfo, stockList);
            stockExchangeInfo.toBuilder().stocks(stockList).build();
            stockExchangeInfoRepository.save(stockExchangeInfo);
        }
        else
        {
            throw new EntityAlreadyExistException(Constants.STOCK_IS_ALREADY_EXIST_ON_THE_EXCHANGE + exchangeName);
        }
    }

    private void checkLiveness(StockExchangeInfo stockExchangeInfoEntity, List<StockInfo> stockList)
    {
        if (stockList.size() >= liveRequiredNumber)
        {
            stockExchangeInfoEntity.toBuilder().isLiveInMarket(true);
        }
        else
        {
            stockExchangeInfoEntity.toBuilder().isLiveInMarket(false);
        }
    }

}
