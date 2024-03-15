package com.seckill.controller;

import com.seckill.bean.Response;
import com.seckill.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/seckillgoods")
public class SeckillGoodsController {

    @Resource
    private SeckillGoodsService seckillGoodsService;


    @GetMapping("/list")
    public Response getGoodsList(){
        return Response.success("查询商品列表",seckillGoodsService.list());
    }
}
