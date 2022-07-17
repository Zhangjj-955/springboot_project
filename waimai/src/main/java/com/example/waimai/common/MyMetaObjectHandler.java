package com.example.waimai.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter("updateUser"))
            metaObject.setValue("updateUser",BaseContext.getCurrentId());
        if (metaObject.hasSetter("updateTime"))
            metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("createTime"))
            metaObject.setValue("createTime", LocalDateTime.now());
        if (metaObject.hasSetter("createUser"))
            metaObject.setValue("createUser",BaseContext.getCurrentId());
        if (metaObject.hasSetter("orderTime"))
            metaObject.setValue("orderTime",LocalDateTime.now());
        if (metaObject.hasSetter("checkoutTime"))
            metaObject.setValue("checkoutTime",LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
