package com.example.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.waimai.common.R;
import com.example.waimai.entity.Employee;
import com.example.waimai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    /**
     * 根据id查employee，提供给修改employee的页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Object queryEmployeeById(@PathVariable String id){
        QueryWrapper<Employee> wrapper = new QueryWrapper<Employee>();
        wrapper.eq("id",id);
        Employee employee = service.getOne(wrapper);
        return R.success(employee);
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @param name  根据条件查询，可以为空
     * @return
     */
    @RequestMapping("/page")
    public R<Page> page(int page,int pageSize,@RequestParam(required = false) String name){
        Page<Employee> page1 = new Page<>(page,pageSize);
        QueryWrapper<Employee> wrapper = new QueryWrapper<Employee>();
        wrapper.like(!(name == null),"name",name).orderByDesc("create_time");
        service.page(page1,wrapper);
        return R.success(page1);
    }

    /**
     * 禁用或启用employee，修改employee，
     * 同一个路径处理
     * @param employee
     * @param session
     * @return
     */
    @PutMapping
    public Object updateEmployee(@RequestBody Employee employee,HttpSession session){
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((Long) session.getAttribute("employee"));
        service.updateById(employee);
        return R.success(null);
    }

    /**
     * 保存创建的employee
     * @param employee
     * @param session
     * @return
     */
    @PostMapping
    public Object save(@RequestBody Employee employee,HttpSession session){
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateUser((Long) session.getAttribute("employee"));
        employee.setUpdateUser((Long) session.getAttribute("employee"));

        //主键由mybatis-plus默认用雪花算法生成
        boolean result = service.save(employee);
        if (!result){
            return R.error(null);
        }else {
            return R.success(null);
        }
    }
    @PostMapping("/login")
    public Object login(@RequestBody Map<String,String> map, HttpSession session){
        log.info("进入/login");
        String password = DigestUtils.md5DigestAsHex(map.get("password").getBytes());
        Employee employee = service.getOne(new QueryWrapper<Employee>().eq("username",map.get("username")).eq("password",password));
        if (employee!=null){
            session.setAttribute("employee",employee.getId());      //filter会根据"employee"判断是否已登陆
            return R.success(employee);
        }else {
            return R.error("账号或密码错误");
        }
    }
    @RequestMapping("/logout")
    public Object logout(HttpSession session){
        log.info("进入/logout");
        session.removeAttribute("employee");
        return R.success(null);
    }
    @DeleteMapping
    public Object delete(@RequestBody Employee employee){
        boolean result = service.removeById(employee);
        if (result){
            return R.success(null);
        }else {
            return R.error("删除失败");
        }
    }
}
