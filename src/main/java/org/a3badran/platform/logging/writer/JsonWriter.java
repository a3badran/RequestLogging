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

/**
 * record request data to log in JSON format
 */
public class JsonWriter implements Writer {
    private static final Log log = LogFactory.getLog(Writer.LOGGER);

    @Override
    public void write(RequestScope requestScope) {
        if (log.isInfoEnabled()) {
            log.info(requestScope.toString());
        }
    }
}
