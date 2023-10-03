package com.coinsimulation.entity.coin;

import com.coinsimulation.dto.TicketDto;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Ethereum {
    public static final String COIN_TYPE = "KRW-ETC";
    @Id
    private String id;
    private String type;
    private String code;
    private Double openingPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double tradePrice;
    private Double prevClosingPrice;
    private String change;
    private Double changePrice;
    private Double signedChangePrice;
    private Double changeRate;
    private Double signedChangeRate;
    private Double tradeVolume;
    private Double accTradeVolume;
    @JsonProperty("acc_trade_volume_24h")
    private Double accTradeVolume24h;
    private Double accTradePrice;
    @JsonProperty("acc_trade_price_24h")
    private Double accTradePrice24h;
    private String tradeDate;
    private String tradeTime;
    private Long tradeTimestamp;
    private String askBid;
    private Double accAskVolume;
    private Double accBidVolume;
    @JsonProperty("highest_52_week_price")
    private Double highest52WeekPrice;
    @JsonProperty("highest_52_week_date")
    private String highest52WeekDate;
    @JsonProperty("lowest_52_week_price")
    private Double lowest52WeekPrice;
    @JsonProperty("lowest_52_week_date")
    private String lowest52WeekDate;
    private String tradeStatus;
    private String marketState;
    private String marketStateForIos;
    private Boolean isTradingSuspended;
    private LocalDateTime delistingDate;
    private String marketWarning;
    private Long timestamp;
    private String streamType;

    public static Ethereum fromTicket(TicketDto ticketDto) {
        Ethereum ethereum = new Ethereum();
        BeanUtils.copyProperties(ticketDto, ethereum);
        return ethereum;
    }


}

