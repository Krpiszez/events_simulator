package com.example.rokevents.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreKVKScoreCalculator {

    static class GovernorRecord {
        String id;
        String owner;
        String status;
        int individualScore;

        public GovernorRecord(String id, String owner, String status) {
            this.id = id;
            this.owner = owner;
            this.status = status;
            this.individualScore = 0;
        }
    }

    public static String calculateScores(String detailsFilePath, String scoresFilePath, String outputFilePath) throws IOException {
        Map<String, Integer> governorScores = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(scoresFilePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String id = tokens[0].trim();
                    int score = Integer.parseInt(tokens[1].trim());
                    governorScores.put(id, score);
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new IOException("Error reading scores file: " + e.getMessage(), e);
        }

        List<GovernorRecord> records = new ArrayList<>();
        Map<String, Integer> ownerTotalScores = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(detailsFilePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    String id = tokens[0].trim();
                    String owner = tokens[1].trim();
                    String status = tokens[2].trim();

                    GovernorRecord record = new GovernorRecord(id, owner, status);
                    record.individualScore = governorScores.getOrDefault(id, 0);
                    records.add(record);

                    ownerTotalScores.put(owner, ownerTotalScores.getOrDefault(owner, 0) + record.individualScore);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading details file: " + e.getMessage(), e);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bw.write("Governor ID,Owner,Status,Total Score (Main + Farm)\n");

            for (GovernorRecord record : records) {
                StringBuilder sb = new StringBuilder();
                sb.append(record.id).append(",")
                        .append(record.owner).append(",")
                        .append(record.status).append(",");

                if (record.status.equalsIgnoreCase("main")) {
                    sb.append(ownerTotalScores.getOrDefault(record.owner, 0));
                } else {
                    sb.append("");
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
        String scoresFilePath  = "C:\\rok\\scores.csv";
        String outputFilePath  = "C:\\rok\\output.csv";

        try {
            calculateScores(detailsFilePath, scoresFilePath, outputFilePath);
            System.out.println("Processing complete! Output saved to: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}