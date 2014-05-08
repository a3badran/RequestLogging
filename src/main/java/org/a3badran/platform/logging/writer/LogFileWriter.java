/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging.writer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.a3badran.platform.logging.RequestLogger;
import org.a3badran.platform.logging.RequestScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Strings;

/**
 * record request data to log
 */
public class LogFileWriter implements Writer {
    private static final Log log = LogFactory.getLog(Writer.LOGGER);
    private static final Object EOL = System.getProperty("line.separator");

    @Override
    public void write(RequestScope requestScope) {
        if (log.isInfoEnabled()) {
            String requestId = RequestLogger.MDC.get().get(Writer.REQUEST_ID);
            String threadId = RequestLogger.MDC.get().get(Writer.THREAD_ID);

            // NOTE: time will be in JVM time zone
            StringBuilder buffer = new StringBuilder();
            buffer.append("----------------------------------------------" + EOL);
            buffer.append("StartTime: ").append(new Date(requestScope.getStartTime()).toString()).append(EOL);
            buffer.append("EndTime: ").append(new Date(requestScope.getEndTime()).toString()).append(EOL);
            buffer.append("Time (ms): ").append(requestScope.getEndTime() - requestScope.getStartTime()).append(EOL);
            buffer.append("RequestID: ").append(requestId).append(EOL);
            buffer.append("ThreadID: ").append(threadId).append(EOL);

            // write all sub scopes
            buffer.append("SubRequests: ");
            if (requestScope.getSubScopes() !=null) {
                for (Map.Entry<String, RequestScope> subScope : requestScope.getSubScopes().entrySet()) {
                    RequestScope scope = subScope.getValue();
                    buffer.append(subScope.getKey());
                    buffer.append(" ").append(scope.getTotalTime());
                    buffer.append("/").append(scope.getCallCount()).append(", ");
                }
            }

            // write all counters
            buffer.append(EOL);
            buffer.append("Counters: ");
            if (requestScope.getCounters() != null) {
                for (Map.Entry<String, AtomicLong> entry : requestScope.getCounters().entrySet()) {
                    buffer.append(entry.getKey()).append("/").append(entry.getValue()).append(", ");
                }
            }

            if (!Strings.isNullOrEmpty(requestScope.getError())) {
                buffer.append(EOL).append("Error: ").append(requestScope.getError());
            }

            if (!Strings.isNullOrEmpty(requestScope.getWarninge())) {
                buffer.append(EOL).append("Warning: ").append(requestScope.getWarninge());
            }

            // write all additional info
            buffer.append(EOL);
            if (requestScope.getInfo() != null) {
                for (String item : requestScope.getInfo()) {
                    buffer.append(item).append(EOL);
                }
            }
            buffer.append("Request: ").append(requestScope.getName()).append(EOL);

            log.info(buffer.toString());
        }

    }
}
