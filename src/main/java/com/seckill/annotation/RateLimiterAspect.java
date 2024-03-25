package com.seckill.annotation;

import com.seckill.bean.Response;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class RateLimiterAspect {

    @Resource
    private RedisTemplate<String,Integer> redisTemplate;

    @Around("@annotation(rateLimiter)")
    public Object aroundRateLimitedMethod(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String key = buildKey(joinPoint);
        int macCount= rateLimiter.maxCount();
        int seconds= rateLimiter.maxCount();
        ValueOperations<String, Integer> valueOperations= redisTemplate.opsForValue();
        Integer count=(Integer) valueOperations.get(key);
        if (count==null){
            valueOperations.set(key,1,seconds, TimeUnit.SECONDS);
        }
        else if (count<macCount){
           valueOperations.increment(key);
        }
       else{
            return Response.error("访问次数过多，请稍后再试");
        }
        return joinPoint.proceed();
    }

    private String buildKey(ProceedingJoinPoint joinPoint) {
        // 根据方法信息和请求参数生成唯一的key
        // 例如可以使用方法名和参数来生成
        Long userId = (Long) joinPoint.getArgs()[1];
        return joinPoint.getSignature().getDeclaringTypeName() + ":"+userId;
    }
}
