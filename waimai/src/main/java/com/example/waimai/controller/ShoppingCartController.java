package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import com.example.waimai.entity.ShoppingCart;
import com.example.waimai.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        shoppingCart.setUserId(BaseContext.getCurrentId());
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",BaseContext.getCurrentId());
        if (shoppingCart.getDishId()!=null){
            shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
        }else {
            shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
        ShoppingCart currentShoppingCart = shoppingCartService.getOne(shoppingCartQueryWrapper);
        if (currentShoppingCart==null){
            shoppingCart.setNumber(1);

            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }else {
            currentShoppingCart.setNumber(currentShoppingCart.getNumber()+1);
            shoppingCartService.updateById(currentShoppingCart);
        }
        return R.success(currentShoppingCart);
    }
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody Map<String,Long> map){
        QueryWrapper<ShoppingCart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",BaseContext.getCurrentId());
        if (map.get("dishId")!=null){
            wrapper.eq("dish_id",map.get("dishId"));
        }else {
            wrapper.eq("setmeal_id",map.get("setmealId"));
        }
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        one.setNumber(one.getNumber()-1);
        if (one.getNumber()==0){
            shoppingCartService.removeById(one);
        }else {
            shoppingCartService.updateById(one);
        }
        return R.success(one);
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }
    @DeleteMapping("clean")
    public Object clean(){
        QueryWrapper<ShoppingCart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",BaseContext.getCurrentId());
        shoppingCartService.remove(wrapper);
        return R.success(null);
    }
}
