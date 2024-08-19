package com.exchange.app.stockexchange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exchange.app.stockexchange.model.entity.StockExchangeInfo;

@Repository
public interface StockExchangeInfoRepository extends JpaRepository<StockExchangeInfo, Long>
{

    Optional<StockExchangeInfo> findByName(String name);

}
