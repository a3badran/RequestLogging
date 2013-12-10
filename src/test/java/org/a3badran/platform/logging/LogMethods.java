/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogMethods {
    private LogMethods methods2;
    
    @Autowired
    private LogClass logClass;

    @LogRequest("noParams")
    public void noParams() {
    }

    @LogRequest("hasReturnType")
    public Object hasReturnType() {
        return new Object();
    }

    @LogRequest("hasPrimitiveParam")
    public void hasPrimitiveParam(@LogParam("i") int i) {
    }

    @LogRequest("hasMixedParams")
    public void hasMixedParams(int i, String s, @LogParam("i") Long l) {
    }

    @LogRequest("logAllParam")
    public void logAllParam(@LogParam("i") int i, @LogParam("s") String s, @LogParam("l") Long l) {
    }

    @LogRequest("ignoreAllParam")
    public void ignoreAllParam(int i, String s, Long l) {
    }

    @LogRequest("slow")
    public void slow(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
    
    @LogRequest("logWarning")
    public void logWarning() throws Exception {
        throw new IllegalStateException();
    }
    
    @LogRequest("logError")
    public void logError() throws Exception {
        throw new NullPointerException();
    }

    @LogRequest("subScopes")
    public void subScopes(@LogParam("id1") String id1, @LogParam("id2") String id2) {
        methods2.slow(10);
    }

    @LogRequest("nestedSubScopes")
    public void nestedSubScopes(@LogParam("id1") String id1, @LogParam("id2") String id2) {
        methods2.slow(10);
        methods2.slow(5);

        methods2.subScopes("id3", "id4");
    }
    
    @LogRequest("logClassCall")
    public void logClassCall() {
        logClass.notAnnotatedMethod1();
    }

    public void setMethods2(LogMethods methods2) {
        this.methods2 = methods2;
    }
}
