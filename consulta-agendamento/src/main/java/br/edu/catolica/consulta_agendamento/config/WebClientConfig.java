package br.edu.catolica.consulta_agendamento.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${animal.service.base-url}")
    private String animalServiceBaseUrl;

    @Value("${auth.service.base-url}")
    private String authServiceBaseUrl;

    @Bean(name = "animalWebClient")
    public WebClient animalWebClient() {
        return WebClient.builder()
                .baseUrl(animalServiceBaseUrl)
                .build();
    }

    @Bean(name = "authWebClient")
    public WebClient authWebClient() {
        return WebClient.builder()
                .baseUrl(authServiceBaseUrl)
                .build();
    }
}