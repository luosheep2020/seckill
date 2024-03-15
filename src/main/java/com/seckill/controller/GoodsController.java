package com.seckill.controller;

import com.seckill.bean.Response;
import com.seckill.service.GoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;


    @GetMapping("/list")
    public Response getGoodsList(){
        return Response.success("查询商品列表",goodsService.list());
    }
}
