package com.example.rokevents.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ScreenshotScoreMerger {

    static class GovernorRecord {
        String id;
        String owner;
        String status;

        public GovernorRecord(String id, String owner, String status) {
            this.id = id;
            this.owner = owner;
            this.status = status;
        }
    }

    public static String processScreenshots(String detailsFilePath, String screenshotsDir, String outputFilePath, String tessDataPath) throws IOException, TesseractException {
        return processScreenshots(detailsFilePath, screenshotsDir, outputFilePath, tessDataPath, null);
    }

    public static String processScreenshots(String detailsFilePath, String screenshotsDir, String outputFilePath, String tessDataPath, Tesseract externalTesseract) throws IOException, TesseractException {

        Tesseract tesseract = externalTesseract != null ? externalTesseract : new Tesseract();
        if (externalTesseract == null) {
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");
        }

        List<GovernorRecord> records = new ArrayList<>();
        int totalCSVRowsProcessed = 0;
        int mainAccountCount = 0;
        int farmAccountCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(detailsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    GovernorRecord record = new GovernorRecord(tokens[0].trim(), tokens[1].trim(), tokens[2].trim());
                    records.add(record);

                    totalCSVRowsProcessed++;
                    if (record.status.equalsIgnoreCase("main")) {
                        mainAccountCount++;
                    } else if (record.status.equalsIgnoreCase("farm")) {
                        farmAccountCount++;
                    }
                }
            }

        } catch (IOException e) {
            throw new IOException("Error reading details file: " + e.getMessage(), e);
        }

        Map<String, Integer> extractedPlayerScores = new HashMap<>();
        File folder = new File(screenshotsDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"))) {
                    try {
                        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(file);
                        java.awt.image.BufferedImage enhancedImage = preprocessImage(originalImage);

                        String resultText = tesseract.doOCR(enhancedImage);
                        parseScoresFromText(resultText, extractedPlayerScores);
                    } catch (TesseractException | IOException e) {
                        // Log but continue processing
                    }
                }
            }
        }

        Map<String, Integer> individualGovScores = new HashMap<>();
        Map<String, Integer> ownerTotalScores = new HashMap<>();

        for (Map.Entry<String, Integer> entry : extractedPlayerScores.entrySet()) {
            String rawOcrName = entry.getKey();
            String messyScreenshotName = rawOcrName.toLowerCase().replaceAll("[^a-z0-9]", "");
            int score = entry.getValue();

            GovernorRecord bestMatch = null;
            boolean ocrLooksLikeFarm = messyScreenshotName.contains("farm") ||
                    messyScreenshotName.contains("fill") ||
                    messyScreenshotName.contains("alt") ||
                    messyScreenshotName.matches(".*\\d+.*");

            for (GovernorRecord rec : records) {
                if (individualGovScores.containsKey(rec.id)) {
                    continue;
                }

                String cleanOwnerName = rec.owner.toLowerCase().trim().replaceAll("[^a-z0-9]", "");

                if (messyScreenshotName.contains(cleanOwnerName) ||
                        cleanOwnerName.contains(messyScreenshotName) ||
                        hasSharedSequence(messyScreenshotName, cleanOwnerName, 4)) {

                    if (bestMatch == null) {
                        bestMatch = rec;
                    } else {
                        if (ocrLooksLikeFarm && rec.status.equalsIgnoreCase("farm") && bestMatch.status.equalsIgnoreCase("main")) {
                            bestMatch = rec;
                        } else if (!ocrLooksLikeFarm && rec.status.equalsIgnoreCase("main") && bestMatch.status.equalsIgnoreCase("farm")) {
                            bestMatch = rec;
                        }
                    }
                }
            }

            if (bestMatch != null) {
                individualGovScores.put(bestMatch.id, score);
            }
        }

        for (GovernorRecord rec : records) {
            int score = individualGovScores.getOrDefault(rec.id, 0);
            ownerTotalScores.put(rec.owner, ownerTotalScores.getOrDefault(rec.owner, 0) + score);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bw.write("Governor ID,Owner,Status,Screenshot Individual Score,Total Combined Score\n");

            for (GovernorRecord record : records) {
                int indScore = individualGovScores.getOrDefault(record.id, 0);
                int totalScore = ownerTotalScores.getOrDefault(record.owner, 0);

                StringBuilder sb = new StringBuilder();
                sb.append(record.id).append(",")
                        .append(record.owner).append(",")
                        .append(record.status).append(",");

                if (record.status.equalsIgnoreCase("main")) {
                    sb.append(indScore).append(",").append(totalScore);
                } else {
                    sb.append(indScore).append(",").append("");
                }

                bw.write(sb.toString());
                bw.newLine();
            }

        } catch (IOException e) {
            throw new IOException("Error writing output file: " + e.getMessage(), e);
        }

        return outputFilePath;
    }

    public static void main(String[] args) {
        String detailsFilePath = "C:\\rok\\details.csv";
        String screenshotsDir = "C:\\rok\\screenshots\\";
        String outputFilePath = "C:\\rok\\screenshot_output.csv";
        String tessDataPath = "C:\\rok\\tessdata\\";

        try {
            processScreenshots(detailsFilePath, screenshotsDir, outputFilePath, tessDataPath);
            System.out.println("Processing complete! Output saved to: " + outputFilePath);
        } catch (IOException | TesseractException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void parseScoresFromText(String rawText, Map<String, Integer> scoreMap) {
        String[] lines = rawText.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty() || trimmedLine.contains("Governor") || trimmedLine.contains("Points")) {
                continue;
            }

            trimmedLine = trimmedLine.replaceAll("^\\d+\\s+", "");

            Pattern pattern = Pattern.compile("(.+?)\\s+([0-9,]+)\\s+(\\d+(?:/\\d+)?)");
            Matcher matcher = pattern.matcher(trimmedLine);

            if (matcher.find()) {
                String potentialName = matcher.group(1).trim();
                String scoreStr = matcher.group(2).replace(",", "").trim();

                try {
                    int score = Integer.parseInt(scoreStr);
                    if (!potentialName.isEmpty()) {
                        scoreMap.put(potentialName, score);
                        System.out.println("Successfully Parsed -> Player: [" + potentialName + "] Score: [" + score + "]");
                    }
                } catch (NumberFormatException e) {
                    // Fail-safe catch
                }
            }
        }
    }

    private static java.awt.image.BufferedImage preprocessImage(java.awt.image.BufferedImage originalImage) {
        int newWidth = originalImage.getWidth() * 2;
        int newHeight = originalImage.getHeight() * 2;

        java.awt.image.BufferedImage processed = new java.awt.image.BufferedImage(
                newWidth, newHeight, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);

        java.awt.Graphics2D g2d = processed.createGraphics();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return processed;
    }

    private static boolean hasSharedSequence(String s1, String s2, int minSequenceLength) {
        if (s1.length() < minSequenceLength || s2.length() < minSequenceLength) {
            return false;
        }
        for (int i = 0; i <= s1.length() - minSequenceLength; i++) {
            String chunk = s1.substring(i, i + minSequenceLength);
            if (s2.contains(chunk)) {
                return true;
            }
        }
        return false;
    }
}