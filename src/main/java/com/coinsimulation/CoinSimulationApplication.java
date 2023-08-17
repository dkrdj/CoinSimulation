package com.coinsimulation;

import com.coinsimulation.util.UpbitThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoinSimulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinSimulationApplication.class, args);
        new UpbitThread().start();
    }

}
