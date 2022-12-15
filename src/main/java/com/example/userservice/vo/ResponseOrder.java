package com.example.userservice.vo;

import lombok.Data;

@Data
public class ResponseOrder {
    private String orderId;
    private Integer musicQty;
    private Integer musicPrice;
    private Integer musicTotalPrice;
}
