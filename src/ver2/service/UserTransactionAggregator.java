package ver2.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTransactionAggregator {
    private Map<String, List<LogEntry>> userLogs = new HashMap<>();
    private Map<String, BigDecimal> userBalances = new HashMap<>();

    public void processLogEntries(List<LogEntry> logs) {

    }

    public void calculateFinalBalances() {

    }

    public Map<String, List<LogEntry>> getUserLogs() {
        return userLogs;
    }

    public Map<String, BigDecimal> getUserBalances() {
        return userBalances;
    }
}
