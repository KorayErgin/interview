package com.exchange.app.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exchange.app.stock.model.entity.StockInfo;

@Repository
public interface StockInfoRepository extends JpaRepository<StockInfo, Long>
{

    Optional<StockInfo> findByName(String name);
}
