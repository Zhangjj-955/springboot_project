package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.common.R;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.Canteen;

import java.util.List;

public interface CanteenService extends IService<Canteen> {
    R<Page<CanteenDto>> canteenPage(long page,long pageSize);
    R<CanteenDto> getCanteenById(long id);

    R<Object> modifyCanteen(CanteenDto canteenDto);

    R<List<CanteenDto>> listCanteen();

    void deleteCanteen(Long[] ids);
}
