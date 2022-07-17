package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submitOrder(Orders orders);
}
