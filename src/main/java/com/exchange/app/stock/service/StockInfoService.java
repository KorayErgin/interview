package com.exchange.app.stock.service;

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
        // logger.debug("Input :{}",
        // objectMapper.writeValueAsString(stockInfoDTO));

        Optional<StockInfo> stockInfoEntity = stockInfoRepository.findByName(stockInfoDTO.getName());
        if (!stockInfoEntity.isPresent())
        {
            StockInfo newStockInfo = StockInfo.builder().name(stockInfoDTO.getName())
                    .description(stockInfoDTO.getDescription()).currentPrice(stockInfoDTO.getCurrentPrice()).build();
            stockInfoRepository.save(newStockInfo);
            logger.info("Stock succesfully created.");

        }
        else
        {
            logger.info("Stock {} already exist.", stockInfoDTO.getName());
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
            logger.info("Stock {} not found.", stockName);
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
            StockInfo stock = stockInfoEntity.get().toBuilder().name(stockInfoDTO.getName())
                    .description(stockInfoDTO.getDescription()).currentPrice(stockInfoDTO.getCurrentPrice()).build();
            stockInfoRepository.save(stock);
            logger.info("Stock succesfully updated.");
        }
        else
        {
            logger.info("Stock {} not found.", stockInfoDTO.getName());
            throw new EntityNotFoundException("There is no record for stock : " + stockInfoDTO.getName());
        }
    }

}
