package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.entity.SetmealDish;
import com.example.waimai.mapper.SetmealDishMapper;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    @Autowired
    SetmealService setmealService;
    @Autowired
    SetmealDishService setmealDishService;
    @Transactional
    @Override
    public void saveSetmealDish(SetmealDto setmealDto) {
        setmealService.save(setmealDto);

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish :
                setmealDishList) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishList);
    }
}
