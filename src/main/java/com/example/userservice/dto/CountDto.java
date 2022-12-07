package com.example.userservice.dto;

import lombok.Data;

@Data
public class CountDto {
    private String userId;
    private String product;
    private Integer count;
}
