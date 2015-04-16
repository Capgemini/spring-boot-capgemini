package com.capgemini.boot.trace;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.AbstractTraceInterceptor;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import com.capgemini.boot.trace.config.TraceLoggerPointcut;

/**
 * Utility methods for trace logger configuration.
 * 
 * @author Craig Williams
 *
 */
public final class TraceLoggerConfigurationUtils {
	
	private static final String ADVISOR_BEAN_SUFFIX = "Advisor";
	
	/** 
	 * Private constructor as this class provides static methods only
	 */
	private TraceLoggerConfigurationUtils() {
		
	}
	
	/**
	 * Creates a trace interceptor for logging entry into and exit from methods.
	 * 
	 * @return The created trace interceptor
	 */
	public static AbstractTraceInterceptor createTraceInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setEnterMessage("Entering $[methodName]($[arguments])");
        customizableTraceInterceptor.setExitMessage("Leaving  $[methodName](), returned $[returnValue]");
        return customizableTraceInterceptor;
    }
	
	/**
	 * Registers an advisor bean based on a specified pointcut.
	 * 
	 * @param registry The bean registry where the advisor will be registered.
	 * @param pointcut The pointcut for the advisor
	 */
	public static void registerAdvisor(BeanDefinitionRegistry registry, TraceLoggerPointcut pointcut) {
		registry.registerBeanDefinition(createAdvisorBeanName(pointcut.getName()), 
				createAdvisorBeanDefinition(pointcut.getPointcutExpression()));
	}
	
	private static BeanDefinition createAdvisorBeanDefinition(String pointcutExpression) {
		final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setConstructorArgumentValues(createAdvisorConstructorArguments(pointcutExpression));
		beanDefinition.setBeanClass(DefaultPointcutAdvisor.class);
		
		return beanDefinition;
	}
	
	private static ConstructorArgumentValues createAdvisorConstructorArguments(String pointcutExpression) {
		final ConstructorArgumentValues constructorValues = new ConstructorArgumentValues();
		
		final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(pointcutExpression);
		
		constructorValues.addIndexedArgumentValue(0, pointcut);
		constructorValues.addIndexedArgumentValue(1, TraceLoggerConfigurationUtils.createTraceInterceptor());
		
		return constructorValues;
	}
	
	private static String createAdvisorBeanName(String pointcutName) {
		return pointcutName + ADVISOR_BEAN_SUFFIX;
	}
}
