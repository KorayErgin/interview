package com.exchange.app.stockexchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<StockExchangeInfo> stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName);
        if (stockExchangeInfo.isPresent())
        {
            return stockExchangeInfo.get();
        }
        else
        {
            throw new EntityNotFoundException("No Stock Information for Exchange : " + exchangeName);
        }
    }

    public void addStockToExchange(String exchangeName, StockExchangeInfoDTO stockExchangeInfoDTO, String stockName)
    {
        Optional<StockInfo> stockInfo = stockInfoRepository.findByName(stockName);
        if (!stockInfo.isPresent())
        {
            throw new EntityNotFoundException("Stock: " + stockName + " is not exist on the system.");
        }
        Optional<StockExchangeInfo> stockExchangeInfo = stockExchangeInfoRepository.findByName(exchangeName);
        if (stockExchangeInfo.isPresent())
        {
            updateExchangeInfo(exchangeName, stockExchangeInfoDTO, stockInfo, stockExchangeInfo);
        }
        else
        {
            createNewExchangeInfo(stockExchangeInfoDTO, stockInfo);

        }
    }

    private void createNewExchangeInfo(StockExchangeInfoDTO stockExchangeInfoDTO, Optional<StockInfo> stockInfo)
    {
        StockExchangeInfo newStockExchangeInfo = new StockExchangeInfo();
        newStockExchangeInfo.setDescription(stockExchangeInfoDTO.getDescription());
        newStockExchangeInfo.setName(stockExchangeInfoDTO.getName());
        List<StockInfo> listOfStocks = new ArrayList<>();
        listOfStocks.add(stockInfo.get());
        newStockExchangeInfo.setStocks(listOfStocks);
        stockExchangeInfoRepository.save(newStockExchangeInfo);
    }

    private void updateExchangeInfo(String exchangeName, StockExchangeInfoDTO stockExchangeInfoDTO,
            Optional<StockInfo> stockInfo, Optional<StockExchangeInfo> stockExchangeInfo)
    {
        StockExchangeInfo stockExchangeInfoEntity = stockExchangeInfo.get();
        List<StockInfo> stockList = stockExchangeInfoEntity.getStocks();

        StockInfo activeStock = stockInfo.get();
        boolean contains = stockList.stream().anyMatch(item -> stockIdComparator.compare(item, activeStock) == 0);
        if (!contains)
        {
            stockList.add(activeStock);
            checkLiveness(stockExchangeInfoEntity, stockList);
            stockExchangeInfoEntity.setStocks(stockList);
            stockExchangeInfoRepository.save(stockExchangeInfoEntity);
        }
        else
        {
            throw new EntityAlreadyExistException(
                    "Stock: " + stockExchangeInfoDTO.getName() + " is already exist on the exchange : " + exchangeName);
        }
    }

    public void deleteStockFromExchange(String exchangeName, String stockName)
    {
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
                stockExchangeInfoEntity.setStocks(stockList);
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

    private void checkLiveness(StockExchangeInfo stockExchangeInfoEntity, List<StockInfo> stockList)
    {
        if (stockList.size() >= 5)
        {
            stockExchangeInfoEntity.setLiveInMarket(true);
        }
        else
        {
            stockExchangeInfoEntity.setLiveInMarket(false);
        }
    }

}
