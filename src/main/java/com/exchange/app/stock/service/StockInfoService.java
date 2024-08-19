package com.exchange.app.stock.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;

import common.error.EntityAlreadyExistException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class StockInfoService
{
    private StockInfoRepository stockInfoRepository;

    @Autowired
    public StockInfoService(StockInfoRepository stockInfoRepository)
    {
        this.stockInfoRepository = stockInfoRepository;
    }

    @Transactional
    public void createStock(StockInfoDTO stockInfoDTO)
    {
        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockInfoDTO.getName());
        if (!stockInfoEntity.isPresent())
        {
            stockInfoRepository.save(mapFromStockInfoDTOToStockInfoEntity(new StockInfo(), stockInfoDTO));
        }
        else
        {
            throw new EntityAlreadyExistException("There is already exist stock : " + stockInfoDTO.getName());
        }
    }

    @Transactional
    public void deleteStock(String stockName)
    {
        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockName);
        if (stockInfoEntity.isPresent())
        {
            stockInfoRepository.deleteById(stockInfoEntity.get().getStockID());
        }
        else
        {
            throw new EntityNotFoundException("There is no record for stock : " + stockName);
        }
    }

    @Transactional
    public void updateStock(StockInfoDTO stockInfoDTO)
    {
        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockInfoDTO.getName());
        if (stockInfoEntity.isPresent())
        {
            StockInfo stockInfo = stockInfoEntity.get();
            stockInfo = mapFromStockInfoDTOToStockInfoEntity(stockInfo, stockInfoDTO);
            stockInfoRepository.save(stockInfo);
        }
        else
        {
            throw new EntityNotFoundException("There is no record for stock : " + stockInfoDTO.getName());
        }
    }

    private StockInfo mapFromStockInfoDTOToStockInfoEntity(StockInfo stockInfo, StockInfoDTO stockInfoDTO)
    {
        stockInfo.setName(stockInfoDTO.getName());
        stockInfo.setDescription(stockInfoDTO.getDescription());
        stockInfo.setCurrentPrice(stockInfoDTO.getCurrentPrice());
        stockInfo.setLastUpdateTime(new Date());
        return stockInfo;
    }
}
