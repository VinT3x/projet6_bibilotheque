package com.oc.projet3.biblioclient.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Permet d'éviter le problème des images qui ne s'affichent pas
 * No mapping for GET /images addResourceLocations
 * et permettre le chargement de bootstrap et jquerry
 * No mapping for GET /webjars/bootstrap/4.1.3/css/bootstrap.min.css
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/","classpath:/image/")
                .setCachePeriod(0);
    }

}
