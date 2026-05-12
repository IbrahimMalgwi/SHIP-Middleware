package com.appglobaltechnologies.ship_middleware.callback;

import lombok.extern.slf4j.Slf4j;
import com.appglobaltechnologies.ship_middleware.callback.ShipCallbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ship")
@RequiredArgsConstructor
@Slf4j
public class ShipCallbackController {
    private final ShipCallbackService callbackService;

    @PostMapping(
            value = "/callback",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String callback(
            @RequestBody String payload,

            @RequestHeader(value = "X-SHIP-Signature", required = false)
            String signature,

            @RequestHeader(value = "X-SHIP-Date", required = false)
            String timestamp,

            @RequestHeader(value = "X-SHIP-Nonce", required = false)
            String nonce,

            @RequestHeader(value = "X-SHIP-ClientId", required = false)
            String clientId
    ) {

        return callbackService.processCallback(
                payload,
                signature,
                timestamp,
                nonce,
                clientId
        );
    }
}
