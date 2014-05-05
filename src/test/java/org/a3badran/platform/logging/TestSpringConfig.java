/**
 * Copyright (c) 2013 Ahmed Badran (a3badran). This content is released under the MIT License. See LICENCE.txt
 */
package org.a3badran.platform.logging;

import org.a3badran.platform.logging.aspect.LogClassAspect;
import org.a3badran.platform.logging.aspect.LogRequestAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan({"org.a3badran.platform.logging"})
@EnableAspectJAutoProxy
public class TestSpringConfig
{
    @Bean
    public LogRequestAspect logRequestAspect() {
        LogRequestAspect aspect = new LogRequestAspect();
        aspect.setRequestErrorHandler(new ErrorHandler());
        return aspect;
    }

    @Bean
    public LogClassAspect logClassAspect() {
        LogClassAspect aspect = new LogClassAspect();
        aspect.setRequestErrorHandler(new ErrorHandler());
        return aspect;
    }
}
