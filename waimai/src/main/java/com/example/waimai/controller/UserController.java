package com.example.waimai.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.waimai.common.R;
import com.example.waimai.entity.User;
import com.example.waimai.service.UserService;
import com.example.waimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    JavaMailSender mailSender;
    @Autowired
    MailProperties mailProperties;

    /**
     * 发送手机短信验证码
     *
     * @param
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
//        String mail = (String) map.get("mail");
//        if (StringUtils.isNotEmpty(mail)) {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(mailProperties.getUsername());
//            message.setTo(mail);
//            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            message.setSubject("验证码");
//            message.setText("验证码为:" + code + ",有效时间五分钟");
//            mailSender.send(message);
//            stringRedisTemplate.opsForValue().set(mail, code, 5, TimeUnit.MINUTES);
//            return R.success("手机验证码短信发送成功");
//        }


        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}", code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Session
            //session.setAttribute(phone,code);

//            将验证码保存在redis里，定时五分钟
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param
     * @return
     */
//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map, HttpSession session) {
//
//        //获取手机号
//        String phone = map.get("phone").toString();
//
//        //获取验证码
//        String code = map.get("code").toString();
//
//        //从Session中获取保存的验证码
//        //Object codeInSession = session.getAttribute(phone);
//
//        //从redis里取验证码
////        Object codeInSession = stringRedisTemplate.opsForValue().get(phone);
//
//        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
////        if (codeInSession != null && codeInSession.equals(code)) {
//            //如果能够比对成功，说明登录成功
//
////            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
////            queryWrapper.eq(User::getPhone, phone);
////
////            User user = userService.getOne(queryWrapper);
////            if (user == null) {
////                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
////                user = new User();
////                user.setPhone(phone);
//////                user.set
////                userService.save(user);
////            }
//            session.setAttribute("user", Long.parseLong(phone));
////
////            //登陆成功，删除redis中的验证码
////            stringRedisTemplate.delete(phone);
//            return R.success(new User());
//        }
//        return R.error("登录失败");
//    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpServletRequest request) {
        String account = map.get("account");
        String password = map.get("pwd");
//        String password = DigestUtils.md5DigestAsHex("200538".getBytes());
        User user = userService.getOne(new QueryWrapper<User>().eq("account", account).eq("password", password));
        if (user != null) {
            String sessionId = request.getSession().getId();
            request.getSession().setAttribute("user", user.getId());
            return R.success(user).add("sessionId", sessionId);
        } else {
            return R.error("账号或密码错误");
        }
    }

    @RequestMapping("/page")
    public R<Page> page(int page, int pageSize, @RequestParam(required = false) String name) {
        Page<User> page1 = new Page<>(page, pageSize);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like(!(name == null), "name", name);
        userService.page(page1, wrapper);
        return R.success(page1);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        return R.success(null);
    }
    @PostMapping("/recharge")
    public Object recharge(@RequestBody Map<String,Long> map){
        float balance = userService.getById(map.get("userId")).getBalance();
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",map.get("userId")).set("balance",balance+10.0);
        userService.update(wrapper);
        return R.success(null);
    }

    @PostMapping("/register")
    public Object save(@RequestBody User user){

        if (userService.save(user)){
            return R.success(null);
        } else {
            return R.error("账号已注册");
        }

    }
}
