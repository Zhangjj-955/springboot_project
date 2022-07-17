package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.DishFlavor;
import com.example.waimai.mapper.DishMapper;
import com.example.waimai.service.DishFlavorService;
import com.example.waimai.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService flavorService;

    /**
     * 新增菜品同时保存口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveDishWithFlavor(DishDto dishDto) {

        this.save(dishDto);         //先保存Dish，这样Dish就有了ID，dishDto是Dish的子类，多态save

        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor flavor:
             flavorList) {
            flavor.setDishId(dishDto.getId());
        }
        flavorService.saveBatch(flavorList);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public DishDto queryDishWithFlavor(Long id) {
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",id);
        List<DishFlavor> dishFlavorList = flavorService.list(queryWrapper); //先查出所有属于这个dish_id的flavor

        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavorList);                                 //用dishDto将flavor和dish结合在一起
        return dishDto;
    }
    @Transactional  //两张表，要加事务管理
    @Override
    public void updateDishAndFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        Long id = dishDto.getId();
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper();
        wrapper.eq("dish_id",id);
        flavorService.remove(wrapper);
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor dishFlavor :
                flavorList) {
            dishFlavor.setDishId(id);
        }
        flavorService.saveBatch(flavorList);
    }
    @Transactional
    @Override
    public void deleteDishAndFlavor(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        this.removeBatchByIds(idList);
        QueryWrapper<DishFlavor> wrapper = new QueryWrapper<>();
        wrapper.in("dish_id",idList);
        flavorService.remove(wrapper);
    }
}
