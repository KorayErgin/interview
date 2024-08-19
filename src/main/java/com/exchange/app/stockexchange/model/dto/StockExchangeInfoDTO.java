package com.exchange.app.stockexchange.model.dto;

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
public class StockExchangeInfoDTO
{

    private String name;

    private String description;

    private boolean isLiveInMarket;

}
