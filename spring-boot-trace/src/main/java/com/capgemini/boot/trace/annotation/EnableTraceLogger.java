package com.capgemini.boot.trace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.capgemini.boot.trace.TraceLoggerConfiguration;

/**
 * Trace logging will be enabled for spring-boot applications that enable this annotation.
 * 
 * @author Craig Williams
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TraceLoggerConfiguration.class)
public @interface EnableTraceLogger {

}
