package com.exchange.app.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;

import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stock.service.StockInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

public class StockInfoServiceTest
{

    private static final String UPDATE_PRICE = "160.00";
    private static final String PRICE = "150.00";
    private static final String STOCK_DESCRIPTION = "Apple Inc.";
    private static final String STOCK_NAME = "AAPL";

    @Mock
    private StockInfoRepository stockInfoRepository;

    @InjectMocks
    private StockInfoService stockInfoService;

    @BeforeMethod
    public void setUp()
    {
        MockitoAnnotations.openMocks(this);
        stockInfoService = new StockInfoService(stockInfoRepository);

    }

    @Test
    public void createStock_NewStock_Success() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(STOCK_NAME).description(STOCK_DESCRIPTION)
                .currentPrice(new java.math.BigDecimal(PRICE)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        stockInfoService.createStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    public void createStock_ExistingStock_ThrowsEntityAlreadyExistException()
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(STOCK_NAME).description(STOCK_DESCRIPTION)
                .currentPrice(new java.math.BigDecimal(PRICE)).build();
        StockInfo existingStock = StockInfo.builder().name(STOCK_NAME).description(STOCK_DESCRIPTION)
                .currentPrice(new java.math.BigDecimal(PRICE)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.of(existingStock));
        assertThrows(EntityAlreadyExistException.class, () -> stockInfoService.createStock(stockInfoDTO));
        verify(stockInfoRepository, times(0)).save(any(StockInfo.class));
    }

    @Test
    public void deleteStock_ExistingStock_Success()
    {
        StockInfo existingStock = StockInfo.builder().name(STOCK_NAME).build();
        when(stockInfoRepository.findByName(STOCK_NAME)).thenReturn(Optional.of(existingStock));
        stockInfoService.deleteStock(STOCK_NAME);
        verify(stockInfoRepository, times(1)).deleteById(existingStock.getStockID());
    }

    @Test
    public void deleteStock_NonExistingStock_ThrowsEntityNotFoundException()
    {
        String stockName = STOCK_NAME;
        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stockInfoService.deleteStock(stockName));
        verify(stockInfoRepository, times(0)).deleteById(any(Long.class));
    }

    @Test
    public void updateStock_ExistingStock_Success() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(STOCK_NAME).description("Updated Apple Inc.")
                .currentPrice(new java.math.BigDecimal(UPDATE_PRICE)).build();

        StockInfo existingStock = StockInfo.builder().name(STOCK_NAME).description(STOCK_DESCRIPTION)
                .currentPrice(new java.math.BigDecimal(PRICE)).build();

        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.of(existingStock));
        stockInfoService.updateStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    public void updateStock_NonExistingStock_ThrowsEntityNotFoundException() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(STOCK_NAME).description(STOCK_DESCRIPTION)
                .currentPrice(new java.math.BigDecimal(UPDATE_PRICE)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> stockInfoService.updateStock(stockInfoDTO));
        verify(stockInfoRepository, times(0)).save(any(StockInfo.class));
    }
}
