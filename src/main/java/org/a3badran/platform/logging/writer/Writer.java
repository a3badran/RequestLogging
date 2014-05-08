/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging.writer;

import org.a3badran.platform.logging.RequestScope;

/**
 * Interface to write requests
 */
public interface Writer {
    public static final String LOGGER = "requestLogger";
    public static final String THREAD_ID = "threadId";
    public static final String REQUEST_ID = "requestId";

    public void write(RequestScope requestScope);
}
