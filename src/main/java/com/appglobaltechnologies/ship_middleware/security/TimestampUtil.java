package com.appglobaltechnologies.ship_middleware.security;

import java.time.Instant;

public class TimestampUtil {
    public static String unixTimestamp() {
        return String.valueOf(
                Instant.now().getEpochSecond()
        );
    }
}
