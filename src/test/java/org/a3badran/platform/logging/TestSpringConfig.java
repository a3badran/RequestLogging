/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan({ "org.a3badran.platform.logging"})
@EnableAspectJAutoProxy
public class TestSpringConfig
{
    @Bean
    public LogAspect logAspect() {
        LogAspect aspect = new LogAspect();
        aspect.setRequestErrorHandler(new ErrorHandler());
        return aspect;
    }
}
