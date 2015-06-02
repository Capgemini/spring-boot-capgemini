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

package com.capgemini.boot.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = TestApplication)
@IntegrationTest(["server.port=0",
                     "test-settings.settingMap.one=mapSettingValueOne",
                     "test-settings.settingMap.two=mapSettingValueTwo",
                     "test-settings.settingList[0]=listSettingValueOne",
                     "test-settings.settingList[1]=listSettingValueTwo",
                     "test-settings.singleSetting=singleSettingValue"])
class SettingBackedBeanCreationSpec extends Specification {

    @Autowired
    ApplicationContext context

    def "first map setting backed bean bean exists"() {
        given:
        TestSettingBackedBean bean = context.getBean("oneFromMap")
        
        expect:
        bean.settingValue == "mapSettingValueOne"
    }
    
    def "second map setting backed bean bean exists"() {
        given:
        TestSettingBackedBean bean = context.getBean("twoFromMap")
        
        expect:
        bean.settingValue == "mapSettingValueTwo"
    }
    
    def "first list setting backed bean bean exists"() {
        given:
        TestSettingBackedBean bean = context.getBean("1FromList")
        
        expect:
        bean.settingValue == "listSettingValueOne"
    }
    
    def "second list setting backed bean bean exists"() {
        given:
        TestSettingBackedBean bean = context.getBean("2FromList")
        
        expect:
        bean.settingValue == "listSettingValueTwo"
    }
    
    def "setting backed bean bean exists for single setting"() {
        given:
        TestSettingBackedBean bean = context.getBean("singleSettingBean")
        
        expect:
        bean.settingValue == "singleSettingValue"
    }
}
