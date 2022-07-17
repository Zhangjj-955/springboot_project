package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import com.example.waimai.entity.Orders;
import com.example.waimai.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @PostMapping("/submit")
    public Object submit(@RequestBody Orders orders){
        orderService.submitOrder(orders);
        return R.success(null);
    }
    @GetMapping("/userPage")
    public R<Page<Orders>> page(int page,int pageSize){
        Page<Orders> page1 = new Page<>(page,pageSize);
        QueryWrapper<Orders> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", BaseContext.getCurrentId());
        orderService.page(page1,wrapper);
        return R.success(page1);
    }
}
