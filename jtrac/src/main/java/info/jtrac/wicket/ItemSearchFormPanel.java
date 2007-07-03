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
import info.jtrac.domain.Item;
import info.jtrac.domain.ItemRefId;
import info.jtrac.domain.ItemSearch;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.exception.InvalidRefIdException;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * item search form panel
 */
public class ItemSearchFormPanel extends BasePanel {
    
    private ItemSearch itemSearch;
    
    public ItemSearchFormPanel(String id, User user) {
        super(id);
        this.itemSearch = new ItemSearch(user, this);
        addComponents();
    }
    
    public ItemSearchFormPanel(String id) {
        super(id);
        Space s = getCurrentSpace();
        if(s != null) {
            this.itemSearch = new ItemSearch(s, this);
        } else {
            this.itemSearch = new ItemSearch(getPrincipal(), this);
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
        form.add(new FeedbackPanel("feedback"));
        form.setModel(new CompoundPropertyModel(itemSearch));
        List<Integer> sizes = Arrays.asList(new Integer[] {5, 10, 15, 25, 50, 100, -1});
        final String noLimit = getLocalizer().getString("item_search_form.noLimit", null);
        DropDownChoice pageSizeChoice = new DropDownChoice("pageSize", sizes, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                return ((Integer) o) == -1 ? noLimit : o.toString();
            }
            public String getIdValue(Object o, int i) {
                return o.toString();
            }
        });
        form.add(pageSizeChoice);        
        form.add(new CheckBox("showHistory"));
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                String refId = itemSearch.getRefId();                
                if(refId != null) {
                    try {
                        new ItemRefId(refId);
                    } catch(InvalidRefIdException e) {
                        form.error(localize("item_search_form.error.refId.invalid"));
                        return;
                    }
                    Item item = getJtrac().loadItemByRefId(refId);
                    if(item == null) {
                        form.error(localize("item_search_form.error.refId.notFound")); 
                        return;
                    }
                    setCurrentItemSearch(null);
                    setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getRefId()));
                    return;
                }
                String searchText = itemSearch.getSearchText();
                if(searchText != null) {
                    if(!getJtrac().validateTextSearchQuery(searchText)) {
                        form.error(localize("item_search_form.error.summary.invalid"));
                        return;
                    }
                }
                setCurrentItemSearch(itemSearch);
                setResponsePage(ItemListPage.class);
            }
        });
        form.add(new ListView("columns", itemSearch.getColumnHeadings()) {
            protected void populateItem(final ListItem listItem) {
                final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                listItem.add(new Label("columnName", ch.getLabel()));
                listItem.add(new CheckBox("visible", new PropertyModel(ch, "visible")));
                DropDownChoice expressionChoice = new DropDownChoice("expression", ch.getValidFilterExpressions(), new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String key = ((Expression) o).getKey();
                        return localize("item_filter." + key);
                    }
                    public String getIdValue(Object o, int i) {
                        return ((Expression) o).getKey();
                    }
                });
                expressionChoice.setModel(new PropertyModel(ch.getFilterCriteria(), "expression"));
                expressionChoice.setNullValid(true);
                listItem.add(expressionChoice);
                final Component fragParent = getFilterUiFragment(ch);                
                fragParent.setOutputMarkupId(true);
                listItem.add(fragParent);
                expressionChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        if(!ch.getFilterCriteria().requiresUiFragmentUpdate()) {
                            return;
                        }
                        Component fragment = getFilterUiFragment(ch);
                        fragment.setOutputMarkupId(true);
                        listItem.replace(fragment);
                        target.addComponent(fragment);
                    }
                });
            }
        });
    }
        
    private Component getFilterUiFragment(ColumnHeading ch) {
        if(ch.getFilterCriteria().getExpression() == null) {
            return new WebMarkupContainer("fragParent");
        } else {
            return ch.getFilterUiFragment(ItemSearchFormPanel.this);
        }
    }
    
}
