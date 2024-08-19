package com.exchange.app.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stock.service.StockInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

class StockInfoServiceTest
{

    private static final double price = 150.0;

    private static final String description = "Apple Inc.";

    private static final String stockName = "AAPL";

    @Mock
    private StockInfoRepository stockInfoRepository;

    @InjectMocks
    private StockInfoService stockInfoService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStock_WhenStockDoesNotExist_ShouldCreateStock() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(stockName).description(description)
                .currentPrice(new BigDecimal(price)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        stockInfoService.createStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    void createStock_WhenStockAlreadyExists_ShouldThrowEntityAlreadyExistException() throws JsonProcessingException
    {
        // Arrange
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(stockName).description(description)
                .currentPrice(new BigDecimal(150.0)).build();
        StockInfo stockInfo = StockInfo.builder().name(stockName).stockID(1L).build();

        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.of(stockInfo));
        assertThatThrownBy(() -> stockInfoService.createStock(stockInfoDTO))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessageContaining("There is already exist stock : AAPL");
    }

    @Test
    void createStock_ShouldLogAppropriateMessages() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(stockName).description(description)
                .currentPrice(new BigDecimal(150.0)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        stockInfoService.createStock(stockInfoDTO);
        verify(objectMapper, times(1)).writeValueAsString(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    void deleteStock_WhenStockExists_ShouldDeleteStock()
    {
        StockInfo stockInfo = StockInfo.builder().name(stockName).stockID(1L).build();

        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));
        stockInfoService.deleteStock(stockName);
        verify(stockInfoRepository, times(1)).deleteById(stockInfo.getStockID());
    }

    @Test
    void deleteStock_WhenStockDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stockInfoService.deleteStock(stockName))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("There is no record for stock : AAPL");
    }

    @Test
    void updateStock_WhenStockExists_ShouldUpdateStock() throws JsonProcessingException
    {

        StockInfo stockInfo = StockInfo.builder().name(stockName).stockID(1L).build();
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(stockName).description(description)
                .currentPrice(new BigDecimal(150.0)).build();
        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));
        stockInfoService.updateStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(stockInfo);
    }

    @Test
    void updateStock_WhenStockDoesNotExist_ShouldThrowEntityNotFoundException() throws JsonProcessingException
    {
        StockInfoDTO stockInfoDTO = StockInfoDTO.builder().name(stockName).description(description)
                .currentPrice(new BigDecimal(150.0)).build();
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stockInfoService.updateStock(stockInfoDTO)).isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("There is no record for stock : AAPL");
    }

}
