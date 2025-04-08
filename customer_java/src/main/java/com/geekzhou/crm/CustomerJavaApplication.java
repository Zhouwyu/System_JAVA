package com.geekzhou.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.geekzhou.crm.mapper.mybatis")
public class CustomerJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerJavaApplication.class, args);
	}

}
