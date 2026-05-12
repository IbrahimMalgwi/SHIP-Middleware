package com.appglobaltechnologies.ship_middleware.callback;

import com.appglobaltechnologies.ship_middleware.security.ShipTokenService;
import com.appglobaltechnologies.ship_middleware.transport.ShipTransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final ShipTokenService tokenService;
    private final ShipTransportService transportService;

    @GetMapping("/token")
    public String token() {
        return tokenService.getToken();
    }

    @PostMapping("/send")
    public String send(@RequestBody String payload) {

        return transportService.postResource(
                payload,
                "123456"
        );
    }
}
