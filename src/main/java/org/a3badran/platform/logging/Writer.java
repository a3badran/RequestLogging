/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

/**
 * Interface to write requests
 */
public interface Writer {
    public static final String LOGGER = "requestLogger";
    public void write(RequestScope requestScope);
}
