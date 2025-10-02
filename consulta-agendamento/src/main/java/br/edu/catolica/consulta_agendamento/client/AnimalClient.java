package br.edu.catolica.consulta_agendamento.client;

import java.time.Duration;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AnimalClient {

    private final WebClient webClient;

    public AnimalClient(@Qualifier("animalWebClient") WebClient webClient) {
        this.webClient = webClient;
    }


    public boolean animalExiste(Long animalId) {
        try {

            HttpServletRequest currentRequest =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = currentRequest.getHeader("Authorization");


            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length()).trim();
            }


            System.out.println("AnimalClient: chamando serviço de animais para ID=" + animalId + " com token=" + token);


            Map<String, Object> response = webClient.get()
                    .uri("/animais/{id}/exists", animalId)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();


            return response != null && Boolean.TRUE.equals(response.get("exists"));

        } catch (WebClientResponseException e) {
            int status = e.getStatusCode().value();
            System.err.println("AnimalClient: resposta HTTP do serviço de animais: " + status);
            if (status == 403 || status == 404) {
                return false;
            }
            throw new RuntimeException("Erro HTTP ao chamar serviço de animais", e);
        } catch (Exception e) {
            System.err.println("AnimalClient: erro ao chamar serviço de animais: " + e.getMessage());
            throw new RuntimeException("Erro ao chamar serviço de animais", e);
        }
    }
}