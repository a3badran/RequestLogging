/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {TestSpringConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ClassLoggingTest {
    @Autowired
    private LogClass log;

    @Test
    public void testAnnotatedClass1() {
        log.notAnnotatedMethod1();
    }

    @Test
    public void testAnnotatedClass2() {
        log.notAnnotatedMethod2("id", 10);
    }
}
