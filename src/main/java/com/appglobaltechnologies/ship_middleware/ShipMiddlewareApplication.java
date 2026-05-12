package com.appglobaltechnologies.ship_middleware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ShipMiddlewareApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipMiddlewareApplication.class, args);
	}

}
