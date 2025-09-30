package br.edu.catolica.consulta_agendamento.service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenValidationService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String validationPath;
    private final HttpMethod httpMethod;

    public TokenValidationService(
            RestTemplate restTemplate,
            @Value("${security.auth-service.base-url:http://localhost:8085}") String baseUrl,
            @Value("${security.auth-service.token-validation-path:/api/v1/auth/validate-token/}") String validationPath,
            @Value("${security.auth-service.method:POST}") String method) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.validationPath = validationPath;
        this.httpMethod = HttpMethod.valueOf(method.toUpperCase());
    }

    public Optional<String> validate(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return Optional.empty();
        }

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path(validationPath)
                    .build()
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

            ResponseEntity<Map> response = restTemplate.exchange(url, httpMethod, new HttpEntity<>(headers), Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map body = response.getBody();
                if (body != null) {
                    Object principal = body.getOrDefault("username", body.getOrDefault("sub", body.get("user")));
                    if (principal != null) {
                        return Optional.of(principal.toString());
                    }
                }
                return Optional.of(authorizationHeader.substring("Bearer ".length()));
            }
        } catch (HttpStatusCodeException ex) {
            log.warn("Token validation rejected by auth service: {}", ex.getStatusCode());
        } catch (Exception ex) {
            log.error("Token validation failed", ex);
        }
        return Optional.empty();
    }
}


