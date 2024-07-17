package com.oliveiradev.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.oliveiradev.serialization.converter.YamlJackson2HttpMesageConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

    @Value("${cors.originPatterns:default}")
    private String corsOriginPatterns = "";


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJackson2HttpMesageConverter());
    }    

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allowedOrgins = corsOriginPatterns.split(",");
        registry.addMapping("/**")
        //.allowedMethods("GET", "POST", "PUT") -- AQUI PERMITIMOS APENAS ESSES METODOS
        .allowedMethods("*") // TODOS OS METODOS LIBERADOS >> *
        .allowedOriginPatterns(allowedOrgins)
    .allowCredentials(true);

    }

    @SuppressWarnings("null")
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        
        /*
        // via QUERY PARAM. http://localhost:8080/api/person/v1?mediaType=xml
        
        configurer.favorParameter(true)
            .parameterName("mediatype")
            .ignoreAcceptHeader(true)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("x-yaml, MEDIA_TYPE_APPLICATION_YML"); 
            */

        // via HEADER PARAM.  http://localhost:8080/api/person/v1
        configurer.favorParameter(false)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON)
            .mediaType("json", MediaType.APPLICATION_JSON)
            .mediaType("xml", MediaType.APPLICATION_XML)
            .mediaType("x-yaml", MEDIA_TYPE_APPLICATION_YML
        );
    }
}