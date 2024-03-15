package com.seckill.controller;

import com.seckill.bean.Response;
import com.seckill.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/list")
    public Response getOrderList(){
        return Response.success("查询所有订单",orderService.list());
    }
}
