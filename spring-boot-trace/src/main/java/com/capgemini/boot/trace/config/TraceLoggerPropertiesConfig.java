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
 */
public final class TraceLoggerPropertiesConfig implements TraceLoggerConfig {

    private static final String PROPERTY_PREFIX = "trace-logging.";

    private static final String POINTCUT_PREFIX = PROPERTY_PREFIX + "pointcut.";

    private RelaxedPropertyResolver propertyResolver;

    public TraceLoggerPropertiesConfig(PropertyResolver environment) {
        propertyResolver = new RelaxedPropertyResolver(environment);
    }

    /**
     * Retrieves pointcuts configured via trace-logging.pointcut.[name]
     * properties.
     * 
     * @return configured pointcuts
     */
    @Override
    public List<TraceLoggerPointcut> getConfiguredPointcuts() {
        final Map<String, Object> pointcutProperties = propertyResolver
                .getSubProperties(POINTCUT_PREFIX);

        final List<TraceLoggerPointcut> pointcuts = new ArrayList<TraceLoggerPointcut>();

        for (String pointcutName : pointcutProperties.keySet()) {
            final TraceLoggerPointcut pointcut = new TraceLoggerPointcut(
                    createPointcutNameFromPropertyKey(pointcutName),
                    pointcutProperties.get(pointcutName).toString());
            pointcuts.add(pointcut);
        }

        return pointcuts;
    }

    /**
     * Trace logging is considered to be enabled if there are configured
     * pointcuts.
     * 
     * @return true if there are configured pointcuts, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return !getConfiguredPointcuts().isEmpty();
    }

    private String createPointcutNameFromPropertyKey(String key) {
        return key.replace(".", "-");
    }
}
