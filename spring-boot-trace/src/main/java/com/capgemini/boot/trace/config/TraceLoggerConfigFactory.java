package com.capgemini.boot.trace.config;

import org.springframework.core.env.Environment;

/**
 * Factory class for creating TraceLoggerConfig.
 * 
 * @author Craig Williams
 *
 */
public final class TraceLoggerConfigFactory {
	private TraceLoggerConfigFactory() {
		
	}
	
	/**
	 * Creates trace logger configuration based on the supplied environment.
	 * 
	 * @param environment Environment for the current application
	 * @return The created config
	 */
	public static TraceLoggerConfig createConfig(Environment environment) {
		return new TraceLoggerPropertiesConfig(environment);
	}
}
