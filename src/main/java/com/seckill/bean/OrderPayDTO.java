package com.seckill.bean;

import lombok.Data;

@Data
public class OrderPayDTO {
    private Long userId;
    private Long orderId;
    private int payWay;
}
