package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveDishWithFlavor(DishDto dishDto);
    DishDto queryDishWithFlavor(Long id);

    void updateDishAndFlavor(DishDto dishDto);

    void deleteDishAndFlavor(Long[] ids);
}
