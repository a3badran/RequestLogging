/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging.writer;

import java.util.Date;
import java.util.Map;

import org.a3badran.platform.logging.RequestScope;
import org.a3badran.platform.logging.writer.Writer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Strings;

/**
 * record request data to log
 */
public class FileWriter implements Writer {
    private static final Log log = LogFactory.getLog(Writer.LOGGER);
    private static final Object EOL = System.getProperty("line.separator");

    @Override
    public void write(RequestScope requestScope) {
        if (log.isInfoEnabled()) {
            // NOTE: time will be in JVM time zone
            StringBuilder buffer = new StringBuilder();
            buffer.append("----------------------------------------------" + EOL);
            buffer.append("StartTime: ").append(new Date(requestScope.getStartTime()).toString()).append(EOL);
            buffer.append("EndTime: ").append(new Date(requestScope.getEndTime()).toString()).append(EOL);
            buffer.append("Time (ms): ").append(requestScope.getEndTime() - requestScope.getStartTime()).append(EOL);
            
            if (!Strings.isNullOrEmpty(requestScope.getError())) {
                buffer.append("Error: ").append(requestScope.getError()).append(EOL);
            }

            if (!Strings.isNullOrEmpty(requestScope.getWarninge())) {
                buffer.append("Warning: ").append(requestScope.getWarninge()).append(EOL);
            }

            // write all sub scopes
            buffer.append("SubRequests: ");
            if (requestScope.getSubScopes() !=null) {
                for (Map.Entry<String, RequestScope> subScope : requestScope.getSubScopes().entrySet()) {
                    RequestScope scope = subScope.getValue();
                    buffer.append(subScope.getKey());
                    buffer.append(" ").append(scope.getTotalTime());
                    buffer.append("/").append(scope.getCount()).append(", ");
                }
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
