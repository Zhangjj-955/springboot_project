package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.CanteenCharacter;
import com.example.waimai.entity.CanteenWindow;
import com.example.waimai.mapper.CanteenCharacterMapper;
import com.example.waimai.service.CanteenCharacterService;
import com.example.waimai.service.CanteenService;
import com.example.waimai.service.CanteenWindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CanteenCharacterImpl extends ServiceImpl<CanteenCharacterMapper, CanteenCharacter> implements CanteenCharacterService {

    @Autowired
    CanteenService canteenService;
    @Autowired
    CanteenCharacterService canteenCharacterService;
    @Autowired
    CanteenWindowService canteenWindowService;

    @Override
    public void saveCanteenCharacter(CanteenDto canteenDto) {
        canteenService.save(canteenDto);
        List<CanteenWindow> canteenWindowList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CanteenWindow canteenWindow = new CanteenWindow();
            canteenWindow.setWindowNumber(i);
            canteenWindow.setWindowStatus(0);
            canteenWindow.setCanteenId(canteenDto.getId());
            canteenWindowList.add(canteenWindow);
        }
        canteenWindowService.saveBatch(canteenWindowList);
        List<String> canteenCharacterStringList = canteenDto.getCharacterStringList();
        List<CanteenCharacter> canteenCharacterList = new ArrayList<>();
        for (String canteenCharacterString : canteenCharacterStringList) {
            CanteenCharacter canteenCharacter = new CanteenCharacter();
            canteenCharacter.setCanteenId(canteenDto.getId());
            canteenCharacter.setCanteenCharacter(canteenCharacterString);
            canteenCharacterList.add(canteenCharacter);
        }
        canteenCharacterService.saveBatch(canteenCharacterList);
    }
}
