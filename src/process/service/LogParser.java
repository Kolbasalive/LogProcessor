package process.service;

import process.domain.LogEntry;
import process.domain.OperationType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LogParser {
    private LogParser() {
    }

    public static List<LogEntry> parserLogFile(Path file) {
        List<LogEntry> entries = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("] ");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime timestamp = LocalDateTime.parse(parts[0].substring(1), formatter);
                String result = parts[1];
                String[] token = result.split(" ");
                String actor = token[0];

                entries.add(buildLogEntry(timestamp, actor, token));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }

    private static LogEntry buildLogEntry(LocalDateTime timestamp, String actor, String[] token) {
        String operation = token[1];
        BigDecimal amount;
        return switch (operation) {
            case "balance" -> {
                amount = new BigDecimal(token[3]);
                yield new LogEntry(timestamp, actor, OperationType.BALANCE_INQUIRY, amount, null);
            }
            case "transferred" -> {
                amount = new BigDecimal(token[2]);
                yield new LogEntry(timestamp, actor, OperationType.TRANSFERRED, amount, token[4]);
            }
            case "withdrew" -> {
                amount = new BigDecimal(token[2]);
                yield new LogEntry(timestamp, actor, OperationType.WITHDREW, amount, null);
            }
            default -> throw new IllegalArgumentException("Unknown operation: " + operation);
        };
    }
}