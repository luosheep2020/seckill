package com.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.bean.Order;
import com.seckill.bean.Response;

/**
* @author luosheep
* @description 针对表【t_order】的数据库操作Service
* @createDate 2024-03-14 22:02:50
*/
public interface OrderService extends IService<Order> {
    Response seckill(Long userId, Long goodsId);
}
