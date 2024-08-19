package com.exchange.app.stock.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockInfoDTO
{
    private String name;

    private String description;

    private BigDecimal currentPrice;

}
