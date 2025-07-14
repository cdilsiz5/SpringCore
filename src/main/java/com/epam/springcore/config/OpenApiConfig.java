package com.epam.springcore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.title}")
    private String title;

    @Value("${openapi.description}")
    private String description;

    @Value("${openapi.version}")
    private String version;

    @Value("${openapi.license.name}")
    private String licenseName;

    @Value("${openapi.license.url}")
    private String licenseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .license(new License().name(licenseName).url(licenseUrl)));
    }
}