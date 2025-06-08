package process.utils;

import process.domain.LogEntry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileUtils {

    private FileUtils() {}

    public static List<Path> getLogFiles(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Directory not exist: " + directory);
        }

        try (Stream<Path> stream = Files.list(directory)) {
            return stream
                    .filter(path -> path.toString().endsWith(".log"))
                    .toList();
        }
    }

    public static void writeUserLogs(Path directory, Map<String, List<LogEntry>> userLogs) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Map.Entry<String, List<LogEntry>> entry : userLogs.entrySet()) {
            String user = entry.getKey();
            List<LogEntry> logEntries = entry.getValue();
            Path userFile = directory.resolve(user + ".log");

            try (BufferedWriter writer = Files.newBufferedWriter(userFile)) {
                for (LogEntry log : logEntries) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[").append(log.getTimestamp().format(dateTimeFormatter)).append("] ");
                    sb.append(log.getActor()).append(" ");

                    switch (log.getOperationType()) {
                        case BALANCE_INQUIRY:
                            sb.append("balance inquiry ").append(log.getAmount());
                            break;
                        case TRANSFERRED:
                            sb.append("transferred ").append(log.getAmount())
                                    .append(" to ").append(log.getTargetUser());
                            break;
                        case RECEIVED:
                            sb.append("received ").append(log.getAmount())
                                    .append(" from ").append(log.getTargetUser());
                            break;
                        case WITHDREW:
                            sb.append("withdrew ").append(log.getAmount());
                            break;
                        case FINAL_BALANCE:
                            sb.append("final balance ").append(log.getAmount());
                            break;
                    }

                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
        }
    }
}