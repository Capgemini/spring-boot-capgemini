package com.capgemini.boot.trace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.capgemini.boot.trace.TraceLoggerConfiguration;

/**
 * Methods annotated with this annotation will be trace logged when TraceLogger is enabled
 * for a spring-boot application.
 * 
 * @author Craig Williams
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trace {

}
