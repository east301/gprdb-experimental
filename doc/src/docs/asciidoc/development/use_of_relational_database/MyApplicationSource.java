package com.example.my.pkg;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackage = "com.example.my.pkg")
@EntityScan(basePackage = "com.example.my.pkg")
@EnableJpaRepositories(basePackages = "com.example.my.pkg")
public class MyApplicationSource {
    // empty
}
