package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetMealController {
    @Autowired
    SetmealDishService setmealDishService;
    @Autowired
    SetmealService setmealService;
    @Autowired
    RedisTemplate redisTemplate;
    @PostMapping
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public Object saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealDishService.saveSetmealDish(setmealDto);
        redisTemplate.delete(setmealDto.getCategoryId());
        return R.success(null);
    }
    @GetMapping("page")
    public R<Page<SetmealDto>> page(long page,long pageSize,String name){
        return setmealService.setmealPage(page,pageSize,name);
    }
    @DeleteMapping
    @CacheEvict(value = "SetmealCache",allEntries = true)
    public Object deleteSetmeal(Long[] ids){
        log.info("ids:{}",ids);
        setmealService.deleteSetmeal(ids);
        return R.success(null);
    }
    @PostMapping("/status/{statu}")
    public Object changeStatus(@PathVariable int statu,Long[] ids){
        UpdateWrapper<Setmeal> wrapper = new UpdateWrapper<>();
        List<Long> idList = Arrays.asList(ids);
        wrapper.set("status",statu).in("id",idList);
        setmealService.update(wrapper);
        return R.success(null);
    }
    @GetMapping("/list")
    @Cacheable(value = "SetmealCache",key = "#categoryId")
    public R<List<Setmeal>> list(Long categoryId, int status){
        List<Setmeal> setmealList;
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id",categoryId).eq("status",status);
        setmealList = setmealService.list(setmealQueryWrapper);

        return R.success(setmealList);
    }

}
