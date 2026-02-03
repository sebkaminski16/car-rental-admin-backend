package io.github.sebkaminski16.carrentaladmin.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    //added because front end was blocking requests
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        //allowing ANY port for now (because Vite chooses port randomly for some reason)
                        .allowedOriginPatterns(
                                "http://127.0.0.1:*",
                                "http://localhost:*"
                        )
                        .allowCredentials(true)
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}
