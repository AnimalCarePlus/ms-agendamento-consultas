package br.edu.catolica.consulta_agendamento.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.edu.catolica.consulta_agendamento.exception.UnauthorizedException;

@Service
public class LocalAuthService {

    private final JwtService jwtService;
    private final String expectedUsername;
    private final String expectedPassword;

    public LocalAuthService(JwtService jwtService,
            @Value("${security.local-user.username}") String expectedUsername,
            @Value("${security.local-user.password}") String expectedPassword) {
        this.jwtService = jwtService;
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    public String authenticate(String username, String password) {
        if (!expectedUsername.equals(username) || !expectedPassword.equals(password)) {
            throw new UnauthorizedException("Credenciais invalidas");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "LOCAL_USER");
        return jwtService.generateToken(username, claims);
    }
}
