package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.entity.CanteenWindow;
import com.example.waimai.mapper.WindowMapper;
import com.example.waimai.service.CanteenWindowService;
import org.springframework.stereotype.Service;

@Service
public class WindowServiceImpl extends ServiceImpl<WindowMapper, CanteenWindow> implements CanteenWindowService {
}
