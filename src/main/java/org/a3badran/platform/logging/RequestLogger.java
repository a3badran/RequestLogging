/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import com.google.common.collect.Sets;
import org.a3badran.platform.logging.writer.LogFileWriter;
import org.a3badran.platform.logging.writer.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * RequestLogger is meant to help with logging consistent timing data
 * that could be used for analysis at a later time. It works at a request level (per thread).
 * It is not intended for low level profiling, but rather for collecting metrics and data pertaining
 * to a request at a high level.  For example, you will start a scope at the top HTTP servlet handler level, 
 * then add other scopes to expensive operations (i.e. database calls, remote services calls, computationally
 * expensive functions, etc).  This should help with understanding where time get spent during a request as well
 * as capturing the type, time and volume of requests your service gets.
 *    
 * @author a3badran
 *
 */
public class RequestLogger {
    private static final Log log = LogFactory.getLog(Writer.LOGGER);
    private static final Object EOL = System.getProperty("line.separator");
    private static ThreadLocal<RequestScope> threadLocal = new ThreadLocal<>();
    private static Collection<Writer> writers = Sets.newHashSet((Writer) new LogFileWriter());
    private static RequestErrorHandler errorHandler;
    public static ThreadLocal<Map<String, String>> MDC = new ThreadLocal<>();

    private RequestLogger() {
        // hidden constructor.
    }

    public static RequestScope startScope(String name) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            topScope =  new RequestScope(name);
            topScope.incrementCallCount();
            threadLocal.set(topScope);

            if (MDC.get() == null || MDC.get().get(Writer.THREAD_ID) == null) {
                MDC.set(new HashMap<String, String>());
                MDC.get().put(Writer.THREAD_ID, String.valueOf(Thread.currentThread().getId()));
            }

            return topScope;
        }
        else {
            RequestScope subScope = topScope.getSubScope(name);

            if (subScope != null) {
                subScope.resetStartTime();
                return subScope;
            }
            else {
                return new RequestScope(name);
            }
        }
    }

    public static void endScope(RequestScope scope) {
        RequestScope topScope = threadLocal.get();
        if (topScope == scope) {
            topScope.endScope();
            topScope.incrementTotalTime();
            threadLocal.set(null);

            for(Writer writer : writers) {
                writer.write(topScope);
            }
        }
        else {
            scope.endScope();
            topScope.addSubScope(scope);
        }
    }

    public static void renameTopScope(String newName) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            log.error(String.format("[ERROR] no scope defined to rename [%s]%s", newName, EOL));
        }
        else {
            topScope.setName(newName);
        }
    }

    public static void incrementCounter(String key, long value) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            log.error(String.format("[ERROR] can't increment counter without a scope [%s:%s]%s", key, value, EOL));
        }
        else {
            topScope.incrementCounter(key, value);
        }
    }

    public static void addInfo(String key, String value) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            log.error(String.format("[ERROR] can't add info without a scope [%s:%s]%s", key, value, EOL));
        }
        else {
            topScope.addInfo(key, value);
        }
    }

    public static String addRequestId() {
        String guid = UUID.randomUUID().toString();
        addRequestId(guid);
        return guid;
    }

    public static void addRequestId(String guid) {
        if (MDC.get() == null) {
            MDC.set(new HashMap<String, String>());
        }
        MDC.get().put(Writer.REQUEST_ID, guid);
    }

    public static String getRequestId() {
        if (MDC.get() == null) {
            return null;
        }

        return MDC.get().get(Writer.REQUEST_ID);
    }

    public static void addError(String value) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            log.error(String.format("[ERROR] can't add error without a scope [%s]%s", value, EOL));
        }
        else {
            topScope.addError(value);
        }
    }

    public static void addWarning(String value) {
        RequestScope topScope = threadLocal.get();
        if (topScope == null) {
            log.error(String.format("[ERROR] can't add warning without a scope [%s]%s", value, EOL));
        }
        else {
            topScope.addWarning(value);
        }
    }

    public static void setWriters(Collection<Writer> writers) {
        RequestLogger.writers.clear();
        RequestLogger.writers.addAll(writers);
    }

    public static void setRequestErrorHandler(RequestErrorHandler requestErrorHandler) {
        RequestLogger.errorHandler = requestErrorHandler;
    }

    public static RequestErrorHandler getRequestErrorHandler() {
        return RequestLogger.errorHandler;
    }

    public static Collection<Writer> getWriters() {
        return Collections.unmodifiableCollection(RequestLogger.writers);
    }
}
