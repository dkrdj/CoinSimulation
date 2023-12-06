package com.coinsimulation.entity;

import com.coinsimulation.dto.response.OrderResponse;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
@Builder
@ToString
public class Order {
    @Id
    private Long id;
    private Long userId;
    private String code;
    private String gubun;
    private Double price;
    @Setter
    private Double amount;
    private Timestamp dateTime;

    public OrderResponse toResponse() {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(this, orderResponse);
        orderResponse.setDateTime(this.getDateTime());
        return orderResponse;
    }
}
