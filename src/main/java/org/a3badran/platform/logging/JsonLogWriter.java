/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * record request data to log in JSON format
 */
public class JsonLogWriter implements Writer {
    private static final Log log = LogFactory.getLog("request");

    @Override
    public void write(RequestScope requestScope) {
        if (log.isInfoEnabled()) {
            log.info(requestScope.toString());
        }
    }
}
