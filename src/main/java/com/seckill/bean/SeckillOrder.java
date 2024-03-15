package com.seckill.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀订单表
 * @TableName t_seckill_order
 */
@TableName(value ="t_seckill_order")
@Data
@Builder
public class SeckillOrder implements Serializable {
    /**
     * 秒杀订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品ID
     */
    private Long goodsId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}