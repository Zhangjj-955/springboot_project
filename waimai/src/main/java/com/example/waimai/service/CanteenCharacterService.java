package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.CanteenCharacter;

public interface CanteenCharacterService extends IService<CanteenCharacter> {
    void saveCanteenCharacter(CanteenDto canteenDto);
}
