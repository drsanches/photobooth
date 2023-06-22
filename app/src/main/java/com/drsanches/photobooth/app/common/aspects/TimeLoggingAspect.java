package com.drsanches.photobooth.app.common.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class TimeLoggingAspect {

    @Pointcut("within(@com.drsanches.photobooth.app.common.aspects.MonitorTime *)")
    public void monitorTime() {
    }

    @Around("monitorTime()")
    public Object aroundCallAt(ProceedingJoinPoint call) throws Throwable {
        StopWatch clock = new StopWatch(call.toString());
        try {
            clock.start(call.toShortString());
            return call.proceed();
        } finally {
            clock.stop();
            log.trace("Method execution time calculated. Method: {}, millis: {}",
                    call.getSignature(), clock.getTotalTimeMillis());
        }
    }
}
