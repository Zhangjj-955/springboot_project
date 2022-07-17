package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.CustomerException;
import com.example.waimai.entity.Category;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.mapper.CategoryMapper;
import com.example.waimai.service.CategoryService;
import com.example.waimai.service.DishService;
import com.example.waimai.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;
    @Override
    public void remove(Long id) {
        QueryWrapper wrapper = new QueryWrapper<Dish>();
        wrapper.eq("category_id",id);
        long dishCount = dishService.count(wrapper);
        if (dishCount!=0){
            throw new CustomerException("已关联菜品，不能删除");
        }
        wrapper = new QueryWrapper<Setmeal>();
        wrapper.eq("category_id",id);
        long setmealCount = setmealService.count(wrapper);
        if (setmealCount!=0){
            throw new CustomerException("已关联套餐，不能删除");
        }
        super.removeById(id);
    }
}
