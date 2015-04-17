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

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Configures basic trace logging for enabled spring-boot applications.
 * 
 * Methods annotated with the @Trace annotation will be traced.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TraceLoggerConfiguration {
    @Bean(name = "traceInterceptor")
    public AbstractTraceInterceptor customizableTraceInterceptor() {
        return TraceLoggerConfigurationUtils.createTraceInterceptor();
    }

    @Bean(name = "traceAnnotationAdvisor")
    public Advisor traceAnnotationAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.capgemini.boot.trace.annotation.Trace) or within(@com.capgemini.boot.trace.annotation.Trace *)");
        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }
}
