package com.coinsimulation.entity;

import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document
@Getter
@Setter
public class Bitcoin {
    public static final String COIN_TYPE = "KRW-BTC";
    @Id
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

    public static Bitcoin fromTicket(TicketDto ticketDto) {
        Bitcoin bitcoin = new Bitcoin();
        BeanUtils.copyProperties(ticketDto, bitcoin);
        return bitcoin;
    }


}
