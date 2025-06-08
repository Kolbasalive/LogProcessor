import process.domain.LogEntry;
import process.utils.FileUtils;
import process.service.UserTransactionAggregator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static process.service.LogParser.parserLogFile;

public class Main {
    public static void main(String[] args) throws IOException {
        Path logDir = Paths.get("logs");
        Path outputDir = logDir.resolve("transactions_by_users");

        List<Path> logFiles = FileUtils.getLogFiles(logDir);
        List<LogEntry> allEntries = new ArrayList<>();

        for (Path logFile : logFiles) {
            allEntries.addAll(parserLogFile(logFile));
        }

        UserTransactionAggregator aggregator = new UserTransactionAggregator();
        aggregator.processLogEntries(allEntries);
        aggregator.calculateFinalBalances();

        FileUtils.writeUserLogs(outputDir, aggregator.getUserLogs());
    }
}