package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.SetmealDish;

public interface SetmealDishService extends IService<SetmealDish> {
    void saveSetmealDish(SetmealDto setmealDto);
}
