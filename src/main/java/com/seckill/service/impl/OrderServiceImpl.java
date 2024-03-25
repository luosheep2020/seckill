package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.bean.*;
import com.seckill.config.RabbitMqConfig;
import com.seckill.mapper.OrderMapper;
import com.seckill.service.GoodsService;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillGoodsService;
import com.seckill.service.SeckillOrderService;
import com.seckill.util.RedisUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private Redisson  redisson;
    public static final String SECKILL_GOODS_KEY="seckill:goods:";
    public static final String ORDER_LOCK_KEY="order:lock:";
    @Override
    @Transactional
    public Response seckill(Long userId, Long goodsId) {
        Goods goods = goodsService.getById(goodsId);
        SeckillGoods seckillGoods = seckillGoodsService
                .getOne(new LambdaQueryWrapper<SeckillGoods>().eq(SeckillGoods::getGoodsId, goodsId));
        redisUtils.set(SECKILL_GOODS_KEY+goodsId,seckillGoods);
        redisTemplate.opsForValue().set(SECKILL_GOODS_KEY+goodsId,goods.toString());
        LambdaQueryWrapper<Order> orderQuery= new LambdaQueryWrapper<>();
        orderQuery.eq(Order::getUserId,userId).eq(Order::getGoodsId,goodsId).and(q->q.in(Order::getStatus,0,1,2,3,4,5));
        int count= (int) orderService.count(orderQuery);
        if (count>0){
            return Response.error("秒杀商品一人限购一单哦~");
        }
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

    @Override
    @Transactional
    public Response payOrder(OrderPayDTO orderPayDTO) {
        Long userId =  orderPayDTO.getUserId() ;
        Long orderId= orderPayDTO.getOrderId();
        int payWay= orderPayDTO.getPayWay();
        Order order=this.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderId,orderId));
        Assert.isTrue(order!=null,"订单不存在");
        Assert.isTrue(order.getStatus()==0,"订单不可支付，请检查订单状态");
        RLock rLock=redisson.getLock(ORDER_LOCK_KEY+orderId);
        boolean result=false;
        try {
            rLock.lock();
            switch (payWay){
                case 1:{
                    result=VxPay(orderId);
                    break;
                }
                case 2 :{
                    result=AliPay(orderId);
                    break;
                }
                default :{
                    //其他支付
                }
            }
        }finally {
            if (rLock.isLocked()){
                rLock.unlock();
            }
        }
        if (result){
            order.setStatus(1);
            order.setPayDate(LocalDateTime.now());
            this.updateById(order);
            return Response.success("支付成功",order);
        }
        return Response.error("支付成功失败");
    }

    @Override
    public boolean VxPay(Long oderId) {
        //Todo 微信支付
        return true;
    }

    @Override
    public boolean AliPay(Long oderId) {
        //Todo 阿里支付
        return true;
    }
}




