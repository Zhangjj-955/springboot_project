package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.R;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Category;
import com.example.waimai.entity.Setmeal;
import com.example.waimai.entity.SetmealDish;
import com.example.waimai.mapper.SetmealMapper;
import com.example.waimai.service.CategoryService;
import com.example.waimai.service.SetmealDishService;
import com.example.waimai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealService setmealService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    SetmealDishService setmealDishService;
    @Override
    public R<Page<SetmealDto>> setmealPage(long page, long pageSize,String name) {
        Page<SetmealDto> page1 = new Page<>(page,pageSize);
        Page<Setmeal> page2 = new Page<>(page,pageSize);
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.like(name!=null,"name",name);
        setmealService.page(page2,wrapper);
        BeanUtils.copyProperties(page2,page1);
        List<Setmeal> setmealList = page2.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        SetmealDto setmealDto;
        for (Setmeal setmeal :
                setmealList) {
            setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            setmealDtoList.add(setmealDto);
        }
        page1.setRecords(setmealDtoList);
        return R.success(page1);
    }
    @Transactional
    @Override
    public void deleteSetmeal(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        setmealService.removeBatchByIds(idList);

        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
        for (Long id :
                idList) {
            wrapper.eq("setmeal_id",id);
            setmealDishService.remove(wrapper);
            wrapper.clear();
        }
    }
}
