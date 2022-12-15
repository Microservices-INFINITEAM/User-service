package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data //setter getter 자동생성
@JsonInclude(JsonInclude.Include.NON_NULL) //불필요한 값 제어
public class ResponseCount {
    private String userId;
    private String music;
    private Integer count;
}
