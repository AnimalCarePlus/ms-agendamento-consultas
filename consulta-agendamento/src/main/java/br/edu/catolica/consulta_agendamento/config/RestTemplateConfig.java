package br.edu.catolica.consulta_agendamento.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate authRestTemplate(RestTemplateBuilder builder,
            @Value("${security.auth-service.timeout-ms:3000}") long timeout) {
        Duration duration = Duration.ofMillis(timeout);
        return builder
                .setConnectTimeout(duration)
                .setReadTimeout(duration)
                .build();
    }
}
