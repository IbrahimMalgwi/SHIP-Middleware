package com.appglobaltechnologies.ship_middleware.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.appglobaltechnologies.ship_middleware.config.ShipProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipTokenService {
    private final ShipProperties properties;

    private final WebClient webClient =
            WebClient.builder().build();

    public String getToken() {

        Map<String, String> body = Map.of(
                "clientId", properties.getClientId(),
                "clientSecret", properties.getClientSecret(),
                "grantType", "client_credentials",
                "scope", "ship-full-access"
        );

        TokenResponse response =
                webClient.post()
                        .uri(properties.getTokenUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(TokenResponse.class)
                        .block();

        if (response == null
                || response.getData() == null
                || response.getData().getAccessToken() == null) {

            throw new RuntimeException(
                    "SHIP token not found");
        }

        return response.getData().getAccessToken();
    }

    @Data
    public static class TokenResponse {

        private TokenData data;
    }

    @Data
    public static class TokenData {

        @JsonProperty("access_token")
        private String accessToken;
    }
}
