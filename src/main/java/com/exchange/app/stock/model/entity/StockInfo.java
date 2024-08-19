package com.exchange.app.stock.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "stocks_info")
public class StockInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stock_id")
    private Long stockID;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "current_price")
    private BigDecimal currentPrice;

    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_exchange_id")
    private StockExchangeInfo stockExchangeInfo;

}
