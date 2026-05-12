package com.appglobaltechnologies.ship_middleware.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.appglobaltechnologies.ship_middleware.config.ShipProperties;
import com.appglobaltechnologies.ship_middleware.security.ShipTokenService;
import com.appglobaltechnologies.ship_middleware.security.ShipHmacService;
import com.appglobaltechnologies.ship_middleware.security.NonceUtil;
import com.appglobaltechnologies.ship_middleware.security.TimestampUtil;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ShipTransportService {
    private final ShipProperties properties;
    private final ShipTokenService tokenService;
    private final ShipHmacService hmacService;

    private final WebClient webClient =
            WebClient.builder().build();

    public String postResource(
            String payload,
            String correlationId
    ) {

        String token = tokenService.getToken();

        String timestamp =
                TimestampUtil.unixTimestamp();

        String nonce =
                NonceUtil.generateNonce();

        String signature =
                hmacService.computeSignature(
                        "POST",
                        properties.getShipPath(),
                        "",
                        payload,
                        timestamp,
                        nonce,
                        properties.getHmacClientId(),
                        properties.getHmacSecret()
                );

        String sigHeader =
                "kid=" + properties.getHmacClientId()
                        + ";alg=HMAC-SHA256;sig=" + signature;

        return webClient.post()
                .uri(properties.getBaseUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .header("correlationId", correlationId)
                .header("X-SHIP-Date", timestamp)
                .header("X-SHIP-Nonce", nonce)
                .header("X-SHIP-Signature", sigHeader)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
