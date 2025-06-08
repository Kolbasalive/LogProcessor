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
    private LogParser() {}

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
                String operation = token[1];

                if ("balance".equals(operation)) {
                    BigDecimal amount = new BigDecimal(token[3]);
                    entries.add(new LogEntry(timestamp,
                            actor,
                            OperationType.BALANCE_INQUIRY,
                            amount,
                            null
                    ));
                } else if ("transferred".equals(operation)) {
                    BigDecimal amount = new BigDecimal(token[2]);
                    String target = token[4];
                    entries.add(new LogEntry(timestamp,
                            actor,
                            OperationType.TRANSFERRED,
                            amount,
                            target
                    ));
                } else if ("withdrew".equals(operation)) {
                    BigDecimal amount = new BigDecimal(token[2]);
                    entries.add(new LogEntry(timestamp,
                            actor,
                            OperationType.WITHDREW,
                            amount,
                            null
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return entries;
    }
}