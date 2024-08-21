package com.exchange.app.stock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exchange.app.common.constants.Constants;
import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.common.error.EntityNotFoundException;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        logger.debug(Constants.STEPIN);
        stockInfoRepository.findByName(stockInfoDTO.getName()).ifPresentOrElse(existingStock ->
        {
            logger.error(Constants.ALREADY_EXIST_STOCK + stockInfoDTO.getName());
            throw new EntityAlreadyExistException(Constants.ALREADY_EXIST_STOCK + stockInfoDTO.getName());
        }, () ->
        {
            StockInfo newStockInfo = StockInfo.builder().name(stockInfoDTO.getName())
                    .description(stockInfoDTO.getDescription()).currentPrice(stockInfoDTO.getCurrentPrice()).build();
            stockInfoRepository.save(newStockInfo);
            logger.info(Constants.STOCK_CREATED_SUCCESSFULLY);
        });
        logger.debug(Constants.STEPOUT);
    }

    @Transactional
    public void deleteStock(String stockName)
    {
        logger.debug(Constants.STEPIN);
        logger.debug(Constants.INPUT, stockName);
        StockInfo stockInfoEntity = stockInfoRepository.findByName(stockName)
                .orElseThrow(() -> new EntityNotFoundException(Constants.NOT_EXIST_STOCK + stockName));
        stockInfoRepository.deleteById(stockInfoEntity.getStockID());
        logger.debug(Constants.STEPOUT);
    }

    @Transactional
    public void updateStock(StockInfoDTO stockInfoDTO) throws JsonProcessingException
    {
        logger.debug(Constants.STEPIN);
        logger.debug(Constants.INPUT, objectMapper.writeValueAsString(stockInfoDTO));
        StockInfo updatedStockInfo = stockInfoRepository.findByName(stockInfoDTO.getName())
                .map(stock -> stock.toBuilder().name(stockInfoDTO.getName()).description(stockInfoDTO.getDescription())
                        .currentPrice(stockInfoDTO.getCurrentPrice()).build())
                .orElseThrow(() -> new EntityNotFoundException(Constants.NOT_EXIST_STOCK + stockInfoDTO.getName()));
        stockInfoRepository.save(updatedStockInfo);
        logger.debug(Constants.STEPOUT);
    }

}
