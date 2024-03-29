package com.seckill.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class RabbitMqConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    public static final String ORDER_QUEUE = "order_queue";
    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String ORDER_DEAD_EXCHANGE = "order_dead_exchange";
    public static final String ORDER_DEAD_QUEUE = "order_dead_queue";
    public static final String ORDER_ROUTING = "order_routing";
    public static final String ORDER_DEAD_ROUTING = "order_dead_routing";
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);
    @Bean
    public MessageConverter messageConverter(){
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

    /**
     * 订单交换机
     */
    @Bean("orderExchange")
    public DirectExchange getOrderExchange(){
        return new DirectExchange(ORDER_EXCHANGE);
    }
    /**
     * 订单队列
     */
    @Bean("orderQueue")
    public Queue getOrderQueue(){
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("x-dead-letter-exchange",ORDER_DEAD_EXCHANGE);
        map.put("x-dead-letter-routing-key",ORDER_DEAD_ROUTING);
        map.put("x-message-ttl",30000);
        return QueueBuilder.durable(ORDER_QUEUE).withArguments(map).build();
    }

    /**
     * 订单交换机与订单队列绑定
     */
    @Bean
    public Binding oExchangeBindingO(
            @Qualifier("orderExchange") DirectExchange exchange,
            @Qualifier("orderQueue") Queue queue
    ){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_ROUTING);
    }
    /**
     * 死信交换机
     */
    @Bean("orderDeadExchange")
    public DirectExchange getOrderDeadExchange(){
        return new DirectExchange(ORDER_DEAD_EXCHANGE);
    }
    /**
     * 死信队列
     */
    @Bean("orderDeadQueue")
    public Queue getOrderDradQueue(){
        return new Queue(ORDER_DEAD_QUEUE,true,false,false,null);
    }

    @Bean
    public Binding deadExchangeDead(
            @Qualifier("orderDeadExchange") DirectExchange exchange,
            @Qualifier("orderDeadQueue") Queue queue
    ){
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_DEAD_ROUTING);
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        if (ack) {
            logger.info("{}:消息成功到达交换器",correlationData.getId());
        }else{
            logger.error("{}:消息发送失败", correlationData.getId());
        }
    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        logger.error("{}:消息未成功路由到队列",returnedMessage.getMessage().getMessageProperties().getMessageId());
    }
}
