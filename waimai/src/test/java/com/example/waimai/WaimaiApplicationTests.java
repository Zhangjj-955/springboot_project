package com.example.waimai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class WaimaiApplicationTests {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    MailProperties mailProperties;
    @Test
    public void send(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo("1954204887@qq.com");
        message.setSubject("testMessage");
        message.setText("testMessage");
        javaMailSender.send(message);
    }
}
