package com.exchange.app.stockexchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.exchange.app.common.comparator.StockIdComparator;
import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stockexchange.model.dto.StockExchangeInfoDTO;
import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;
import com.exchange.app.stockexchange.repository.StockExchangeInfoRepository;

import jakarta.persistence.EntityNotFoundException;
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
        logger.info("Getting stock info for exchange:{}", exchangeName);
        Optional<StockExchangeInfo> stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName);
        if (stockExchangeInfo.isPresent())
        {
            return stockExchangeInfo.get();
        }
        else
        {
            logger.info("Exchange not found");
            throw new EntityNotFoundException("No Stock Information for Exchange : " + exchangeName);
        }
    }

    public void addStockToExchange(String exchangeName, StockExchangeInfoDTO stockExchangeInfoDTO, String stockName)
    {
        logger.info("Adding stock : {} to Exchange : {}", stockName, exchangeName);

        Optional<StockInfo> stockInfo = stockInfoRepository.findByName(stockName);
        if (!stockInfo.isPresent())
        {
            throw new EntityNotFoundException("Stock: " + stockName + " is not exist on the system.");
        }
        Optional<StockExchangeInfo> stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName);
        if (stockExchangeInfo.isPresent())
        {
            updateExchangeInfo(exchangeName, stockExchangeInfo.get(), stockInfo.get());
        }
        else
        {
            createNewExchangeInfo(stockExchangeInfoDTO, stockInfo);

        }
    }

    public void deleteStockFromExchange(String exchangeName, String stockName)
    {
        logger.info("Deleting stock : {} from Exchange : {}", stockName, exchangeName);

        Optional<StockExchangeInfo> stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName);
        if (stockExchangeInfo.isPresent())
        {
            StockExchangeInfo stockExchangeInfoEntity = stockExchangeInfo.get();
            List<StockInfo> stockList = stockExchangeInfoEntity.getStocks();
            Optional<StockInfo> stockInfo = stockInfoRepository.findByName(stockName);
            StockInfo stock = stockInfo.get();
            boolean contains = stockList.stream()
                    .anyMatch(item -> stockIdComparator.compare(item, stockInfo.get()) == 0);
            if (contains)
            {
                stockList.remove(stock);
                stockExchangeInfoEntity.toBuilder().stocks(stockList).build();
                stockExchangeInfoRepository.save(stockExchangeInfoEntity);
            }
            else
            {
                throw new EntityNotFoundException(
                        "Stock: " + stockName + " is not exist on the exchange : " + exchangeName);
            }
        }
        else
        {
            throw new EntityNotFoundException("No Exchange Information for Exchange : " + exchangeName);
        }
    }

    private void createNewExchangeInfo(StockExchangeInfoDTO stockExchangeInfoDTO, Optional<StockInfo> stockInfo)
    {
        List<StockInfo> listOfStocks = new ArrayList<>();
        listOfStocks.add(stockInfo.get());
        StockExchangeInfo newStockExchangeInfo = StockExchangeInfo.builder().name(stockExchangeInfoDTO.getName())
                .description(stockExchangeInfoDTO.getDescription()).stocks(listOfStocks).build();
        stockExchangeInfoRepository.save(newStockExchangeInfo);
    }

    private void updateExchangeInfo(String exchangeName, StockExchangeInfo stockExchangeInfo, StockInfo stockInfo)
    {
        logger.info("Updating stock : {} from Exchange : {}", stockInfo.getName(), exchangeName);

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
            throw new EntityAlreadyExistException(
                    "Stock: " + stockExchangeInfo.getName() + " is already exist on the exchange : " + exchangeName);
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
