/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging.aspect;

import org.a3badran.platform.logging.RequestErrorHandler;
import org.a3badran.platform.logging.RequestLogger;
import org.a3badran.platform.logging.writer.Writer;

import java.util.Collection;

public abstract class BaseAspect {

    protected RequestErrorHandler requestErrorHandler;

    //-------------------------------------------------------------
    // IoC
    //-------------------------------------------------------------
    public void setWriters(Collection<Writer> writers) {
        RequestLogger.setWriters(writers);
    }

    public void setRequestErrorHandler(RequestErrorHandler requestErrorHandler) {
        this.requestErrorHandler = requestErrorHandler;
    }
}
