package com.seckill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private Integer code;
    private String msg;
    private Object data;


    public static Response success(String msg,Object data){
        return new Response(200,msg,data);
    }

    public static Response error(String msg){
        return new Response(500,msg,null);
    }
}
