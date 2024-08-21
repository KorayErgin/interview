package com.exchange.app.test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.Test;

import com.exchange.app.common.comparator.StockIdComparator;
import com.exchange.app.common.error.EntityNotFoundException;
import com.exchange.app.stock.model.entity.StockInfo;
import com.exchange.app.stock.repository.StockInfoRepository;
import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;
import com.exchange.app.stockexchange.repository.StockExchangeInfoRepository;
import com.exchange.app.stockexchange.service.StockExchangeInfoService;

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
        stockExchangeInfoService = new StockExchangeInfoService(stockExchangeInfoRepository, stockInfoRepository);
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

}
