package com.mentorplatform.Backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/compiler")
public class CompilerController {

    @PostMapping("/run")
    public ResponseEntity<?> runDockerCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String language = request.getOrDefault("language", "java");
        Path tempDir = null; // Declare up here so we can clean it up in the 'finally' block

        try {
            tempDir = Files.createTempDirectory("docker-sandbox");
            Path sourceFile;
            ProcessBuilder pb;
            String hostPath = tempDir.toAbsolutePath().toString();

            switch (language.toLowerCase()) {
                case "javascript":
                    sourceFile = tempDir.resolve("script.js");
                    Files.writeString(sourceFile, code);
                    pb = new ProcessBuilder(
                            "docker", "run", "--rm",
                            "--memory=128m", "--cpus=0.5", "--network=none",
                            "-v", hostPath + ":/app", "-w", "/app",
                            "node:18-alpine", "node", "script.js"
                    );
                    break;
                case "python":
                    sourceFile = tempDir.resolve("script.py");
                    Files.writeString(sourceFile, code);
                    pb = new ProcessBuilder(
                            "docker", "run", "--rm",
                            "--memory=128m", "--cpus=0.5", "--network=none",
                            "-v", hostPath + ":/app", "-w", "/app",
                            "python:3.10-alpine", "python", "script.py"
                    );
                    break;
                case "java":
                default:
                    sourceFile = tempDir.resolve("Main.java");
                    Files.writeString(sourceFile, code);
                    pb = new ProcessBuilder(
                            "docker", "run", "--rm",
                            "--memory=128m", "--cpus=0.5", "--network=none",
                            "-v", hostPath + ":/app", "-w", "/app",
                            "eclipse-temurin:17-alpine", "sh", "-c", "javac Main.java && java Main"
                    );
                    break;
            }

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            boolean finished = process.waitFor(20, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return ResponseEntity.ok(Map.of("output", "Timeout Error: Code execution exceeded 20 seconds. Check for infinite loops!"));
            }

            return ResponseEntity.ok(Map.of("output", output.toString()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("output", "Backend Orchestration Error: " + e.getMessage()));
        } finally {
            // ALWAYS run this, even if the code crashes halfway through!
            if (tempDir != null) {
                cleanUpSandbox(tempDir);
            }
        }
    }

    // --- NEW: Aggressive Cleanup Helper ---
    private void cleanUpSandbox(Path tempDir) {
        File dir = tempDir.toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete(); // This safely deletes Main.java AND Main.class!
                }
            }
            dir.delete(); // Now the empty folder can be safely deleted
        }
    }
}