/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.springframework.stereotype.Component;

@Component
@LogClassRequests
public class LogClass {

    public void notAnnotatedMethod1() {
        // do nothing
    }

    public int notAnnotatedMethod2(String id, int num) {
        return num;
    }
}
