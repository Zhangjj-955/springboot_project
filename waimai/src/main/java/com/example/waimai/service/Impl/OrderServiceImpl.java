package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.CustomerException;
import com.example.waimai.common.R;
import com.example.waimai.dto.OrdersDto;
import com.example.waimai.entity.*;
import com.example.waimai.mapper.OrderMapper;
import com.example.waimai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

    @Autowired
    DishService dishService;

    @Autowired
    CanteenService canteenService;
    @Autowired
    CategoryService categoryService;
    @Transactional
    @Override
    public void submitOrder(Orders orders) {
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        User user = userService.getById(BaseContext.getCurrentId());
        if (addressBook == null) {
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
        shoppingCartQueryWrapper.eq("user_id", user.getId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartQueryWrapper);
        List<OrderDetail> orderDetailList = new ArrayList<>();
        List<Dish> dishList = new ArrayList<>();
        for (ShoppingCart shoppingCart :
                shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());
            orderDetail.setName(shoppingCart.getName());
            if (shoppingCart.getDishId() != null) {
                orderDetail.setDishId(shoppingCart.getDishId());
            } else {
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }
            orderDetailList.add(orderDetail);

            Dish dish = dishService.getById(shoppingCart.getDishId());
            dish.setSaleNum(dish.getSaleNum()+1);
            dishList.add(dish);
        }
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("id",shoppingCartList.get(0).getDishId());
        Dish dish = dishService.getOne(wrapper);

        QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
        categoryQueryWrapper.eq("id",dish.getCategoryId());
        Category category = categoryService.getOne(categoryQueryWrapper);

        QueryWrapper<Canteen> canteenQueryWrapper = new QueryWrapper<>();
        canteenQueryWrapper.eq("id",category.getCanteenId());
        Canteen canteen = canteenService.getOne(canteenQueryWrapper);
        UpdateWrapper<Canteen> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",category.getCanteenId()).set("number",canteen.getNumber()+1);

        canteenService.update(updateWrapper);
        orderDetailService.saveBatch(orderDetailList);
        dishService.updateBatchById(dishList);
        shoppingCartService.remove(shoppingCartQueryWrapper);

        log.info(orders.toString()+orderDetailList);
    }

    @Transactional
    @Override
    public R<Page<OrdersDto>> pageOrder(int page, int pageSize) {
//        if (page==1&&pageSize==1){
//
//        }
        Page<Orders> page1 = new Page<>(page,pageSize);
        Page<OrdersDto> page2 = new Page<>(page,pageSize);
        User user = userService.getById(BaseContext.getCurrentId());

        orderService.page(page1);
        BeanUtils.copyProperties(page1,page2);
//        List<OrderDetail> orderDetailList = new ArrayList<>();
        List<OrdersDto> ordersDtoList = new ArrayList<>();
        for (Orders order : page1.getRecords()) {
            QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id",order.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(wrapper);

            QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
            dishQueryWrapper.eq("id",orderDetails.get(0).getDishId());
            Dish dish = dishService.getOne(dishQueryWrapper);

            QueryWrapper<Category> categoryQueryWrapper = new QueryWrapper<>();
            categoryQueryWrapper.eq("id",dish.getCategoryId());
            Category category = categoryService.getOne(categoryQueryWrapper);

            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order,ordersDto);
            ordersDto.setOrderDetails(orderDetails);
            ordersDto.setCanteenId(category.getCanteenId());
            ordersDtoList.add(ordersDto);
        }
        ordersDtoList.sort(new Comparator<OrdersDto>() {
            @Override
            public int compare(OrdersDto o2, OrdersDto o1) {
                return Integer.compare(o1.getOrderTime().compareTo(o2.getOrderTime()), 0);
            }
        });
        page2.setRecords(ordersDtoList);
        return R.success(page2);
    }

    @Override
    public R<OrdersDto> getOrderById(Long id) {
        Orders order = orderService.getById(id);
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",order.getId());
        List<OrderDetail> orderDetailList = orderDetailService.list(wrapper);
        OrdersDto ordersDto = new OrdersDto();
        BeanUtils.copyProperties(order,ordersDto);
        ordersDto.setOrderDetails(orderDetailList);
        return R.success(ordersDto);
    }

    @Override
    @Transactional
    public Object changeDishRate(Map<String,String> map) {
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("id",map.get("dishId"));
        Dish dish = dishService.getOne(wrapper);
        Integer oldRate = dish.getRate();
        Integer newRate = Integer.valueOf(map.get("rate"));

        UpdateWrapper<Dish> dishUpdateWrapper = new UpdateWrapper<>();
        dishUpdateWrapper.eq("id",map.get("dishId")).set("rate",(newRate+oldRate)/2);

        UpdateWrapper<OrderDetail> detailUpdateWrapper = new UpdateWrapper<>();
        detailUpdateWrapper.eq("dish_id",map.get("dishId")).set("rate",(newRate+oldRate)/2);

        orderDetailService.update(detailUpdateWrapper);
        dishService.update(dishUpdateWrapper);
        return R.success(null);
    }

    @Override
    public Object commitComment(String id,String comment) {
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id).set("comments",comment);
        orderService.update(updateWrapper);
        return R.success(null);
    }

}
