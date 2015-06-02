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

package com.capgemini.boot.core;

import com.capgemini.boot.core.factory.SettingBackedBean;
import com.capgemini.boot.core.factory.SettingBackedBeanFactory;

@SettingBackedBeanFactory(settingsClass = TestSettings.class, settingPrefix = TestSettings.SETTINGS_PREFIX)
public class TestFactory {
    
    @SettingBackedBean(setting = "settingMap", beanNameSuffix = "FromMap")
    public TestSettingBackedBean createSettingBackedBeanWithMap(String settingValue) {
        return new TestSettingBackedBean(settingValue);
    }
    
    @SettingBackedBean(setting = "settingList", beanNameSuffix = "FromList")
    public TestSettingBackedBean createSettingBackedBeanWithList(String settingValue) {
        return new TestSettingBackedBean(settingValue);
    }
    
    @SettingBackedBean(setting = "singleSetting", beanNameSuffix = "singleSettingBean")
    public TestSettingBackedBean createSettingBackedBeanFromSingleSetting(String settingValue) {
        return new TestSettingBackedBean(settingValue);
    }
}
