package br.edu.catolica.consulta_agendamento.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Agendamento de Consultas")
                        .description("Microsservico responsavel pelo agendamento de consultas veterinarias")
                        .version("1.0.0")
                        .license(new License().name("MIT")));
    }
}
