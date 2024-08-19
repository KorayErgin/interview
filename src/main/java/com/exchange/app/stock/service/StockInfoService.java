package com.exchange.app.stock.service;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class StockInfoService
{
    private static final Logger logger = LoggerFactory.getLogger(StockInfoService.class);

    private StockInfoRepository stockInfoRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StockInfoService(StockInfoRepository stockInfoRepository)
    {
        this.stockInfoRepository = stockInfoRepository;
    }

    @Transactional
    public void createStock(StockInfoDTO stockInfoDTO) throws JsonProcessingException
    {
        logger.info("Creating a stock");
        logger.debug("Input :{}", objectMapper.writeValueAsString(stockInfoDTO));

        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockInfoDTO.getName());
        if (!stockInfoEntity.isPresent())
        {
            stockInfoRepository.save(mapFromStockInfoDTOToStockInfoEntity(new StockInfo(), stockInfoDTO));
            logger.info("Stock succesfully created.");

        }
        else
        {
            logger.info("Stock already exist.");
            throw new EntityAlreadyExistException("There is already exist stock : " + stockInfoDTO.getName());
        }
    }

    @Transactional
    public void deleteStock(String stockName)
    {
        logger.info("Deleting a stock");
        logger.debug("Input :{}", stockName);

        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockName);
        if (stockInfoEntity.isPresent())
        {
            stockInfoRepository.deleteById(stockInfoEntity.get().getStockID());
            logger.info("Stock successfully deleted.");
        }
        else
        {
            logger.info("Stock not found.");
            throw new EntityNotFoundException("There is no record for stock : " + stockName);
        }
    }

    @Transactional
    public void updateStock(StockInfoDTO stockInfoDTO) throws JsonProcessingException
    {
        logger.info("Updating a stock");
        logger.debug("Input :{}", objectMapper.writeValueAsString(stockInfoDTO));
        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockInfoDTO.getName());
        if (stockInfoEntity.isPresent())
        {
            StockInfo stockInfo = stockInfoEntity.get();
            stockInfo = mapFromStockInfoDTOToStockInfoEntity(stockInfo, stockInfoDTO);
            stockInfoRepository.save(stockInfo);
            logger.info("Stock succesfully updated.");
        }
        else
        {
            logger.info("Stock not found.");
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
