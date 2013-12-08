/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.springframework.stereotype.Component;

@Component
public class ErrorHandler implements RequestErrorHandler {

    @Override
    public void handleError(Throwable t) {
        if (t instanceof IllegalStateException) {
            RequestLogger.addWarning(t.toString());
        } else {
            RequestLogger.addError(t.toString());
        }
    }

}
