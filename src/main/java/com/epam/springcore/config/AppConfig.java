package com.epam.springcore.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.springcore")
@PropertySource("classpath:application.properties")
public class AppConfig implements WebMvcConfigurer {



}
