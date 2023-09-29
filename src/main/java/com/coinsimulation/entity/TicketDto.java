package com.coinsimulation.entity;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TicketDto {
    private String id;
    private String type;
    private String code;
    private Double opening_price;
    private Double high_price;
    private Double low_price;
    private Double trade_price;
    private Double prev_closing_price;
    private String change;
    private Double change_price;
    private Double signed_change_price;
    private Double change_rate;
    private Double signed_change_rate;
    private Double trade_volume;
    private Double acc_trade_volume;
    private Double acc_trade_volume_24h;
    private Double acc_trade_price;
    private Double acc_trade_price_24h;
    private String trade_date;
    private String trade_time;
    private Long trade_timestamp;
    private String ask_bid;
    private Double acc_ask_volume;
    private Double acc_bid_volume;
    private Double highest_52_week_price;
    private String highest_52_week_date;
    private Double lowest_52_week_price;
    private String lowest_52_week_date;
    private String trade_status;
    private String market_state;
    private String market_state_for_ios;
    private Boolean is_trading_suspended;
    private LocalDateTime delisting_date;
    private String market_warning;
    private Long timestamp;
    private String stream_type;
}