package com.sadeghian.delivery.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeploymentController {

    @Value("${app.version:dev}")
    private String version;

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "application", "spring-k8s-delivery",
                "version", version,
                "status", "running"
        );
    }
}