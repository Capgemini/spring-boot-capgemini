package com.capgemini.boot.trace.config;

/**
 * Wraps a pointcut expression and pointcut name.
 * 
 * @author Craig Williams
 *
 */
public class TraceLoggerPointcut {
	private String name;
	
	private String pointcutExpression;

	public TraceLoggerPointcut(String name, String pointcutExpression) {
		this.name = name;
		this.pointcutExpression = pointcutExpression;
	}

	public String getName() {
		return name;
	}

	public String getPointcutExpression() {
		return pointcutExpression;
	}
	
	
}
