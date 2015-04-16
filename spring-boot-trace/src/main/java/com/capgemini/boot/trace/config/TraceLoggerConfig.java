package com.capgemini.boot.trace.config;

import java.util.List;

/**
 * Configuration for trace logging.
 * 
 * @author Craig Williams
 *
 */
public interface TraceLoggerConfig {

	List<TraceLoggerPointcut> getConfiguredPointcuts();
	
	boolean isEnabled();
}
