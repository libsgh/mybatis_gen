package com.libsgh.mybatis.gen;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.libsgh.mybatis.gen.model.GenConfig;
import com.libsgh.mybatis.gen.service.GenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class App {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassname;

    @Bean
    @Primary
    public DruidDataSource getDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(dbUrl);
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassname);
        return ds;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
