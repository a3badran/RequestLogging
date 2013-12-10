/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import java.util.Collection;

public abstract class LogAspect {

    protected RequestErrorHandler requestErrorHandler;

    //-------------------------------------------------------------
    // IoC
    //-------------------------------------------------------------
    public void setRecorders(Collection<Writer> writers) {
        RequestLogger.setWriters(writers);
    }

    public void setRequestErrorHandler(RequestErrorHandler requestErrorHandler) {
        this.requestErrorHandler = requestErrorHandler;
    }
}
