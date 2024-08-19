import com.exchange.app.stockexchange.service.StockExchangeInfoService;

package com.exchange.app.stock.service;

import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.dto.StockInfoDTO;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockInfoServiceTest {

    @Mock
    private StockInfoRepository stockInfoRepository;

    @InjectMocks
    private StockInfoService stockInfoService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStock_WhenStockDoesNotExist_ShouldCreateStock() throws JsonProcessingException {
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 150.0);
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        stockInfoService.createStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    void createStock_WhenStockAlreadyExists_ShouldThrowEntityAlreadyExistException() throws JsonProcessingException {
        // Arrange
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 150.0);
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.of(new StockInfo()));
        assertThatThrownBy(() -> stockInfoService.createStock(stockInfoDTO))
                .isInstanceOf(EntityAlreadyExistException.class)
                .hasMessageContaining("There is already exist stock : AAPL");
    }

    @Test
    void createStock_ShouldLogAppropriateMessages() throws JsonProcessingException {
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 150.0);
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        stockInfoService.createStock(stockInfoDTO);
        verify(objectMapper, times(1)).writeValueAsString(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(any(StockInfo.class));
    }

    @Test
    void deleteStock_WhenStockExists_ShouldDeleteStock() {
        // Arrange
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockID(1L);
        stockInfo.setName("AAPL");
        when(stockInfoRepository.findByName("AAPL")).thenReturn(Optional.of(stockInfo));
        stockInfoService.deleteStock("AAPL");
        verify(stockInfoRepository, times(1)).deleteById(stockInfo.getStockID());
    }

    @Test
    void deleteStock_WhenStockDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(stockInfoRepository.findByName("AAPL")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stockInfoService.deleteStock("AAPL"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("There is no record for stock : AAPL");
    }

    @Test
    void updateStock_WhenStockExists_ShouldUpdateStock() throws JsonProcessingException {
        // Arrange
        StockInfo stockInfo = new StockInfo();
        stockInfo.setStockID(1L);
        stockInfo.setName("AAPL");
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 155.0);
        when(stockInfoRepository.findByName("AAPL")).thenReturn(Optional.of(stockInfo));
        stockInfoService.updateStock(stockInfoDTO);
        verify(stockInfoRepository, times(1)).save(stockInfo);
    }

    @Test
    void updateStock_WhenStockDoesNotExist_ShouldThrowEntityNotFoundException() throws JsonProcessingException {
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 155.0);
        when(stockInfoRepository.findByName(stockInfoDTO.getName())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> stockInfoService.updateStock(stockInfoDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("There is no record for stock : AAPL");
    }

    @Test
    void mapFromStockInfoDTOToStockInfoEntity_ShouldMapCorrectly() {
        StockInfoDTO stockInfoDTO = new StockInfoDTO("AAPL", "Apple Inc.", 150.0);
        StockInfo stockInfo = new StockInfo();

        StockInfo mappedStockInfo = stockInfoService.mapFromStockInfoDTOToStockInfoEntity(stockInfo, stockInfoDTO);
        assertThat(mappedStockInfo.getName()).isEqualTo("AAPL");
        assertThat(mappedStockInfo.getDescription()).isEqualTo("Apple Inc.");
        assertThat(mappedStockInfo.getCurrentPrice()).isEqualTo(150.0);
        assertThat(mappedStockInfo.getLastUpdateTime()).isNotNull();
    }
}
