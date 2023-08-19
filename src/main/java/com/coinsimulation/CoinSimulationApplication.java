package com.coinsimulation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@EnableScheduling
public class CoinSimulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinSimulationApplication.class, args);
        // 스레드 작업 시작
    }

}
