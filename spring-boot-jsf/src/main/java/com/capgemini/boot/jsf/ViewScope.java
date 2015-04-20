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
package com.capgemini.boot.jsf;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Spring scope which corresponds to the JSF View Scope.
 */
public class ViewScope implements Scope {

    public static final String SCOPE_VIEW = "view";
//
    /**
     * Get the object with the supplied name, using the object factory to create it,
     * as long as it isn't found in the view.
     */
    public Object get(String name , ObjectFactory<?> objectFactory) {
        Object result;
        Map<String, Object> viewMap = new HashMap<String, Object>();
        if (viewMap.containsKey(name)) {
            result = viewMap.get(name);
        } else {
            Object object = objectFactory.getObject();
            viewMap.put(name, object);
            result = object;
        }
        return result;
    }

    /**
     * Remove the bean with the supplied name from the view scope.
     */
    public Object remove(String name) {
        return FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(name);
    }

    /**
     * Register a callback to be called on destruction
     * @see org.springframework.beans.factory.config.Scope#registerDestructionCallback(String, Runnable)
     *
     * @param name
     * @param callback
     */
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {  }

    /**
     * @see org.springframework.beans.factory.config.Scope#resolveContextualObject(String)
     * @param key
     * @return
     */
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    /**
     * Get the conversion ID.
     * @see org.springframework.beans.factory.config.Scope#getConversationId()
     *
     * @return String
     */
    @Override
    public String getConversationId() {
        return null;
    }
}
