package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.common.R;
import com.example.waimai.dto.OrdersDto;
import com.example.waimai.entity.Orders;

import java.util.Map;

public interface OrderService extends IService<Orders> {
    void submitOrder(Orders orders);

    R<Page<OrdersDto>> pageOrder(int page, int pageSize);

    R<OrdersDto> getOrderById(Long id);

    Object changeDishRate(Map<String,String> map);

    Object commitComment(String id,String comment);
}
