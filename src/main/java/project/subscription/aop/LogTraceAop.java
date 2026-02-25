package project.subscription.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import project.subscription.log.LogTrace;
import project.subscription.log.TraceStatus;

@Aspect
@Component
@Slf4j
public class LogTraceAop {

    @Pointcut("!execution(* project.subscription.exception..*.*(..)) && !execution(* project.subscription.config..*.*(..))" +
            " && !execution(* project.subscription.jwt..*.*(..))" )
    private void execlude(){}

    private LogTrace logTrace = new LogTrace();

    @Around("execution(* project.subscription..*.*(..)) && execlude()")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringType().getSimpleName();
        TraceStatus status = logTrace.begin(className + "." + methodName + "()");
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logTrace.end(status, e.getClass().getName() +": " +e.getMessage());
            throw e;
        }
        logTrace.end(status, null);

        return result;
    }
}
