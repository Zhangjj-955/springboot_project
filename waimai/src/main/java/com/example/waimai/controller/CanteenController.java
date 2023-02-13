package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.Canteen;
import com.example.waimai.service.CanteenCharacterService;
import com.example.waimai.service.CanteenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/canteen")
public class CanteenController {
    @Autowired
    private CanteenService canteenService;
    @Autowired
    private CanteenCharacterService canteenCharacterService;
    @PostMapping("/getAll")
    public R<Page<Canteen>> getAll(@RequestBody Map<String,Long> map){
        Page<Canteen> page = new Page<>(map.get("startNum"),map.get("pageSize"));
        canteenService.page(page);
        return R.success(page);
    }
    @PostMapping
    public Object saveCanteen(@RequestBody CanteenDto canteenDto){
        canteenCharacterService.saveCanteenCharacter(canteenDto);
        return R.success(null);
    }
    @GetMapping("page")
    public R<Page<CanteenDto>> page(long page,long pageSize){
        return canteenService.canteenPage(page,pageSize);
    }
    @GetMapping("{id}")
    public R<CanteenDto> getById(@PathVariable long id){
        return canteenService.getCanteenById(id);
    }
    @PutMapping
    public Object modifyCanteen(@RequestBody CanteenDto canteenDto){
        return canteenService.modifyCanteen(canteenDto);
    }
    @GetMapping("/list")
    public R<List<CanteenDto>> list(){
        return canteenService.listCanteen();
    }
    @PostMapping("/status/{statu}")
    public Object changeStatus(@PathVariable int statu,Long[] ids){
        UpdateWrapper<Canteen> wrapper = new UpdateWrapper<>();
        List<Long> idList = Arrays.asList(ids);
        wrapper.set("status",statu).in("id",idList);
        canteenService.update(wrapper);
        return R.success(null);
    }
    @DeleteMapping
    @CacheEvict(value = "CanteenCache",allEntries = true)
    public Object deleteCanteen(Long[] ids){
        log.info("ids:{}",ids);
        canteenService.deleteCanteen(ids);
        return R.success(null);
    }
}
