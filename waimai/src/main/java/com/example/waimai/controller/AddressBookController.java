package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import com.example.waimai.entity.AddressBook;
import com.example.waimai.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;
    @PostMapping
    public R<String> addAddress(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(null);
    }

    /**
     * 只选出当前用户的addressBook，从BaseContext获取user_id
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> listAddress(){
        QueryWrapper<AddressBook> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", BaseContext.getCurrentId()).orderByDesc("is_default");
        List<AddressBook> addressBookList = addressBookService.list(wrapper);
        return R.success(addressBookList);
    }
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody Map<String,Long> map){
        addressBookService.changeDefault(map.get("id"));
        return R.success(null);
    }
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        QueryWrapper<AddressBook> addressBookQueryWrapper = new QueryWrapper<>();
        addressBookQueryWrapper.eq("user_id",BaseContext.getCurrentId());
        addressBookQueryWrapper.eq("is_default",1);
        AddressBook one = addressBookService.getOne(addressBookQueryWrapper);
        if (one!=null){
            return R.success(one);
        }else {
            return R.error("没有默认地址");
        }
    }
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBook(@PathVariable String id){
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        AddressBook one = addressBookService.getOne(queryWrapper);
        return R.success(one);
    }
    @Transactional
    @PutMapping
    public Object saveEditAddress(@RequestBody AddressBook addressBook){
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", addressBook.getId());
        AddressBook addressBook1 = addressBookService.getOne(queryWrapper);
        addressBook.setUserId(addressBook1.getUserId());
        addressBookService.removeById(addressBook.getId());
        addressBookService.save(addressBook);
        return R.success(null);
    }
}
