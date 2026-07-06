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
        // Paths configuration
        String detailsFilePath = "C:\\rok\\details.csv";
        String screenshotsDir = "C:\\rok\\screenshots\\";
        String outputFilePath = "C:\\rok\\screenshot_output.csv";

        // Point it directly to your custom folder where you saved the downloaded file
        String tessDataPath = "C:\\rok\\tessdata\\";

        // 1. Initialize Tesseract OCR Engine
        Tesseract tesseract = new Tesseract();

        // This tells Tess4J to use its bundled library loaders instead of looking for a Windows installation folder
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");

        // ... rest of your code remains exactly the same ...

        // 2. Read the master details file first so we know who our valid players are
        List<GovernorRecord> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(detailsFilePath))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    records.add(new GovernorRecord(tokens[0].trim(), tokens[1].trim(), tokens[2].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading details file: " + e.getMessage());
            return;
        }

        // 3. Process the screenshots folder and extract Player names & Scores
        Map<String, Integer> extractedPlayerScores = new HashMap<>();
        File folder = new File(screenshotsDir);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"))) {
                    System.out.println("Scanning image: " + file.getName());
                    try {
                        // PREPROCESSING: Enhance the image size and contrast for Tesseract
                        java.awt.image.BufferedImage originalImage = javax.imageio.ImageIO.read(file);
                        java.awt.image.BufferedImage enhancedImage = preprocessImage(originalImage);

                        // Run OCR on the enhanced image instead of the raw file
                        String resultText = tesseract.doOCR(enhancedImage);
                        parseScoresFromText(resultText, extractedPlayerScores);
                    } catch (TesseractException | IOException e) {
                        System.err.println("Could not read image " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        // ==========================================
        // REVISED STEP 4: Smart Unique Mapping
        // ==========================================
        Map<String, Integer> individualOwnerScores = new HashMap<>();
        Map<String, Integer> ownerTotalScores = new HashMap<>();

        // 4a. Match messy screenshot data to a UNIQUE owner name exactly ONCE
        for (Map.Entry<String, Integer> entry : extractedPlayerScores.entrySet()) {
            String messyScreenshotName = entry.getKey().toLowerCase();
            int score = entry.getValue();

            for (GovernorRecord rec : records) {
                String cleanOwnerName = rec.owner.toLowerCase().trim();

                // If the screenshot matches a known owner, lock it in for that owner
                if (messyScreenshotName.contains(cleanOwnerName) || cleanOwnerName.contains(messyScreenshotName)) {
                    // Only assign if we haven't assigned a score to this owner yet from this match
                    if (!individualOwnerScores.containsKey(rec.owner)) {
                        individualOwnerScores.put(rec.owner, score);
                        System.out.println("Locked Score -> Owner: [" + rec.owner + "] gets Score: " + score);
                    }
                    break; // Move to the next screenshot entry
                }
            }
        }

        // 4b. Now calculate total owner scores (Main + Farms combined) safely
        for (GovernorRecord rec : records) {
            int score = individualOwnerScores.getOrDefault(rec.owner, 0);

            // Only add the score to the owner's cumulative total once per owner check
            if (!ownerTotalScores.containsKey(rec.owner)) {
                ownerTotalScores.put(rec.owner, score);
            }
        }

        // ==========================================
        // REVISED STEP 5: Safe Output Writing (Only Main Rows Get Scores)
        // ==========================================
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bw.write("Governor ID,Owner,Status,Screenshot Individual Score,Total Combined Score\n");

            for (GovernorRecord record : records) {
                int indScore = individualOwnerScores.getOrDefault(record.owner, 0);
                int totalScore = ownerTotalScores.getOrDefault(record.owner, 0);

                StringBuilder sb = new StringBuilder();
                sb.append(record.id).append(",")
                        .append(record.owner).append(",")
                        .append(record.status).append(",");

                // Only print scores if this is the main account row
                if (record.status.equalsIgnoreCase("main")) {
                    sb.append(indScore).append(",")
                            .append(totalScore);
                } else {
                    // Leaves both individual score and total score completely blank for farm rows
                    sb.append(",")
                            .append("");
                }

                bw.write(sb.toString());
                bw.newLine();
            }
            System.out.println("Processing complete! Fixed screenshot data written to: " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

    /**
     * Re-engineered parser tailored specifically for the image_71fb86.jpg layout.
     * Looks for the points column by working backward from the fraction column,
     * allowing special symbols and spaces in names.
     */
    private static void parseScoresFromText(String rawText, Map<String, Integer> scoreMap) {
        String[] lines = rawText.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Skip headers or empty lines
            if (trimmedLine.isEmpty() || trimmedLine.contains("Governor") || trimmedLine.contains("Points")) {
                continue;
            }

            // Remove rank prefixes if Tesseract picks up the leading "1", "2", "3", etc.
            // This prevents a rank from sticking to a name (e.g. "4 VixenVortex" -> "VixenVortex")
            trimmedLine = trimmedLine.replaceAll("^\\d+\\s+", "");

            // REVISED REGEX FOR THE IMAGE LAYOUT:
            // Match any character string (the name), followed by a number with commas (the score),
            // followed by a fraction sequence like 6/6 or 2/3 (the turned in column).
            Pattern pattern = Pattern.compile("(.+?)\\s+([0-9,]+)\\s+(\\d+/\\d+)");
            Matcher matcher = pattern.matcher(trimmedLine);

            if (matcher.find()) {
                String potentialName = matcher.group(1).trim();
                String scoreStr = matcher.group(2).replace(",", "").trim();

                try {
                    int score = Integer.parseInt(scoreStr);

                    // Clean up common OCR artifacts on symbol-heavy names if necessary
                    if (!potentialName.isEmpty()) {
                        scoreMap.put(potentialName, score);
                        System.out.println("Successfully Parsed -> Player: [" + potentialName + "] Score: [" + score + "]");
                    }
                } catch (NumberFormatException e) {
                    // Ignore unparseable numbers safely
                }
            }
        }
    }

    /**
     * Resizes the image to 2x its original size and converts it to grayscale.
     * This makes thin, small fonts (like 'ethan') much easier for Tesseract to anchor.
     */
    private static java.awt.image.BufferedImage preprocessImage(java.awt.image.BufferedImage originalImage) {
        int newWidth = originalImage.getWidth() * 2;
        int newHeight = originalImage.getHeight() * 2;

        // Create a new high-quality grayscale image buffer
        java.awt.image.BufferedImage processed = new java.awt.image.BufferedImage(
                newWidth, newHeight, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);

        java.awt.Graphics2D g2d = processed.createGraphics();

        // Use high quality rendering hints for interpolation
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Draw the original image upscaled into the grayscale layout
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return processed;
    }
}