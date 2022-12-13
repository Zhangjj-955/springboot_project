package com.example.waimai.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.waimai.common.R;
import com.example.waimai.entity.User;
import com.example.waimai.service.UserService;
import com.example.waimai.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
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
     * @param mail
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Map map){
        String mail = (String) map.get("mail");
        if (StringUtils.isNotEmpty(mail)){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getUsername());
            message.setTo(mail);
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            message.setSubject("验证码");
            message.setText("验证码为:"+code+",有效时间五分钟");
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(mail,code,5,TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }


//        //获取手机号
//        String phone = user.getPhone();
//
//        if(StringUtils.isNotEmpty(phone)){
//            //生成随机的4位验证码
//            String code = ValidateCodeUtils.generateValidateCode(4).toString();
//            log.info("code={}",code);
//
//            //调用阿里云提供的短信服务API完成发送短信
//            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
//
//            //需要将生成的验证码保存到Session
//            //session.setAttribute(phone,code);
//
////            将验证码保存在redis里，定时五分钟
//            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
//            return R.success("手机验证码短信发送成功");
//        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从redis里取验证码
        Object codeInSession = stringRedisTemplate.opsForValue().get(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //登陆成功，删除redis中的验证码
            stringRedisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

}
