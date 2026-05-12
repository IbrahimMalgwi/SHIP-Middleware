package com.appglobaltechnologies.ship_middleware.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ShipHmacService {
    public String computeSignature(
            String method,
            String path,
            String query,
            String payload,
            String timestamp,
            String nonce,
            String clientId,
            String secret
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] bodyHash =
                    digest.digest(
                            payload.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            String bodyHashBase64 =
                    Base64.getEncoder()
                            .encodeToString(bodyHash);

            String canonical =
                    method.toUpperCase() + "\n" +
                            path + "\n" +
                            (query == null ? "" : query) + "\n" +
                            bodyHashBase64 + "\n" +
                            timestamp + "\n" +
                            nonce + "\n" +
                            clientId;

            Mac mac = Mac.getInstance("HmacSHA256");

            mac.init(
                    new SecretKeySpec(
                            secret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    )
            );

            byte[] hmac =
                    mac.doFinal(
                            canonical.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getEncoder()
                    .encodeToString(hmac);

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Unable to compute HMAC signature",
                    ex
            );
        }
    }
}
