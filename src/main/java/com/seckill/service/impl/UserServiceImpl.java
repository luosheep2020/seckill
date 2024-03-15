package com.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.bean.User;
import com.seckill.service.UserService;
import com.seckill.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author luosheep
* @description 针对表【t_user(用户表)】的数据库操作Service实现
* @createDate 2024-03-14 22:03:05
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




