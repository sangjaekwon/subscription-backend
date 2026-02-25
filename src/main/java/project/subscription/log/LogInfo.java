package project.subscription.log;

import lombok.Data;

import java.util.UUID;

@Data
public class LogInfo {

    private String transactionId;
    private int level;

    public LogInfo() {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8);
        this.level = 0;
    }
}
