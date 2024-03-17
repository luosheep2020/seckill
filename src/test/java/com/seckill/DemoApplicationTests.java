package com.seckill;

import com.alibaba.fastjson2.JSON;
import com.seckill.bean.Goods;
import com.seckill.service.SeckillGoodsService;
import com.seckill.util.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class DemoApplicationTests {
    @Resource
    private SeckillGoodsService seckillGoodsService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private RedisUtils redisUtils;
    public static final String SECKILL_GOODS_KEY="seckill:goods:";
    @Test
    void contextLoads() {
        Goods goods= JSON.parseObject(redisTemplate.opsForValue().get(SECKILL_GOODS_KEY+1), Goods.class);
        assert goods != null;
        System.out.println(goods.toString());
    }


}
