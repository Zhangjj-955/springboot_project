package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.common.R;
import com.example.waimai.dto.SetmealDto;
import com.example.waimai.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    R<Page<SetmealDto>> setmealPage(long page, long pageSize,String name);
    void deleteSetmeal(Long[] ids);
}
