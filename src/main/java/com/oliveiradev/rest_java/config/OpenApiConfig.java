package com.oliveiradev.rest_java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RestFull API with Java 18")
                .version("v1")
                .description("API with SpringBoot")
                .termsOfService("")
                .license(
                    new License().name("Apache 2.0")
                    .url("")));
    }
}
