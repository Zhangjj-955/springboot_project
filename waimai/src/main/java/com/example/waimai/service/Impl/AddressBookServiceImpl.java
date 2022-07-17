package com.example.waimai.service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.waimai.common.BaseContext;
import com.example.waimai.entity.AddressBook;
import com.example.waimai.mapper.AddressBookMapper;
import com.example.waimai.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    AddressBookService addressBookService;
    @Transactional
    @Override
    public void changeDefault(Long id) {
        UpdateWrapper<AddressBook> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", BaseContext.getCurrentId()).set("is_default",0);
        addressBookService.update(wrapper);

        wrapper.clear();
        wrapper.set("is_default",1).eq("id",id);
        addressBookService.update(wrapper);
    }
}
