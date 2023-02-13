package com.example.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.CanteenCharacter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CanteenCharacterMapper extends BaseMapper<CanteenCharacter> {
}
