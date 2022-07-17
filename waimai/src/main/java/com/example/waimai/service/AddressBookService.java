package com.example.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.waimai.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void changeDefault(Long id);
}
