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

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;

import com.capgemini.boot.core.factory.FactoryProcessorStrategy;
import com.capgemini.boot.core.factory.SettingBackedBean;
import com.capgemini.boot.core.factory.SettingBackedBeanFactory;

/**
 * @SettingBackedBean and @SettingBackedBeanFactory annotation strategy.
 *
 */
public class DefaultAnnotationStrategy implements FactoryProcessorStrategy {  
    
    @Override
    public Class<?> getSettingsClass(Class<?> factoryClass) {
        final SettingBackedBeanFactory annotation = getAnnotationFromFactory(factoryClass);
        return annotation.settingsClass();
    }
    
    @Override
    public String getSettingPrefix(Class<?> factoryClass) {
        final SettingBackedBeanFactory annotation = getAnnotationFromFactory(factoryClass);
        return annotation.settingPrefix();
    }
    
    @Override
    public String getBeanNameSuffix(Method factoryMethod) {
        final SettingBackedBean annotation = (SettingBackedBean) factoryMethod.getAnnotation(getFactoryMethodAnnotation());
        return annotation.beanNameSuffix();
    }
    
    @Override
    public String[] getFactoryBeanNames(ApplicationContext context) {
        return context.getBeanNamesForAnnotation(getFactoryClassAnnotation());
    }
    
    @Override
    public boolean isFactoryMethod(Method method) {
        return method.isAnnotationPresent(getFactoryMethodAnnotation());
    }
    
    @Override
    public String getSettingName(Method factoryMethod) {
        return factoryMethod.getAnnotation(getFactoryMethodAnnotation()).setting();
    }
    
    private SettingBackedBeanFactory getAnnotationFromFactory(Class<?> factoryClass) {
        return factoryClass.getAnnotation(getFactoryClassAnnotation());
    }
    
    private Class<SettingBackedBeanFactory> getFactoryClassAnnotation() {
        return SettingBackedBeanFactory.class;
    }
    
    private Class<SettingBackedBean> getFactoryMethodAnnotation() {
        return SettingBackedBean.class;
    }
}
