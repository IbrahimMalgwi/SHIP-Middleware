package com.appglobaltechnologies.ship_middleware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ship")
public class ShipProperties {
    private String baseUrl;
    private String tokenUrl;

    private String clientId;
    private String clientSecret;

    private String hmacClientId;
    private String hmacSecret;

    private String callbackUrl;

    private String facilityId;
    private String organisationId;

    private String shipPath;

    private String callbackPath;
}
