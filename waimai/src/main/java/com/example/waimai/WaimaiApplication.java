package com.example.waimai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
//@MapperScan("com.example.waimai.mapper")  //在这个路径下的接口都生成mapper实现类，不标@Mapper也行
@ServletComponentScan   //@ComponentScan只能扫描@Controller，@Service，@Repository，@Component，其他的如@WebFilter不能扫描
@SpringBootApplication
@EnableTransactionManagement
public class WaimaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaimaiApplication.class, args);
    }

}
