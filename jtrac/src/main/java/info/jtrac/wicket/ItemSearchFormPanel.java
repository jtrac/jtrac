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

import info.jtrac.Jtrac;
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
import org.apache.wicket.markup.html.link.Link;
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
    private boolean expandAll;
    
    public ItemSearchFormPanel(String id, User user) {
        super(id);
        this.itemSearch = new ItemSearch(user);
        addComponents();
    }
    
    public ItemSearchFormPanel(String id) {
        super(id);
        Space s = getCurrentSpace();
        if(s != null) {
            this.itemSearch = new ItemSearch(s);
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
        form.add(new FeedbackPanel("feedback"));
        form.setModel(new CompoundPropertyModel(itemSearch));
        List<Integer> sizes = Arrays.asList(new Integer[] {5, 10, 15, 25, 50, 100, -1});        
        DropDownChoice pageSizeChoice = new DropDownChoice("pageSize", sizes, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                return ((Integer) o) == -1 ? localize("item_search_form.noLimit") : o.toString();
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
                    if(getCurrentSpace() != null) {
                        // user can save typing by entering the refId number without the space prefixCode
                        try { 
                            long id = Long.parseLong(refId);
                            refId = getCurrentSpace().getPrefixCode() + "-" + id;
                        } catch(Exception e) {
                            // oops that didn't work, continue
                        }
                    }
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
                setResponsePage(ItemListPage.class, itemSearch.getAsQueryString());
            }
        });
        form.add(new Link("expandAll") {
            public void onClick() {
                expandAll = true;                
            }
            @Override
            public boolean isVisible() {
                return !expandAll;
            }
        });
        form.add(new ListView("columns", itemSearch.getColumnHeadings()) {
            protected void populateItem(final ListItem listItem) {
                final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                String label = ch.isField() ? ch.getLabel() : localize("item_list." + ch.getName());
                listItem.add(new Label("columnName", label));
                listItem.add(new CheckBox("visible", new PropertyModel(ch, "visible")));
                List<Expression> validExpressions = ch.getValidFilterExpressions();
                DropDownChoice expressionChoice = new IndicatingDropDownChoice("expression", validExpressions, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String key = ((Expression) o).getKey();
                        return localize("item_filter." + key);
                    }
                    public String getIdValue(Object o, int i) {
                        return ((Expression) o).getKey();
                    }
                });
                // always pre-select "equal to" for filter criteria on ID
                if(ch.getName() == ColumnHeading.Name.ID) {
                    ch.getFilterCriteria().setExpression(Expression.EQ);
                }                                
                if(expandAll && ch.getFilterCriteria().getExpression() == null) {                  
                    ch.getFilterCriteria().setExpression(validExpressions.get(0));                                 
                }
                expressionChoice.setModel(new PropertyModel(ch.getFilterCriteria(), "expression"));
                expressionChoice.setNullValid(true);
                listItem.add(expressionChoice);
                Component fragParent = null;
                fragParent = getFilterUiFragment(ch);
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
                        target.appendJavascript("document.getElementById('" + fragment.getMarkupId() + "').focus()");
                    }
                });
            }            
        });
    }
        
    private Component getFilterUiFragment(ColumnHeading ch) {
        if(ch.getFilterCriteria().getExpression() == null) {
            return new WebMarkupContainer("fragParent");
        }        
        User user = JtracSession.get().getUser();
        // the space could be null also
        Space space = JtracSession.get().getCurrentSpace();
        Jtrac jtrac = JtracApplication.get().getJtrac();
        return ch.getFilterUiFragment(this, user, space, jtrac);
    }

}
