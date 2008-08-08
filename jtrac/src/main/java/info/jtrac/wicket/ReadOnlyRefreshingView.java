/*
 * Copyright 2002-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.jtrac.wicket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * usage requires only passing a list dynamically by overriding
 */
public abstract class ReadOnlyRefreshingView<T> extends RefreshingView {

    protected final SimpleAttributeModifier CLASS_ALT = new SimpleAttributeModifier("class", "alt");
    protected final SimpleAttributeModifier CLASS_SELECTED = new SimpleAttributeModifier("class", "selected");
    protected final SimpleAttributeModifier CLASS_ERROR_BACK = new SimpleAttributeModifier("class", "error-back");
    
    public ReadOnlyRefreshingView(String id) {
        super(id);
    }

    public abstract List<T> getObjectList();

    @Override
    protected Iterator getItemModels() {
        List<T> list = getObjectList();
        List<IModel> models = new ArrayList<IModel>(list.size());
        for (final T o : list) {
            models.add(new AbstractReadOnlyModel() {

                public Object getObject() {
                    return o;
                }
            });
        }
        return models.iterator();
    }
}
