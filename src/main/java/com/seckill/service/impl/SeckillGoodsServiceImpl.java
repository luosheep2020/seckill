package com.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.bean.SeckillGoods;
import com.seckill.service.SeckillGoodsService;
import com.seckill.mapper.SeckillGoodsMapper;
import org.springframework.stereotype.Service;

/**
* @author luosheep
* @description 针对表【t_seckill_goods(秒杀商品表)】的数据库操作Service实现
* @createDate 2024-03-14 22:02:58
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService {

}




