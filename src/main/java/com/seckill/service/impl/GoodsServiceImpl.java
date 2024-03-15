package com.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.bean.Goods;
import com.seckill.service.GoodsService;
import com.seckill.mapper.GoodsMapper;
import org.springframework.stereotype.Service;

/**
* @author luosheep
* @description 针对表【t_goods(商品表)】的数据库操作Service实现
* @createDate 2024-03-14 22:02:17
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService {

}




