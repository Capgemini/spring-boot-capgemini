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

package com.capgemini.boot.core.factory;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Resolves a settings class instance.
 *
 */
public interface SettingsResolver {
    
    /**
     * Resolves an instance of a settings class of the specfied type, with a property prefix.
     * 
     * @param settingsClass The class type to resolve
     * @param prefix The property prefix
     * @param environment The environment
     * @return The resolved settings class instance
     */
    <T> T resolveSettings(Class<T> settingsClass, String prefix, ConfigurableEnvironment environment);
}
