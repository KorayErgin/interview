package com.exchange.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class ExchangeAppApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ExchangeAppApplication.class, args);
    }

}
