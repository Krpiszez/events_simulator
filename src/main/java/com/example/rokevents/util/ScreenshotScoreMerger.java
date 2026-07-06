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

    public static void main(String[] args) {
        String detailsFilePath = "C:\\rok\\details.csv";
        String screenshotsDir = "C:\\rok\\screenshots\\";
        String outputFilePath = "C:\\rok\\screenshot_output.csv";
        String tessDataPath = "C:\\rok\\tessdata\\";

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");

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

            System.out.println("==========================================");
            System.out.println("MASTER CSV INPUT RECAP:");
            System.out.println("-> Total Player Records Read: " + totalCSVRowsProcessed);
            System.out.println("-> Main Accounts Found     : " + mainAccountCount);
            System.out.println("-> Farm Accounts Found     : " + farmAccountCount);
            System.out.println("==========================================");

        } catch (IOException e) {
            System.err.println("Error reading details file: " + e.getMessage());
            return;
        }

        Map<String, Integer> extractedPlayerScores = new HashMap<>();
        File folder = new File(screenshotsDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"))) {
                    System.out.println("Scanning image: " + file.getName());
                    try {
                        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(file);
                        java.awt.image.BufferedImage enhancedImage = preprocessImage(originalImage);

                        String resultText = tesseract.doOCR(enhancedImage);
                        parseScoresFromText(resultText, extractedPlayerScores);
                    } catch (TesseractException | IOException e) {
                        System.err.println("Could not read image " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        // Map specific Governor IDs to their captured individual scores
        Map<String, Integer> individualGovScores = new HashMap<>();
        Map<String, Integer> ownerTotalScores = new HashMap<>();

        // Match each distinct OCR detection to exactly ONE appropriate Governor record
        for (Map.Entry<String, Integer> entry : extractedPlayerScores.entrySet()) {
            String rawOcrName = entry.getKey();
            String messyScreenshotName = rawOcrName.toLowerCase().replaceAll("[^a-z0-9]", "");
            int score = entry.getValue();

            GovernorRecord bestMatch = null;
            boolean ocrLooksLikeFarm = messyScreenshotName.contains("farm") ||
                    messyScreenshotName.contains("fill") ||
                    messyScreenshotName.contains("alt") ||
                    messyScreenshotName.matches(".*\\d+.*"); // contains numbers like Linky2

            for (GovernorRecord rec : records) {
                // Skip if this specific Gov ID already has an assigned score
                if (individualGovScores.containsKey(rec.id)) {
                    continue;
                }

                String cleanOwnerName = rec.owner.toLowerCase().trim().replaceAll("[^a-z0-9]", "");

                // Check if name matches via string contains or shared sequence
                if (messyScreenshotName.contains(cleanOwnerName) ||
                        cleanOwnerName.contains(messyScreenshotName) ||
                        hasSharedSequence(messyScreenshotName, cleanOwnerName, 4)) {

                    if (bestMatch == null) {
                        bestMatch = rec;
                    } else {
                        // Heuristic routing: pick based on main vs farm signals
                        if (ocrLooksLikeFarm && rec.status.equalsIgnoreCase("farm") && bestMatch.status.equalsIgnoreCase("main")) {
                            bestMatch = rec; // Upgrade to farm match
                        } else if (!ocrLooksLikeFarm && rec.status.equalsIgnoreCase("main") && bestMatch.status.equalsIgnoreCase("farm")) {
                            bestMatch = rec; // Upgrade to main match
                        }
                    }
                }
            }

            // If we found a unique open slot for this owner, lock it
            if (bestMatch != null) {
                individualGovScores.put(bestMatch.id, score);
                System.out.println("Locked Score -> Gov ID: [" + bestMatch.id + "] (" + bestMatch.owner + " - " + bestMatch.status + ") matched to OCR [" + rawOcrName + "] gets Score: " + score);
            }
        }

        // Aggregate totals accurately by Owner
        for (GovernorRecord rec : records) {
            int score = individualGovScores.getOrDefault(rec.id, 0);
            ownerTotalScores.put(rec.owner, ownerTotalScores.getOrDefault(rec.owner, 0) + score);
        }

        int writtenRows = 0;
        int writtenMains = 0;
        int writtenFarms = 0;

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
                    writtenMains++;
                } else {
                    sb.append(indScore).append(",").append(""); // Farm keeps its individual score visible on its row
                    if (record.status.equalsIgnoreCase("farm")) {
                        writtenFarms++;
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
                writtenRows++;
            }

            System.out.println("==========================================");
            System.out.println("FINAL PROCESS WRITE RECAP:");
            System.out.println("-> Total Lines Written to CSV: " + writtenRows);
            System.out.println("-> Main Rows Accounted For   : " + writtenMains);
            System.out.println("-> Farm Rows Accounted For   : " + writtenFarms);
            System.out.println("==========================================");

        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
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