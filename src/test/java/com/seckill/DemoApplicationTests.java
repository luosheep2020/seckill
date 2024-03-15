package com.seckill;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.bean.SeckillGoods;
import com.seckill.service.SeckillGoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class DemoApplicationTests {
    @Resource
    private SeckillGoodsService seckillGoodsService;


    @Test
    void contextLoads() {
        SeckillGoods seckillGoods=seckillGoodsService
                .getOne(new LambdaQueryWrapper<SeckillGoods>().eq(SeckillGoods::getGoodsId,1));
        System.out.println(seckillGoods);

    }


}
