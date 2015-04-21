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

package com.capgemini.boot.trace;

import org.springframework.beans.FatalBeanException;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class SettingsBackedRegistrar<T> implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private ConfigurableEnvironment environment;
    private T settings;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment)environment;
    }

    protected T getSettings() {
        if (this.settings == null) {
            this.settings = resolveSettings();
        }
        return this.settings;
    }

    protected abstract String getPropertyPrefix();

    private T resolveSettings() {
        try {
            Type type = getClass().getGenericSuperclass();
            ParameterizedType p = (ParameterizedType) type;
            Type settingsType = p.getActualTypeArguments()[0];
            T newSettings = (T) Class.forName(settingsType.getTypeName()).newInstance();

            PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(newSettings);
            factory.setTargetName(getPropertyPrefix());
            factory.setPropertySources(environment.getPropertySources());
            factory.setConversionService(environment.getConversionService());
            factory.bindPropertiesToTarget();

            return newSettings;
        } catch (Exception ex) {
            throw new FatalBeanException("Could not bind DataSourceSettings properties", ex);
        }
    }
}
