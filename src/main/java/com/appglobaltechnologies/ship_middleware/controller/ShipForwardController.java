package com.appglobaltechnologies.ship_middleware.controller;

import com.appglobaltechnologies.ship_middleware.transport.ShipTransportService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipForwardController {

    private final ShipTransportService transportService;
    private final ObjectMapper objectMapper;

    @PostMapping(
            value = "/forward",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> forwardToShip(
            @RequestBody String payload
    ) {

        try {

            JsonNode json =
                    objectMapper.readTree(payload);

            String correlationId = null;

            if (json.has("correlationId")) {

                correlationId =
                        json.get("correlationId")
                                .asText();
            }

            if (correlationId == null
                    || correlationId.isBlank()) {

                correlationId =
                        UUID.randomUUID()
                                .toString();
            }

            log.info(
                    "Forwarding to SHIP - correlationId: {}",
                    correlationId
            );

            String response =
                    transportService.postResource(
                            payload,
                            correlationId
                    );

            log.info(
                    "SHIP response: {}",
                    response
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            log.error(
                    "SHIP forwarding failed",
                    ex
            );

            String errorOutcome = """
            {
              "resourceType":"OperationOutcome",
              "issue":[{
                "severity":"error",
                "code":"exception",
                "diagnostics":"%s"
              }]
            }
            """.formatted(
                    ex.getMessage()
                            .replace("\"", "'")
            );

            return ResponseEntity
                    .internalServerError()
                    .body(errorOutcome);
        }
    }
}