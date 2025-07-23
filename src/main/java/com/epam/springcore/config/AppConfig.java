package com.epam.springcore.config;

import com.epam.springcore.session.UserSessionRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.epam.springcore")
@PropertySource("classpath:application.properties")
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private UserSessionRegistry sessionRegistry;

    @PostConstruct
    public void initFilter() {
        ActiveUserFilter.setUserSessionRegistry(sessionRegistry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");

        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
