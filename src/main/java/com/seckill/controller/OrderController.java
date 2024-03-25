package com.seckill.controller;

import com.seckill.bean.OrderPayDTO;
import com.seckill.bean.Response;
import com.seckill.service.OrderService;
import com.seckill.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;

    @GetMapping("/list")
    public Response getOrderList() {
        return Response.success("查询所有订单", orderService.list());
    }

    @PostMapping("/pay")
    public Response payOrder(@RequestBody OrderPayDTO orderPayDTO) {
        return orderService.payOrder(orderPayDTO);

    }
}
