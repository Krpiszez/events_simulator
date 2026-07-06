package com.example.rokevents.service;

import com.example.rokevents.util.PreKVKScoreCalculator;
import com.example.rokevents.util.ScreenshotScoreMerger;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileProcessingService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "rokevents";
    private Tesseract tesseract;

    public FileProcessingService() {
        initTempDirectory();
        initTesseract();
    }

    private void initTempDirectory() {
        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    private void initTesseract() {
        try {
            this.tesseract = new Tesseract();
            String tessDataPath = System.getenv("TESSDATA_PREFIX");
            if (tessDataPath != null) {
                tesseract.setDatapath(tessDataPath);
            } else {
                tesseract.setDatapath("C:\\rok\\tessdata\\");
            }
            tesseract.setLanguage("eng");
        } catch (Exception e) {
            // Tesseract initialization failed, will be handled when processing screenshots
            this.tesseract = null;
        }
    }

    public FileProcessingResult processWithScreenshotMerger(String detailsFilePath, String screenshotsDir, String tessDataPath) throws IOException, TesseractException {
        String sessionId = UUID.randomUUID().toString();
        String outputFilePath = TEMP_DIR + File.separator + sessionId + "_screenshot_output.csv";

        if (tesseract == null && tessDataPath == null) {
            throw new IllegalArgumentException("Tesseract not configured. Please provide tessDataPath or set TESSDATA_PREFIX environment variable");
        }

        Tesseract tessToUse = tesseract;
        if (tessDataPath != null) {
            tessToUse = new Tesseract();
            tessToUse.setDatapath(tessDataPath);
            tessToUse.setLanguage("eng");
        }

        String result = ScreenshotScoreMerger.processScreenshots(detailsFilePath, screenshotsDir, outputFilePath, tessDataPath, tessToUse);

        String csvContent = readFileContent(result);
        return new FileProcessingResult(sessionId, outputFilePath, csvContent, "Screenshot Score Merger");
    }

    public FileProcessingResult processWithScoreCalculator(String detailsFilePath, String scoresFilePath) throws IOException {
        String sessionId = UUID.randomUUID().toString();
        String outputFilePath = TEMP_DIR + File.separator + sessionId + "_kvk_output.csv";

        String result = PreKVKScoreCalculator.calculateScores(detailsFilePath, scoresFilePath, outputFilePath);

        String csvContent = readFileContent(result);
        return new FileProcessingResult(sessionId, outputFilePath, csvContent, "Pre-KVK Score Calculator");
    }

    public String getFileContent(String sessionId) throws IOException {
        File[] files = new File(TEMP_DIR).listFiles((dir, name) -> name.startsWith(sessionId));
        if (files != null && files.length > 0) {
            return readFileContent(files[0].getAbsolutePath());
        }
        throw new IOException("File not found for session: " + sessionId);
    }

    public byte[] downloadFile(String sessionId) throws IOException {
        File[] files = new File(TEMP_DIR).listFiles((dir, name) -> name.startsWith(sessionId));
        if (files != null && files.length > 0) {
            return Files.readAllBytes(files[0].toPath());
        }
        throw new IOException("File not found for session: " + sessionId);
    }

    public void cleanupOldFiles() {
        File tempDir = new File(TEMP_DIR);
        if (tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                long currentTime = System.currentTimeMillis();
                for (File file : files) {
                    if (currentTime - file.lastModified() > 3600000) { // 1 hour
                        file.delete();
                    }
                }
            }
        }
    }

    private String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static class FileProcessingResult {
        public String sessionId;
        public String filePath;
        public String content;
        public String processingType;

        public FileProcessingResult(String sessionId, String filePath, String content, String processingType) {
            this.sessionId = sessionId;
            this.filePath = filePath;
            this.content = content;
            this.processingType = processingType;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getContent() {
            return content;
        }

        public String getProcessingType() {
            return processingType;
        }
    }
}
