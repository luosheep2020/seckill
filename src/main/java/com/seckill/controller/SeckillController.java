package com.seckill.controller;

import com.seckill.bean.Response;
import com.seckill.service.OrderService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
    @Resource
    private OrderService orderService;
    @Resource
    private Redisson redisson;
    public static final String LOCK_KEY = "SECKILL_STOCK_COUNT:";

    @PostMapping

    public Response doSeckill(@RequestBody Map<String, Long> param) {
        String key = LOCK_KEY + param.get("goodsId");
        RLock rLock = redisson.getLock(key);
        try {
            rLock.lock();
            return orderService.seckill(param.get("userId"), param.get("goodsId"));
        } finally {
            rLock.unlock();
        }
    }


}
