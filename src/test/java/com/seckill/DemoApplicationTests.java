package com.seckill;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.seckill.annotation.MyCustomAnnotation;
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
        seckillGoodsService.remove(Wrappers.lambdaQuery());
    }


    @Test
    @MyCustomAnnotation
    public void testAnnotation(){
        System.out.println("do something");
    }
}
