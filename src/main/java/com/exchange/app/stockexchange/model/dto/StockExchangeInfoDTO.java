package com.exchange.app.stockexchange.model.dto;

public class StockExchangeInfoDTO
{

    private String name;

    private String description;

    private boolean isLiveInMarket;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isLiveInMarket()
    {
        return isLiveInMarket;
    }

    public void setLiveInMarket(boolean isLiveInMarket)
    {
        this.isLiveInMarket = isLiveInMarket;
    }

}
