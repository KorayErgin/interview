package com.exchange.app.stockexchange.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class StockExchangeInfoDTO
{

    private String name;

    private String description;

    private boolean isLiveInMarket;

}
