package com.capgemini.boot.trace;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.capgemini.boot.trace.annotation.Trace;

/**
 * Configures basic trace logging for enabled spring-boot applications.
 * 
 * Currently, only methods annotated with the @Trace annotation will
 * be traced.
 * 
 * @author Craig Williams
 *
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TraceLoggerConfiguration {
	@Bean(name = "traceInterceptor")
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setEnterMessage("Entering $[methodName]($[arguments])");
        customizableTraceInterceptor.setExitMessage("Leaving  $[methodName](), returned $[returnValue]");
        return customizableTraceInterceptor;
    }

    @Bean(name = "traceAnnotationAdvisor")
    public Advisor traceAnnotationAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.capgemini.boot.trace.annotation.Trace)");
        return new DefaultPointcutAdvisor(pointcut, customizableTraceInterceptor());
    }
}
