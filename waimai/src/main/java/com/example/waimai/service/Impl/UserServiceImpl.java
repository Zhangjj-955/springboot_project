package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.mapper.UserMapper;
import com.example.waimai.service.UserService;
import org.springframework.stereotype.Service;
import com.example.waimai.entity.User;
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
