/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {TestSpringConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class RequestLoggingTest {
    
    @Autowired
    private LogMethods methods;

    @Autowired
    private LogMethods methods2;

    @Before
    public void setUp() {
        methods.setMethods2(methods2);
    }

    @Test
    public void noParams() {
        methods.noParams();
    }

    @Test
    public void hasReturnType() {
        Object o = methods.hasReturnType();
        assertNotNull(o);
    }

    @Test
    public void hasPrimitiveParam() {
        methods.hasPrimitiveParam(0);
    }

    @Test
    public void subScopes() {
        methods.subScopes("1234", "9876");
    }

    @Test
    public void nestedSubScopes() {
        methods.nestedSubScopes("1234", "9876");
    }
    
    @Test
    public void hasMixedParams() {
        methods.hasMixedParams(0, "string", 0l);
    }

    @Test
    public void logAllParam() {
        methods.logAllParam(0, "string", 0l);
    }
    
    @Test
    public void logNullParam() {
        methods.logAllParam(0, "string", null);
    }

    @Test
    public void ignoreAllParam() {
        methods.ignoreAllParam(0, "", 0l);
    }

    @Test
    public void logWarning() {
        try {
            methods.logWarning();
        }
        catch (Exception e) {
            // ignore
        }
    }
    
    @Test
    public void logClass() {
        methods.logClassCall();
    }

    @Test
    public void logError() {
        try {
            methods.logError();
        }
        catch (Exception e) {
            // ignore
        }
    }

}
