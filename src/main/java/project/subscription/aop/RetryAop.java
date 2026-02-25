package project.subscription.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import project.subscription.aop.annotation.Retry;

@Aspect
@Component
@Slf4j
public class RetryAop {


    @Around("@annotation(retry)")
    public Object doRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String signature = className + "." + methodName + "()";
        int value = retry.value();
        Exception exceptionHolder = null;
        for (int i = 1; i <= value; i++) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                log.warn(
                        "[RETRY] {} - attempt {}/{} failed: {} (remaining={})",
                        signature,
                        i,
                        value,
                        e.getClass().getSimpleName(),
                        value - i
                );
                Thread.sleep(1000);
                exceptionHolder = e;
            }
        }
        throw exceptionHolder;
    }
}
