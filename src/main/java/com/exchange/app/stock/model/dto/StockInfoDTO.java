package com.exchange.app.stock.model.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class StockInfoDTO
{
    private String name;

    private String description;

    private BigDecimal currentPrice;

    
}
