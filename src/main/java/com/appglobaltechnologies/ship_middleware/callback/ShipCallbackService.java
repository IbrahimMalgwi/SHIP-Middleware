package com.appglobaltechnologies.ship_middleware.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.appglobaltechnologies.ship_middleware.config.ShipProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ShipCallbackService {
    private final ShipProperties properties;

    private final ObjectMapper mapper = new ObjectMapper();

    public String processCallback(
            String payload,
            String signatureHeader,
            String timestamp,
            String nonce,
            String clientId
    ) {

        try {

            String signature = extractSignature(signatureHeader);

            boolean valid = verifyHmac(
                    "POST",
                    properties.getCallbackPath(),
                    "",
                    payload,
                    timestamp,
                    nonce,
                    clientId,
                    signature,
                    properties.getHmacSecret()
            );

            JsonNode json = mapper.readTree(payload);

            String transactionId = getValue(json, "transactionId");
            String shipId = getValue(json, "shipId");
            String status = getValue(json, "status");
            String message = getValue(json, "message");
            String correlationId = getValue(json, "correlationId");

            System.out.println("========== SHIP CALLBACK ==========");
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("SHIP ID: " + shipId);
            System.out.println("Status: " + status);
            System.out.println("Message: " + message);
            System.out.println("Correlation ID: " + correlationId);
            System.out.println("Signature Valid: " + valid);
            System.out.println("===================================");

            if (!valid) {
                return operationOutcome(
                        "error",
                        "security",
                        "Invalid signature"
                );
            }

            return operationOutcome(
                    "information",
                    "informational",
                    "Callback processed successfully"
            );

        } catch (Exception ex) {

            return operationOutcome(
                    "error",
                    "exception",
                    ex.getMessage()
            );
        }
    }

    private boolean verifyHmac(
            String method,
            String path,
            String query,
            String payload,
            String timestamp,
            String nonce,
            String clientId,
            String incomingSignature,
            String secret
    ) throws Exception {

        String computed = computeSignature(
                method,
                path,
                query,
                payload,
                timestamp,
                nonce,
                clientId,
                secret
        );

        return computed.trim().equals(incomingSignature.trim());
    }

    private String computeSignature(
            String method,
            String path,
            String query,
            String payload,
            String timestamp,
            String nonce,
            String clientId,
            String secret
    ) throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] bodyHash = digest.digest(
                payload.getBytes(StandardCharsets.UTF_8)
        );

        String bodyHashBase64 = Base64.getEncoder()
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

        SecretKeySpec secretKeySpec =
                new SecretKeySpec(
                        secret.getBytes(StandardCharsets.UTF_8),
                        "HmacSHA256"
                );

        mac.init(secretKeySpec);

        byte[] hmacBytes = mac.doFinal(
                canonical.getBytes(StandardCharsets.UTF_8)
        );

        return Base64.getEncoder()
                .encodeToString(hmacBytes);
    }

    private String extractSignature(String header) {

        if (header == null) {
            return "";
        }

        String[] parts = header.split(";");

        for (String part : parts) {

            if (part.startsWith("sig=")) {
                return part.replace("sig=", "").trim();
            }
        }

        return "";
    }

    private String getValue(JsonNode node, String field) {

        JsonNode value = node.get(field);

        return value != null ? value.asText() : null;
    }

    private String operationOutcome(
            String severity,
            String code,
            String diagnostics
    ) {

        return """
        {
          "resourceType":"OperationOutcome",
          "issue":[
            {
              "severity":"%s",
              "code":"%s",
              "diagnostics":"%s"
            }
          ]
        }
        """.formatted(severity, code, diagnostics);
    }

}
