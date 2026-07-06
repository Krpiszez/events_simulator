package com.example.rokevents.controller;

import com.example.rokevents.service.FileProcessingService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file-processing")
@CrossOrigin(origins = "*")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "rokevents-upload";

    public FileProcessingController() {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    @PostMapping("/screenshot-merger")
    public ResponseEntity<?> processScreenshotMerger(
            @RequestParam("detailsFile") MultipartFile detailsFile,
            @RequestParam("screenshotZip") MultipartFile screenshotZip,
            @RequestParam(value = "tessDataPath", required = false) String tessDataPath) {
        try {
            String sessionId = UUID.randomUUID().toString();

            // Save uploaded files temporarily
            String detailsPath = saveUploadedFile(detailsFile, sessionId, "details.csv");

            // Extract screenshots
            String screenshotsDir = TEMP_DIR + File.separator + sessionId + File.separator + "screenshots";
            File screenshotsDirFile = new File(screenshotsDir);
            screenshotsDirFile.mkdirs();

            // Handle zip extraction or direct image files
            if (screenshotZip.getOriginalFilename().endsWith(".zip")) {
                extractZipFile(screenshotZip, screenshotsDir);
            } else {
                saveUploadedFile(screenshotZip, sessionId + File.separator + "screenshots", screenshotZip.getOriginalFilename());
            }

            // Process with screenshot merger
            FileProcessingService.FileProcessingResult result = fileProcessingService.processWithScreenshotMerger(
                    detailsPath, screenshotsDir, tessDataPath);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", result.getSessionId());
            response.put("processingType", result.getProcessingType());
            response.put("content", result.getContent());
            response.put("message", "File processing completed successfully");

            return ResponseEntity.ok(response);

        } catch (TesseractException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "OCR processing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/kvk-calculator")
    public ResponseEntity<?> processKVKCalculator(
            @RequestParam("detailsFile") MultipartFile detailsFile,
            @RequestParam("scoresFile") MultipartFile scoresFile) {
        try {
            String sessionId = UUID.randomUUID().toString();

            // Save uploaded files temporarily
            String detailsPath = saveUploadedFile(detailsFile, sessionId, "details.csv");
            String scoresPath = saveUploadedFile(scoresFile, sessionId, "scores.csv");

            // Process with score calculator
            FileProcessingService.FileProcessingResult result = fileProcessingService.processWithScoreCalculator(
                    detailsPath, scoresPath);

            Map<String, Object> response = new HashMap<>();
            response.put("sessionId", result.getSessionId());
            response.put("processingType", result.getProcessingType());
            response.put("content", result.getContent());
            response.put("message", "File processing completed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/download/{sessionId}")
    public ResponseEntity<?> downloadFile(@PathVariable String sessionId) {
        try {
            byte[] fileContent = fileProcessingService.downloadFile(sessionId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output_" + sessionId + ".csv\"")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .body(fileContent);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File not found: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanup() {
        try {
            fileProcessingService.cleanupOldFiles();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cleanup completed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    private String saveUploadedFile(MultipartFile file, String sessionId, String filename) throws IOException {
        String dirPath = TEMP_DIR + File.separator + sessionId;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Path filePath = Paths.get(dirPath, filename);
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }

    private void extractZipFile(MultipartFile zipFile, String outputDir) throws IOException {
        Path outputPath = Paths.get(outputDir);
        Files.createDirectories(outputPath);

        byte[] bytes = zipFile.getBytes();
        Path zipPath = Paths.get(System.getProperty("java.io.tmpdir"), "temp_" + UUID.randomUUID() + ".zip");
        Files.write(zipPath, bytes);

        // Use Java's ZipFile to extract
        try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipPath.toFile())) {
            zip.stream().forEach(entry -> {
                try {
                    Path entryPath = outputPath.resolve(entry.getName());
                    if (entry.isDirectory()) {
                        Files.createDirectories(entryPath);
                    } else {
                        Files.createDirectories(entryPath.getParent());
                        try (java.io.InputStream in = zip.getInputStream(entry)) {
                            Files.copy(in, entryPath);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            Files.deleteIfExists(zipPath);
        }
    }
}
