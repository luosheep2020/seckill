package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.bean.*;
import com.seckill.bean.*;
import com.seckill.config.RabbitMqConfig;
import com.seckill.mapper.OrderMapper;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillGoodsService;
import com.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author luosheep
 * @description 针对表【t_order】的数据库操作Service实现
 * @createDate 2024-03-14 22:02:50
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private SeckillOrderService seckillOrderService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Override
    @Transactional
    public Response seckill(Long userId, Long goodsId) {
        Goods goods = goodsService.getById(goodsId);
        SeckillGoods seckillGoods = seckillGoodsService
                .getOne(new LambdaQueryWrapper<SeckillGoods>().eq(SeckillGoods::getGoodsId, goodsId));
        System.out.println(seckillGoods);
        if (seckillGoods == null) {
            return Response.error("该商品没有参与秒杀哦~");
        }

        if (seckillGoods.getStartDate().isAfter(LocalDateTime.now())) {
            return Response.error("别着急，秒杀还未开始哦~");
        }
        if (seckillGoods.getEndDate().isBefore(LocalDateTime.now())) {
            return Response.error("秒杀结束了哦~");
        }
        if (seckillGoods.getStockCount() < 1) {
            return Response.error("手慢了，该商品已被抢购一空，看看其他的吧~");
        }
        LambdaUpdateWrapper<SeckillGoods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("stock_count = stock_count-1")
                .eq(SeckillGoods::getGoodsId, goodsId);
        boolean success = seckillGoodsService.update(updateWrapper);
        if (!success) {
            return Response.error("手慢了，该商品已被抢购一空，看看其他的吧~");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderId(IdWorker.getId())
                .goodsId(goodsId)
                .deliveryAddrId(0L)
                .goodsName(goods.getGoodsName())
                .goodsCount(1)
                .goodsPrice(seckillGoods.getSeckillPrice())
                .orderChannel(1)
                .status(0)
                .createDate(LocalDateTime.now())
                .build();
        orderService.save(order);

        SeckillOrder seckillOrder = SeckillOrder.builder()
                .userId(userId)
                .goodsId(goodsId)
                .orderId(order.getOrderId())
                .build();
        seckillOrderService.save(seckillOrder);

        rabbitTemplate.convertAndSend(RabbitMqConfig.ORDER_EXCHANGE,RabbitMqConfig.ORDER_ROUTING,order.getOrderId());
        return Response.success("创建订单", null);

    }
}




