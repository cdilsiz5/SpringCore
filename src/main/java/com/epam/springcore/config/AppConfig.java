package com.epam.springcore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.springcore")
@PropertySource("classpath:application.properties")
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
     }
    @Bean
    public FilterRegistrationBean<ActiveUserFilter> activeUserFilter() {
        FilterRegistrationBean<ActiveUserFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ActiveUserFilter());
        registrationBean.addUrlPatterns("/api/epam/v1/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }


}
