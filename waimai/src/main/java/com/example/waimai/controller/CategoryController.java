package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.entity.Category;
import com.example.waimai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService service;
    @PostMapping
    public Object addCategory(@RequestBody Category category){
        service.save(category);
        return R.success(null);
    }
    @GetMapping("/page")
    public R<Page<Category>> page(int page,int pageSize){
        Page<Category> page1 = new Page<>(page,pageSize);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<Category>();
        queryWrapper.orderByAsc("sort");
        service.page(page1,queryWrapper);
        return R.success(page1);
    }
    @DeleteMapping
    public Object delete(Long ids){
        service.remove(ids);
        return R.success(null);
    }
    @PutMapping
    public Object edit(@RequestBody Category category){
        service.updateById(category);
        return R.success(null);
    }
    @GetMapping("/list")
    public R<List<Category>> list(String type){
        QueryWrapper<Category> wrapper = new QueryWrapper();
        wrapper.eq(type!=null,"type",type);
        List<Category> categoryList = service.list(wrapper);
        return R.success(categoryList);
    }
}
