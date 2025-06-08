package process.service;

import process.domain.LogEntry;
import process.domain.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTransactionAggregator {
    private final Map<String, List<LogEntry>> userLogs = new HashMap<>();

    public void processLogEntries(List<LogEntry> logs) {
        for (LogEntry entry : logs) {
            String actor = entry.getActor();
            userLogs.putIfAbsent(actor, new ArrayList<>());
            userLogs.get(actor).add(entry);
            if (entry.getOperationType() == OperationType.TRANSFERRED) {
                String recipient = entry.getTargetUser();
                userLogs.putIfAbsent(recipient, new ArrayList<>());
                LogEntry receivedLog = new LogEntry(
                        entry.getTimestamp(),
                        recipient,
                        OperationType.RECEIVED,
                        entry.getAmount(),
                        actor
                );

                userLogs.get(recipient).add(receivedLog);
            }
        }
        for (List<LogEntry> entries : userLogs.values()) {
            entries.sort(Comparator.comparing(LogEntry::getTimestamp));
        }
    }

    public void calculateFinalBalances() {
        for (Map.Entry<String, List<LogEntry>> userEntry : userLogs.entrySet()) {
            String user = userEntry.getKey();
            List<LogEntry> entries = userEntry.getValue();
            BigDecimal balance = BigDecimal.ZERO;
            boolean balanceInit = false;
            for (LogEntry entry : entries) {
                switch (entry.getOperationType()) {
                    case BALANCE_INQUIRY:
                        if (!balanceInit) {
                            balance = balance.add(entry.getAmount());
                            balanceInit = true;
                        }
                        break;
                    case RECEIVED:
                        balance = balance.add(entry.getAmount());
                        break;
                    case WITHDREW,TRANSFERRED:
                        balance = balance.subtract(entry.getAmount());
                        break;
                    default:
                        break;
                }
            }
            LocalDateTime now = LocalDateTime.now();
            LogEntry finalBalanceEntry = new LogEntry(
                    now,
                    user,
                    OperationType.FINAL_BALANCE,
                    balance,
                    null
            );

            entries.add(finalBalanceEntry);
        }
    }

    public Map<String, List<LogEntry>> getUserLogs() {
        return userLogs;
    }
}