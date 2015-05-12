/*
* Copyright 2015 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.capgemini.boot.trace;

import com.capgemini.boot.core.factory.EnableSettingBackedBeans;
import com.capgemini.boot.trace.settings.TraceLoggerSettings;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configures basic trace logging for enabled spring-boot applications.
 * 
 * Methods annotated with the @Trace annotation will be traced.
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
@EnableSettingBackedBeans
@EnableAspectJAutoProxy
//I would much rather call isEnabled from the settings bean in an expression here, but the bean isn't available
@ConditionalOnProperty(prefix = TraceLoggerSettings.SETTINGS_PREFIX, value = TraceLoggerSettings.ENABLED_SETTING, matchIfMissing = true)
public class TraceLoggerConfiguration {

    @Autowired
    private TraceLoggerSettings settings;
    
    static final String TRACE_ANNOTATION = "@annotation(com.capgemini.boot.trace.annotation.Trace) or within(@com.capgemini.boot.trace.annotation.Trace *)";

    @Bean(name = "traceInterceptor")
    public AbstractTraceInterceptor customizableTraceInterceptor() {
        return TraceLoggerConfigurationUtils.createTraceInterceptor(settings);
    }

    @Bean(name = "traceAnnotationAdvisor")
    public Advisor traceAnnotationAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(TRACE_ANNOTATION);

        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }
}
