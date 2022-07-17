package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetMealController {
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    SetmealService setmealService;
    @PostMapping
    public Object saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealDishService.saveSetmealDish(setmealDto);
        return R.success(null);
    }
    @GetMapping("page")
    public R<Page<SetmealDto>> page(long page,long pageSize,String name){
        return setmealService.setmealPage(page,pageSize,name);
    }
    @DeleteMapping
    public Object deleteSetmeal(Long[] ids){
        log.info("ids:{}",ids);
        setmealService.deleteSetmeal(ids);
        return R.success(null);
    }
    @PostMapping("/status/{statu}")
    public Object changeStatus(@PathVariable int statu,Long[] ids){
        UpdateWrapper<Setmeal> wrapper = new UpdateWrapper();
        List<Long> idList = Arrays.asList(ids);
        wrapper.set("status",statu).in("id",idList);
        setmealService.update(wrapper);
        return R.success(null);
    }
    @GetMapping("/list")
    public R<List<Setmeal>> list(Long categoryId, int status){
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id",categoryId).eq("status",status);
        List<Setmeal> setmealList = setmealService.list(setmealQueryWrapper);
        return R.success(setmealList);
    }
}
