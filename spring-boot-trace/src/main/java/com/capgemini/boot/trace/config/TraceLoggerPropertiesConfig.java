package com.capgemini.boot.trace.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.PropertyResolver;

/**
 * Trace Logger configuration backed by properties.
 * 
 * Pointcuts are configured via trace-logging.pointcut.[name] properties.
 * 
 * @author Craig Williams
 *
 */
public class TraceLoggerPropertiesConfig implements TraceLoggerConfig {

	private static final String PROPERTY_PREFIX = "trace-logging.";
	
	private static final String POINTCUT_PREFIX = PROPERTY_PREFIX + "pointcut.";
	
	private RelaxedPropertyResolver propertyResolver;
	
	public TraceLoggerPropertiesConfig(PropertyResolver environment) {
		propertyResolver = new RelaxedPropertyResolver(environment);
	}
	
	/**
	 * Retrieves pointcuts configured via trace-logging.pointcut.[name] properties.
	 * 
	 * @return configured pointcuts
	 */
	@Override
	public List<TraceLoggerPointcut> getConfiguredPointcuts() {
		final Map<String, Object> pointcutProperties = propertyResolver.getSubProperties(POINTCUT_PREFIX);
		
		final List<TraceLoggerPointcut> pointcuts = new ArrayList<TraceLoggerPointcut>();
		
		for (String pointcutName : pointcutProperties.keySet()) {
			final TraceLoggerPointcut pointcut = new TraceLoggerPointcut(createPointcutNameFromPropertyKey(pointcutName), 
					pointcutProperties.get(pointcutName).toString());
			pointcuts.add(pointcut);
		}
		
		return pointcuts;
	}

	/**
	 * Trace logging is considered to be enabled if there are configured pointcuts.
	 * 
	 * @return true if there are configured pointcuts, false otherwise
	 */
	@Override
	public boolean isEnabled() {
		return !getConfiguredPointcuts().isEmpty();
	}
	
	private String createPointcutNameFromPropertyKey(String key) {
		return key.replace(".",  "-");
	}
}
