package project.subscription.log;


import lombok.Data;

@Data
public class TraceStatus {


    private int level;
    private long currentTime;
    private String messsage;

    public TraceStatus(int level, long currentTime, String messsage) {
        this.level = level;
        this.currentTime = currentTime;
        this.messsage = messsage;
    }
}
