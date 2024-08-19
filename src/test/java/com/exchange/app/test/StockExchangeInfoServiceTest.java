package com.exchange.app.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.exchange.app.common.comparator.StockIdComparator;
import com.exchange.app.common.error.EntityAlreadyExistException;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stockexchange.model.dto.StockExchangeInfoDTO;
import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;
import com.exchange.app.stockexchange.repository.StockExchangeInfoRepository;
import com.exchange.app.stockexchange.service.StockExchangeInfoService;

import jakarta.persistence.EntityNotFoundException;

class StockExchangeInfoServiceTest
{

    private static final String exchangeDescrpiption = "NASDAQ Exchange";
    private static final String exchangeName = "NASDAQ";
    private static final String stockName = "AAPL";
    @Mock
    private StockExchangeInfoRepository stockExchangeInfoRepository;

    @Mock
    private StockInfoRepository stockInfoRepository;

    @Mock
    private StockIdComparator stockIdComparator;

    @InjectMocks
    private StockExchangeInfoService stockExchangeInfoService;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStockExchangeInfo_ExistingExchange_ReturnsStockExchangeInfo()
    {
        StockExchangeInfo stockExchangeInfo = StockExchangeInfo.builder().name(exchangeName)
                .description(exchangeDescrpiption).build();
        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.of(stockExchangeInfo));

        StockExchangeInfo result = stockExchangeInfoService.getStockExchangeInfo(exchangeName);

        assertNotNull(result);
        assertEquals(exchangeName, result.getName());
        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
    }

    @Test
    void getStockExchangeInfo_NonExistingExchange_ThrowsEntityNotFoundException()
    {
        String exchangeName = "NON_EXISTENT_EXCHANGE";
        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stockExchangeInfoService.getStockExchangeInfo(exchangeName));
        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
    }

    @Test
    void addStockToExchange_NewStockInNewExchange_CreatesNewExchangeInfo()
    {

        StockExchangeInfoDTO dto = StockExchangeInfoDTO.builder().name(exchangeName).description(exchangeDescrpiption)
                .build();

        StockInfo stockInfo = StockInfo.builder().name(stockName).build();

        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));
        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.empty());

        stockExchangeInfoService.addStockToExchange(exchangeName, dto, stockName);

        verify(stockInfoRepository, times(1)).findByName(stockName);
        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
        verify(stockExchangeInfoRepository, times(1)).save(any(StockExchangeInfo.class));
    }

    @Test
    void addStockToExchange_ExistingStockInExistingExchange_ThrowsEntityAlreadyExistException()
    {

        StockExchangeInfoDTO dto = StockExchangeInfoDTO.builder().name(exchangeName).description(exchangeDescrpiption)
                .build();
        StockInfo stockInfo = StockInfo.builder().name(stockName).build();
        StockExchangeInfo stockExchangeInfo = StockExchangeInfo.builder().name(exchangeName).stocks(List.of(stockInfo))
                .build();

        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));
        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.of(stockExchangeInfo));
        when(stockIdComparator.compare(any(StockInfo.class), any(StockInfo.class))).thenReturn(0);

        assertThrows(EntityAlreadyExistException.class,
                () -> stockExchangeInfoService.addStockToExchange(exchangeName, dto, stockName));

        verify(stockInfoRepository, times(1)).findByName(stockName);
        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
        verify(stockExchangeInfoRepository, never()).save(any(StockExchangeInfo.class));
    }

    @Test
    void deleteStockFromExchange_ExistingStockInExchange_RemovesStock()
    {
        StockInfo stockInfo = StockInfo.builder().name(stockName).build();
        List<StockInfo> stocks = new ArrayList<>();
        stocks.add(stockInfo);

        StockExchangeInfo stockExchangeInfo = StockExchangeInfo.builder().name(exchangeName).stocks(stocks).build();

        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.of(stockExchangeInfo));
        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));
        when(stockIdComparator.compare(any(StockInfo.class), any(StockInfo.class))).thenReturn(0);

        stockExchangeInfoService.deleteStockFromExchange(exchangeName, stockName);

        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
        verify(stockInfoRepository, times(1)).findByName(stockName);
        verify(stockExchangeInfoRepository, times(1)).save(stockExchangeInfo);
        assertFalse(stockExchangeInfo.getStocks().contains(stockInfo));
    }

    @Test
    void deleteStockFromExchange_NonExistingStockInExchange_ThrowsEntityNotFoundException()
    {

        StockInfo stockInfo = StockInfo.builder().name(stockName).build();
        StockExchangeInfo stockExchangeInfo = StockExchangeInfo.builder().name(exchangeName).stocks(new ArrayList<>())
                .build();

        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.of(stockExchangeInfo));
        when(stockInfoRepository.findByName(stockName)).thenReturn(Optional.of(stockInfo));

        assertThrows(EntityNotFoundException.class,
                () -> stockExchangeInfoService.deleteStockFromExchange(exchangeName, stockName));

        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
        verify(stockInfoRepository, times(1)).findByName(stockName);
        verify(stockExchangeInfoRepository, never()).save(stockExchangeInfo);
    }

    @Test
    void deleteStockFromExchange_NonExistingExchange_ThrowsEntityNotFoundException()
    {
        String exchangeName = "NON_EXISTENT_EXCHANGE";

        when(stockExchangeInfoRepository.findByName(exchangeName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> stockExchangeInfoService.deleteStockFromExchange(exchangeName, stockName));

        verify(stockExchangeInfoRepository, times(1)).findByName(exchangeName);
        verify(stockInfoRepository, never()).findByName(stockName);
        verify(stockExchangeInfoRepository, never()).save(any(StockExchangeInfo.class));
    }
}
