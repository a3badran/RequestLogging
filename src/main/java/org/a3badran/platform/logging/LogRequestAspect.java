/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Aspect to add RequestScope to any method annotated 
 * with @LogRequest. See RequestLogger for log4j setup and more 
 * information.
 * 
 * @author a3badran
 *
 */

@Aspect
public class LogRequestAspect extends LogAspect {

    public LogRequestAspect() {
        // default empty constructor
    }

    @Around(value = "@annotation(LogRequest)", argNames = "joinPoint,LogRequest")
    public Object log(ProceedingJoinPoint joinPoint, LogRequest logRequest) throws Throwable {
        String name = logRequest.value();

        // use method name if @LogRequest(name) is null
        if (name == null || name.isEmpty()) {
            name = joinPoint.getSignature().getName();
            if (joinPoint.getTarget() != null && joinPoint.getTarget().getClass() != null) {
                name = joinPoint.getTarget().getClass().getSimpleName() + "." + name;
            }
        }

        // start the scope
        RequestScope scope = RequestLogger.startScope(name);
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();
            Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
            Annotation[][] annotations;
            annotations = joinPoint.getTarget().getClass().
                    getMethod(methodName, parameterTypes).getParameterAnnotations();

            int i = 0;
            for (Object arg : joinPoint.getArgs()) {
                for (Annotation annotation : annotations[i]) {
                    if (annotation.annotationType() == LogParam.class) {
                        String string = arg == null ? "null" : arg.toString();
                        RequestLogger.addInfo(((LogParam) annotation).value(), string);
                    }
                }
                i++;
            }

            // run the method
            // NOTE: exceptions thrown before the actual method is called will result in failure.
            // TODO: configure the ability to bypass exception prior to method calling.
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
