/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging.aspect;

import java.lang.reflect.Modifier;

import org.a3badran.platform.logging.RequestLogger;
import org.a3badran.platform.logging.RequestScope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspect to add RequestScope to any public method in any class that is annotated 
 * with @LogClassRequests. See RequestLogger for log4j setup and more 
 * information.
 * 
 * @author a3badran
 *
 */

@Aspect
public class LogClassAspect extends BaseAspect {

    public LogClassAspect() {
        // default empty constructor
    }

    @Around("within(@org.a3badran.platform.logging.annotation.LogClassRequests *)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // if method non-public skip
        if (!Modifier.isPublic(joinPoint.getSignature().getModifiers())) {
            return joinPoint.proceed();
        }

        // use method name
        String name = joinPoint.getSignature().getName();
        if (joinPoint.getTarget() != null && joinPoint.getTarget().getClass() != null) {
            name = joinPoint.getTarget().getClass().getSimpleName() + "." + name;
        }

        // start the scope
        RequestScope scope = RequestLogger.startScope(name);
        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            if (requestErrorHandler != null) {
                requestErrorHandler.handleError(t);
            } else {
                RequestLogger.addError(t.toString());
            }
            throw t;
        } finally {
            // close the scope no matter what
            RequestLogger.endScope(scope);
        }
    }
}
