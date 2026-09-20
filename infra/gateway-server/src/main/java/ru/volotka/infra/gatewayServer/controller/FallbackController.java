package ru.volotka.infra.gatewayServer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/user-service")
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        return ResponseEntity.status(503).body(Map.of(
                "status", "SERVICE_UNAVAILABLE",
                "message", "User Service временно недоступен. Попробуйте позже."
        ));
    }
}
