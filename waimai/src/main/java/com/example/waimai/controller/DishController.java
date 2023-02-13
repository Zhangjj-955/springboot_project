package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.DishDto;
import com.example.waimai.entity.Dish;
import com.example.waimai.entity.DishFlavor;
import com.example.waimai.service.CategoryService;
import com.example.waimai.service.DishFlavorService;
import com.example.waimai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    DishService service;
    @Autowired
    CategoryService categoryService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 分页时Dish里面只有categoryId,前端需要的是categoryName,DishDto里面有categoryName属性
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping("/page")
    public Object page(int page,int pageSize,String name){
        Page<Dish> page1 = new Page<>(page,pageSize);
        Page<DishDto> page2 = new Page<>();
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(name!=null,"name",name);
        service.page(page1,queryWrapper);            //先查出一页dish

        BeanUtils.copyProperties(page1,page2,"record"); //将除了record的属性都复制给dishDto的page
        List<DishDto> dishDtoList = new ArrayList<>();      //存储dishDto的record

        for (Dish record : page1.getRecords()) {
            String categoryName = categoryService.getById(record.getCategoryId()).getName();    //查category表查出categoryName

            DishDto dishDto = new DishDto();        //新建一个dishDto存储dish的信息再加上categoryName
            BeanUtils.copyProperties(record,dishDto);
            dishDto.setCategoryName(categoryName);
            dishDtoList.add(dishDto);               //添加到dishDto的record里
        }
        page2.setRecords(dishDtoList);
        return R.success(page2);
    }

    /**
     * 添加菜品，同时在DishFlavor表中添加口味
     * @param dishDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public Object addDish(@RequestBody DishDto dishDto){
//        redisTemplate.opsForValue().set();
        service.saveDishWithFlavor(dishDto);
        return R.success(null);
    }

    /**
     * 修改菜品时使用，回显，同时要查Flavor表
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> queryDishById(@PathVariable Long id){
        DishDto dishDto = service.queryDishWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改完成保存菜品，同时操作Flavor表
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache",allEntries = true)  //allEntries要指定为true删除所有,不然的话要根据cacheName删除
    public Object saveEditDish(@RequestBody DishDto dishDto){
        service.updateDishAndFlavor(dishDto);
        return R.success(null);
    }

    /**
     * 批量删除或单个删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Object deleteDish(Long[] ids){
        service.deleteDishAndFlavor(ids);
        return R.success(null);
    }

    /**
     * 批量更新状态或单个更新
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Object changeStatusByIds(@PathVariable Long status,Long[] ids){
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();  //用的是UpdateWrapper才有set方法
        List<Long> list = Arrays.asList(ids);
        updateWrapper.in("id",list).set("status",status);   //用.in(column,Collection)就是where id in (...)
        service.update(updateWrapper);
        return R.success(null);
    }

    @GetMapping("/list")
    @Cacheable(value = "dishCache" ,key = "#categoryId")
    public R<List<DishDto>> dishList(Long categoryId){
        List<DishDto> dishDtoList = new ArrayList<>();
        QueryWrapper<Dish> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id",categoryId).orderByDesc("sale_num");
        List<Dish> dishList = service.list(wrapper);
        QueryWrapper<DishFlavor> flavorQueryWrapper = new QueryWrapper<>();
        for (Dish dish :
                dishList) {
            flavorQueryWrapper.eq("dish_id",dish.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorQueryWrapper);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setFlavors(dishFlavorList);
            dishDtoList.add(dishDto);
            flavorQueryWrapper.clear();
        }
        return R.success(dishDtoList);
    }
}
