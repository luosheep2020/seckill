package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import com.seckill.bean.Order;
import com.seckill.bean.SeckillGoods;
import com.seckill.bean.SeckillOrder;
import com.seckill.config.RabbitMqConfig;
import com.seckill.mapper.SeckillOrderMapper;
import com.seckill.service.OrderService;
import com.seckill.service.SeckillGoodsService;
import com.seckill.service.SeckillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author luosheep
 * @description 针对表【t_seckill_order(秒杀订单表)】的数据库操作Service实现
 * @createDate 2024-03-14 22:03:02
 */
@Slf4j
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder>
        implements SeckillOrderService {

    @Resource
    private OrderService orderService;

    @Resource
    private SeckillGoodsService seckillGoodsService;
    @RabbitListener(queues = RabbitMqConfig.ORDER_DEAD_QUEUE)
    public void receive(Message message, Channel channel) throws IOException {
        log.info("订单进入死信队列");
        String msg = new String(message.getBody());
        Long orderId = Long.valueOf(msg);
        log.info("订单号：" + msg);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderId, orderId);
        Order order = orderService.getOne(queryWrapper);

        if (order.getStatus() == 0) {
            order.setStatus(6);
            orderService.updateById(order);
            //回退库存
            seckillGoodsService.update(new LambdaUpdateWrapper<SeckillGoods>()
                    .eq(SeckillGoods::getId,order.getGoodsId()).setSql("stock_count = stock_count+1"));
        }
        //应答
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}




