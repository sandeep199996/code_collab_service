package com.mentorplatform.Backend.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compiler")
public class CompilerController {

    @PostMapping("/run")
    public ResponseEntity<?> runCodeProxy(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        String language = (String) request.get("language");

        String version = "15.0.2";
        if ("python".equals(language)) version = "3.10.0";
        if ("javascript".equals(language)) version = "18.15.0";

        // 1. Let Spring Boot handle the JSON formatting!
        Map<String, Object> pistonPayload = new HashMap<>();
        pistonPayload.put("language", language);
        pistonPayload.put("version", version);

        // Piston expects an array of files
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("content", code);
        pistonPayload.put("files", List.of(fileMap));

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // We pass the Map directly. Spring automatically safely converts it to JSON.
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pistonPayload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://emkc.org/api/v2/piston/execute",
                    entity,
                    Map.class
            );

            Map<String, Object> runData = (Map<String, Object>) response.getBody().get("run");
            String output = (String) runData.get("output");

            return ResponseEntity.ok(Map.of("output", output));

        } catch (HttpClientErrorException e) {
            // This catches 400 Bad Request or 401 Unauthorized from Piston
            System.err.println("Piston API Error: " + e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of("output", "API Error: " + e.getStatusCode()));
        } catch (Exception e) {
            // This catches total network failures
            System.err.println("Total Execution Failure: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("output", "Execution Server Error: Check your Spring Boot console."));
        }
    }
}