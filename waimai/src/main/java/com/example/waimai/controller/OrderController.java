package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import com.example.waimai.dto.OrdersDto;
import com.example.waimai.entity.Canteen;
import com.example.waimai.entity.CanteenWindow;
import com.example.waimai.entity.Orders;
import com.example.waimai.service.CanteenService;
import com.example.waimai.service.CanteenWindowService;
import com.example.waimai.service.OrderService;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    CanteenWindowService canteenWindowService;
    @Autowired
    CanteenService canteenService;

    @PostMapping("/submit")
    public Object submit(@RequestBody Orders orders) {
        orderService.submitOrder(orders);
        return R.success(null);
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {
        return orderService.pageOrder(page, pageSize);
    }

    @Transactional
    @PutMapping
    public Object editOrderDetail(@RequestBody Map<String, String> map) {
        UpdateWrapper<Orders> wrapper = new UpdateWrapper<>();
        String status = map.get("status");
        String id = map.get("id");
        String canteenId = map.get("canteenId");
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Orders one = orderService.getOne(queryWrapper);
        if (one.getDelivery() == 1) {
            if (status.equals("3")) {
                QueryWrapper<CanteenWindow> windowQueryWrapper = new QueryWrapper<>();
                windowQueryWrapper.eq("canteen_id", canteenId);
                List<CanteenWindow> windowList = canteenWindowService.list(windowQueryWrapper);
                int i = 0;
                for (; i < 10; i++) {
                    if (windowList.get(i).getWindowStatus() == 0) {
                        one.setWindowNumber(windowList.get(i).getWindowNumber());
                        windowList.get(i).setWindowStatus(1);
                        break;
                    }
                }
                if (i == 10) {
                    return R.error("饭堂窗口不足，请稍后再试");
                }
                wrapper.eq("id", map.get("id")).set("status", 5).set("window_number", windowList.get(i).getWindowNumber());
                orderService.update(wrapper);
                canteenWindowService.updateBatchById(windowList);
                return R.success(null);
            }
            //再点完成空出窗口,用餐人数减一
            //@todo
            if (status.equals("6")){
                Integer windowNumber = one.getWindowNumber();
                UpdateWrapper<CanteenWindow> canteenWindowUpdateWrapper = new UpdateWrapper<>();
                canteenWindowUpdateWrapper.eq("canteen_id",canteenId).eq("window_number",windowNumber).set("window_status",0);
                canteenWindowService.update(canteenWindowUpdateWrapper);
            }
        }
        if (status.equals("6") || status.equals("4")) {
            Canteen canteen = canteenService.getById(canteenId);
            UpdateWrapper<Canteen> canteenUpdateWrapper = new UpdateWrapper<>();
            canteenUpdateWrapper.eq("id", canteenId).set("number", canteen.getNumber() - 1);
            canteenService.update(canteenUpdateWrapper);
        }
        wrapper.eq("id", map.get("id")).set("status", map.get("status"));
        orderService.update(wrapper);
        return R.success(null);
    }

    @GetMapping("/getOrderById")
    public R<OrdersDto> getOrderById(Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/changeDishRate")
    public Object changeDishRate(@RequestBody Map<String, String> map) {
        return orderService.changeDishRate(map);
    }

    @PostMapping("/submitComment")
    public Object submitComment(@RequestBody Map<String, String> map) {
        return orderService.commitComment(map.get("id"), map.get("comment"));
    }

}
