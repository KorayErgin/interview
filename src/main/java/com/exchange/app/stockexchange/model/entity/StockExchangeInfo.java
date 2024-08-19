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
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
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

}
