package process.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LogEntry {
    private LocalDateTime timestamp;
    private String actor;
    private OperationType operationType;
    private BigDecimal amount;
    private String targetUser;

    public LogEntry(LocalDateTime timestamp, String actor, OperationType operation, BigDecimal amount, String targetUser) {
        this.timestamp = timestamp;
        this.actor = actor;
        this.operationType = operation;
        this.amount = amount;
        this.targetUser = targetUser;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getActor() {
        return actor;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTargetUser() {
        return targetUser;
    }
}