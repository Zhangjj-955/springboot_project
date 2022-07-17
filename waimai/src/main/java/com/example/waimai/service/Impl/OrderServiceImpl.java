package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.CustomerException;
import com.example.waimai.entity.*;
import com.example.waimai.mapper.OrderMapper;
import com.example.waimai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    AddressBookService addressBookService;
    @Autowired
    OrderDetailService orderDetailService;
    @Transactional
    @Override
    public void submitOrder(Orders orders) {
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        User user = userService.getById(BaseContext.getCurrentId());
        if (addressBook==null){
            throw new CustomerException("地址信息有误");
        }
        Long orderId = IdWorker.getId();

        orders.setUserId(BaseContext.getCurrentId());
        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setAddress(addressBook.getDetail());
        orders.setStatus(2);
        orders.setPhone(addressBook.getPhone());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",user.getId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartQueryWrapper);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart:
             shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());
            if (shoppingCart.getDishId()!=null){
                orderDetail.setDishId(shoppingCart.getDishId());
            }else {
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }
            orderDetailList.add(orderDetail);
        }
        orderDetailService.saveBatch(orderDetailList);

        shoppingCartService.remove(shoppingCartQueryWrapper);
    }
}
