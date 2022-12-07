package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data //setter getter 자동생성
@JsonInclude(JsonInclude.Include.NON_NULL) //불필요한 값 제어
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
    private Date createdDate;
    private List<String> userLikeGenre;

    private List<ResponseOrder> orders;
}
