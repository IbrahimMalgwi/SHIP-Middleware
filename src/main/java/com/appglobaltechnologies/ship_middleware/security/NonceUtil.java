package com.appglobaltechnologies.ship_middleware.security;
import java.util.UUID;

public class NonceUtil {
    public static String generateNonce() {
        return UUID.randomUUID()
                .toString()
                .toLowerCase();
    }
}
