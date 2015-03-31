package com.capgemini.boot.trace;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import com.capgemini.boot.trace.annotation.EnableTraceLogger;

@Configuration
@EnableAutoConfiguration
@EnableTraceLogger
public class TestApplication {	
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(TestApplication.class).properties(
				"spring.application.name=traceLogger").run(args);
	}
}