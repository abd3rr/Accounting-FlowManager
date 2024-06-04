package fr.uha.AccountingFlowManager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/json/**").allowedOrigins("cdn.datatables.net/plug-ins/1.10.21/i18n/French.json");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
