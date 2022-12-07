package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="order-service") //application name
public interface OrderServiceClient {
    @GetMapping("order-service/{userId}/orders")//주문 확인을 위한 uri
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
