package com.exchange.app.stockexchange.model.entity;

import java.util.List;

import com.exchange.app.stock.model.entity.StockInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_exchanges_info")
public class StockExchangeInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_exchange_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_live_in_market")
    private boolean isLiveInMarket;

    @OneToMany(mappedBy = "stockExchangeInfo", cascade = CascadeType.ALL)
    private List<StockInfo> stocks;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

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

    public List<StockInfo> getStocks()
    {
        return stocks;
    }

    public void setStocks(List<StockInfo> stocks)
    {
        this.stocks = stocks;
    }
}
