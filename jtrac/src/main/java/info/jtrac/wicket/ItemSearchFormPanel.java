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

import info.jtrac.domain.ColumnHeading;
import info.jtrac.domain.FilterCriteria.Expression;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import java.util.List;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.BoundCompoundPropertyModel;

/**
 * item search form panel
 */
public class ItemSearchFormPanel extends BasePanel {
    
    private ItemSearch itemSearch;
    
    private Expression expression;

    private BoundCompoundPropertyModel model;
    
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    
    private Object value;
    private List values;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }
    
    
    
    public ItemSearchFormPanel(String id, User user) {
        super(id);
        this.itemSearch = new ItemSearch(user);
        addComponents();
    }
    
    public ItemSearchFormPanel(String id) {
        super(id);
        Space s = getCurrentSpace();
        if(s != null) {
            this.itemSearch = new ItemSearch(s, this);
        } else {
            this.itemSearch = new ItemSearch(getPrincipal());
        }
        addComponents();
    }
    
    public ItemSearchFormPanel(String id, ItemSearch itemSearch) {
        super(id);
        this.itemSearch = itemSearch;
        addComponents();
    }
    
    private void addComponents() {
        final Form form = new Form("form");
        add(form);
        model = new BoundCompoundPropertyModel(this);
        form.setModel(model);
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                setCurrentItemSearch(itemSearch);
                setResponsePage(ItemListPage.class);
            }
        });
        form.add(new ListView("columns", itemSearch.getColumnHeadings()) {
            protected void populateItem(final ListItem listItem) {
                final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                listItem.add(new Label("columnName", ch.getLabel()));
                DropDownChoice expressionChoice = new DropDownChoice("expression", ch.getValidFilterExpressions(), new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String key = ((Expression) o).getKey();
                        return localize("item_filter." + key);
                    }
                    public String getIdValue(Object o, int i) {
                        return ((Expression) o).getKey();
                    }
                });
                listItem.add(expressionChoice);
                final WebMarkupContainer fragParent = new WebMarkupContainer("fragParent");
                fragParent.setOutputMarkupId(true);
                listItem.add(fragParent);                
                expressionChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        Fragment fragment = ch.getFilterUiFragment(model, ItemSearchFormPanel.this);
                        fragment.setOutputMarkupId(true);
                        listItem.replace(fragment);
                        target.addComponent(fragment);
                    }
                });
            }
        });
        
    }
    
    
    
    
    
}
