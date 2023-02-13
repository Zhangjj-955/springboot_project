package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.R;
import com.example.waimai.dto.CanteenDto;
import com.example.waimai.entity.Canteen;
import com.example.waimai.entity.CanteenCharacter;
import com.example.waimai.entity.SetmealDish;
import com.example.waimai.mapper.CanteenMapper;
import com.example.waimai.service.CanteenCharacterService;
import com.example.waimai.service.CanteenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements CanteenService {
    @Autowired
    CanteenService canteenService;
    @Autowired
    CanteenCharacterService canteenCharacterService;
@Autowired
    RedisTemplate redisTemplate;
    @Override
    public R<Page<CanteenDto>> canteenPage(long page, long pageSize) {
        Page<CanteenDto> page1 = new Page<>(page, pageSize);
        Page<Canteen> page2 = new Page<>(page, pageSize);
        canteenService.page(page2);
        BeanUtils.copyProperties(page2, page1);
        List<Canteen> canteenList = page2.getRecords();
        List<CanteenDto> canteenDtoList = new ArrayList<>();
        for (Canteen canteen : canteenList) {
            CanteenDto canteenDto = new CanteenDto();
            BeanUtils.copyProperties(canteen, canteenDto);
            QueryWrapper<CanteenCharacter> wrapper = new QueryWrapper<>();
            wrapper.eq("canteen_id", canteen.getId());
            List<CanteenCharacter> canteenCharacterList = canteenCharacterService.list(wrapper);
            List<String> characterStringList = new ArrayList<>();
            for (CanteenCharacter canteenCharacter : canteenCharacterList) {
                characterStringList.add(canteenCharacter.getCanteenCharacter());
            }
            canteenDto.setCharacterStringList(characterStringList);
            canteenDtoList.add(canteenDto);
        }
        page1.setRecords(canteenDtoList);
        return R.success(page1);
    }

    @Override
    public R<CanteenDto> getCanteenById(long id) {
        QueryWrapper<CanteenCharacter> wrapper = new QueryWrapper<>();
        wrapper.eq("canteen_id", id);
        List<CanteenCharacter> canteenCharacterList = canteenCharacterService.list(wrapper);
        List<String> characterStringList = new ArrayList<>();
        for (CanteenCharacter canteenCharacter : canteenCharacterList) {
            characterStringList.add(canteenCharacter.getCanteenCharacter());
        }
        Canteen canteen = canteenService.getById(id);
        CanteenDto canteenDto = new CanteenDto();
        BeanUtils.copyProperties(canteen,canteenDto);
        canteenDto.setCharacterStringList(characterStringList);
        return R.success(canteenDto);
    }

    @Override
    public R<Object> modifyCanteen(CanteenDto canteenDto) {
        canteenService.removeById(canteenDto);
        QueryWrapper<CanteenCharacter> wrapper = new QueryWrapper<>();
        wrapper.eq("canteen_id",canteenDto.getId());
        canteenCharacterService.remove(wrapper);
        canteenCharacterService.saveCanteenCharacter(canteenDto);
        return R.success(null);
    }

    @Override
    public R<List<CanteenDto>> listCanteen() {
        List<Canteen> canteenList = canteenService.list();
        List<CanteenDto> canteenDtoList = new ArrayList<>();
        for (Canteen canteen : canteenList) {
            CanteenDto canteenDto = new CanteenDto();
            BeanUtils.copyProperties(canteen,canteenDto);
            QueryWrapper<CanteenCharacter> wrapper = new QueryWrapper<>();
            wrapper.eq("canteen_id",canteen.getId());
            List<CanteenCharacter> canteenCharacterList = canteenCharacterService.list(wrapper);
            List<String> canteenCharacterStringList = new ArrayList<>();
            for (CanteenCharacter canteenCharacter : canteenCharacterList) {
                canteenCharacterStringList.add(canteenCharacter.getCanteenCharacter());
            }
            canteenDto.setCharacterStringList(canteenCharacterStringList);
            canteenDtoList.add(canteenDto);
        }
        canteenDtoList.sort(new Comparator<CanteenDto>() {
            @Override
            public int compare(CanteenDto o1, CanteenDto o2) {
                if (o1.getNumber() > o2.getNumber()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return R.success(canteenDtoList);
    }

    @Transactional
    @Override
    public void deleteCanteen(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
//        redisTemplate.delete(this.getById(ids[0]).getId());
        canteenService.removeBatchByIds(idList);

        //删除饭堂下分类，菜品


//        QueryWrapper<SetmealDish> wrapper = new QueryWrapper<>();
//        for (Long id :
//                idList) {
//            wrapper.eq("setmeal_id",id);
//            setmealDishService.remove(wrapper);
//            wrapper.clear();
//        }
    }
}

