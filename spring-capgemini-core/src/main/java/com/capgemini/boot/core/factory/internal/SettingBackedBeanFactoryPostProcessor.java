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

package com.capgemini.boot.core.factory.internal;

import com.capgemini.boot.core.factory.FactoryProcessorStrategy;
import com.capgemini.boot.core.factory.SettingsResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Finds all SettingsBackedBeanFactory beans, and registers SettingBackedBean bean definitions if factory methods exist.
 *
 */
public class SettingBackedBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    
    private ConfigurableEnvironment environment;
    
    private ExceptionFactory exceptionFactory;
    
    private SettingsResolver settingsResolver;
    
    private FactoryProcessorStrategy processorStrategy;
    
    //A cache of different settings classes so that each only has to be created once
    private Map<Class<?>, Object> settingsCache = new HashMap<Class<?>, Object>();
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
        
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
    
    @Override
    public void postProcessBeanDefinitionRegistry(
            BeanDefinitionRegistry registry) throws BeansException {
        final String[] factoryBeanNames = getProcessorStrategy().getFactoryBeanNames(applicationContext);
        
        for (String factoryBeanName : factoryBeanNames) {
            registerSettingBackedBeansForFactory(factoryBeanName, registry);
        }
    }

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //Do nothing
        
    }
    
    protected Object getSettings(Class<?> settingsClass, String prefix) {
        if (!this.settingsCache.containsKey(settingsClass)) {
            this.settingsCache.put(settingsClass, resolveSettings(settingsClass, prefix));
        }
        
        return this.settingsCache.get(settingsClass);
    }
    
    protected ExceptionFactory getExceptionFactory() {
        if (exceptionFactory == null) {
            exceptionFactory = new DefaultExceptionFactory();
        }
        
        return exceptionFactory;
    }
    
    protected SettingsResolver getSettingsResolver() {
        if (settingsResolver == null) {
            //Check if a settings resolver bean exists, otherwise use spring boot resolver            
            if (!applicationContext.getBeansOfType(SettingsResolver.class).isEmpty()) {
                settingsResolver = applicationContext.getBean(SettingsResolver.class);
            } else {
                settingsResolver = new SpringBootSettingsResolver();
            }
        }
        return settingsResolver;
    }
    
    protected FactoryProcessorStrategy getProcessorStrategy() {
        if (processorStrategy == null) {
            //Check if an processor strategy bean exists, otherwise use default annotation strategy
            if (!applicationContext.getBeansOfType(FactoryProcessorStrategy.class).isEmpty()) {
                processorStrategy = applicationContext.getBean(FactoryProcessorStrategy.class);
            } else {
                processorStrategy = new DefaultAnnotationStrategy();
            }
        }
        
        return processorStrategy;
    }
    
    private void registerSettingBackedBeansForFactory(String settingsBackedBeanFactoryName, BeanDefinitionRegistry registry) {   
        final Class<?> factoryClass = getClassForBeanName(settingsBackedBeanFactoryName, registry);
        
        //Get settings class and prefix from factory
        final Class<?> settingsClass = getProcessorStrategy().getSettingsClass(factoryClass);
        final String settingPrefix = getProcessorStrategy().getSettingPrefix(factoryClass);
        
        //Get all bean factory methods in this settings backed bean factory.
        //Map of setting name to list of methods
        final Map<String, List<Method>> settingBackedBeanMethods = getSettingBackedBeanMethods(factoryClass);
        
        //For each setting that is mapped to a factory method, register beans
        for (String settingName : settingBackedBeanMethods.keySet()) {
            registerSettingBackedBeansForSetting(getSettings(settingsClass, settingPrefix), settingName, 
                    settingsBackedBeanFactoryName, settingBackedBeanMethods.get(settingName), registry);
        }
        
    }
    
    private void registerSettingBackedBeansForSettingInstanceValue(Object settings, String settingInstanceName, Object settingInstanceValue, 
            String settingsBackedBeanFactoryName, List<Method> linkedSCBMethods, BeanDefinitionRegistry registry) {
        
        //Register a bean for each factory method that is linked with the setting, within this factory bean,
        for (Method settingBackedBeanMethod : linkedSCBMethods) {
            
            //Obtain nameSuffix
            final String nameSuffix = getProcessorStrategy().getBeanNameSuffix(settingBackedBeanMethod);
            
            final BeanDefinition beanDefinition = createBeanDefinition(settings, settingInstanceValue, 
                    settingsBackedBeanFactoryName, settingBackedBeanMethod);
        
            registry.registerBeanDefinition(settingInstanceName + nameSuffix, beanDefinition);
        }
    }
    
    private Class<?> getClassForBeanName(String beanName, BeanDefinitionRegistry registry) {
        final String factoryClassName = registry.getBeanDefinition(beanName).getBeanClassName();
        
        try {
            return Class.forName(factoryClassName);
        } catch (ClassNotFoundException e) {
            throw exceptionFactory.createGenericException("Bean class cannot be found", e);
        }
    }
    
    private BeanDefinition createBeanDefinition(Object settings, Object setting, String factoryBeanName, Method factoryMethod) {
        final GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setFactoryBeanName(factoryBeanName);
        definition.setFactoryMethodName(factoryMethod.getName());
        
        //Create method arguments (confusingly called constructor arguments in spring)
        final ConstructorArgumentValues arguments = new ConstructorArgumentValues();
        arguments.addIndexedArgumentValue(0, setting);
        
        final int parameterCount = factoryMethod.getParameterTypes().length;
        if (parameterCount == 2) {
            arguments.addIndexedArgumentValue(1, settings);
        } else if (parameterCount > 2) {
            throw exceptionFactory.createInvalidArgumentsException(factoryMethod);
        }
        
        //TODO more checking of method arguments to ensure they're correct
        
        definition.setConstructorArgumentValues(arguments);

        return definition;
    }
        
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void registerSettingBackedBeansForSetting(Object settings, String settingName, 
            String settingsBackedRegistrarBeanName, List<Method> linkedSCBMethods, BeanDefinitionRegistry registry) {
        
        try {
            //Get getter method for setting
            final Method settingGetter = settings.getClass().getMethod(getGetterMethodForSetting(settingName));
            final Object returnedSetting = settingGetter.invoke(settings);
            
            //TODO provide a 'SettingBackedBeanDefinitionStrategy' interface to allow customization of how a
            //setting (depending on the type) is converted into bean definition(s)
            //Check type of returned setting
            if (returnedSetting instanceof Map) {
                final Map<String, Object> settingInstanceValues = (Map) returnedSetting;
                
                //For each instance of setting, invoke all setting backed bean factory methods
                for (String settingInstanceName : settingInstanceValues.keySet()) {
                    final Object settingInstanceValue = settingInstanceValues.get(settingInstanceName);
                    registerSettingBackedBeansForSettingInstanceValue(settings, settingInstanceName, settingInstanceValue, 
                            settingsBackedRegistrarBeanName, linkedSCBMethods, registry);
                }
            } else if (returnedSetting instanceof Collection) {
                final Collection<Object> settingInstanceValues = (Collection) returnedSetting;
                
                int countForName = 0;
                for (Object settingInstanceValue : settingInstanceValues) {
                    countForName++;
                    registerSettingBackedBeansForSettingInstanceValue(settings, Integer.toString(countForName), settingInstanceValue, settingsBackedRegistrarBeanName, linkedSCBMethods, registry);
                }
            } else {
                //Setting is not a collection, so treat as a single instance
                registerSettingBackedBeansForSettingInstanceValue(settings, "", returnedSetting, settingsBackedRegistrarBeanName, linkedSCBMethods, registry);
            }
        } catch (NoSuchMethodException e) {
            throw exceptionFactory.createNoMatchingSettingException(settingName);
        } catch (SecurityException e) {
            throw exceptionFactory.createGenericException("An exception occured when obtaining setting named " + settingName, e);
        } catch (IllegalAccessException e) {
            throw exceptionFactory.createSettingAccessException(settingName);
        } catch (IllegalArgumentException e) {
            throw exceptionFactory.createSettingArgumentsException(settingName);
        } catch (InvocationTargetException e) {
            throw exceptionFactory.createGenericException("An exception occured when obtaining setting named " + settingName, e);
        }
    }    

    private String getGetterMethodForSetting(String settingName) {
        //Convert first letter of name to uppercase
        final char first = Character.toUpperCase(settingName.charAt(0));
        return "get" + first + settingName.substring(1);
    }
    
    private Map<String, List<Method>> getSettingBackedBeanMethods(Class<?> clazz) {
        final Map<String, List<Method>> settingMethods = new HashMap<String, List<Method>>();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (getProcessorStrategy().isFactoryMethod(method)) {
                final String setting = getProcessorStrategy().getSettingName(method);
                
                if (!settingMethods.containsKey(setting)) {
                    settingMethods.put(setting, new ArrayList<Method>());
                }
                
                settingMethods.get(setting).add(method);
            }
        }
        
        return settingMethods;
    }

    private <T> T  resolveSettings(Class<T> settingsClass, String prefix) {
        return getSettingsResolver().resolveSettings(settingsClass, prefix, environment);
    }
}
