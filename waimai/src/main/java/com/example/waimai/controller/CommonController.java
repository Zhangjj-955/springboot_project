package com.example.waimai.controller;

import com.example.waimai.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${waimai.path}")    //取出配置文件里的值
    String basePath ;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID()+suffix;
        try {
            file.transferTo(new File(basePath+ fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }
    @GetMapping("download")
    public void download(String name, HttpServletResponse response){
        try {
            FileInputStream is = new FileInputStream(basePath+name);
            OutputStream os = response.getOutputStream();
            response.setContentType("img/jpeg");
            byte[] buf = new byte[1024];
            while (!(is.read(buf)==-1)){
                os.write(buf);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
