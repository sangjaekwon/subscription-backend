package project.subscription.log;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";


    private ThreadLocal<LogInfo> threadLocal = new ThreadLocal<>();


    public TraceStatus begin(String message) {
        if(threadLocal.get() == null) {
            threadLocal.set(new LogInfo());
        } else {
            LogInfo logInfo = threadLocal.get();
            logInfo.setLevel(logInfo.getLevel()+1);
        }
        LogInfo logInfo = threadLocal.get();
        long currentTime = System.currentTimeMillis();
        log.info("[{}] {}{}", logInfo.getTransactionId(), levelString(logInfo.getLevel(), START_PREFIX), message);

        return new TraceStatus(logInfo.getLevel(), currentTime, message);

    }

    private String levelString(int level, String prefix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }


    public void end(TraceStatus traceStatus, String e) {
        long time = System.currentTimeMillis() - traceStatus.getCurrentTime();
        LogInfo logInfo = threadLocal.get();
        if(e == null) {
            log.info("[{}] {}{} time={}ms", logInfo.getTransactionId(),
                    levelString(traceStatus.getLevel(), COMPLETE_PREFIX), traceStatus.getMesssage(), time);
        } else {
            log.info("[{}] {}{} time={}ms \n{}", logInfo.getTransactionId(),
                    levelString(traceStatus.getLevel(), EX_PREFIX), traceStatus.getMesssage(), time, e);
        }
        if(traceStatus.getLevel() == 0) threadLocal.remove();
    }
}
