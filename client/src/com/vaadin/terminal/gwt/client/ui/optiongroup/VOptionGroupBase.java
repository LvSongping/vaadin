/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.terminal.gwt.client.ui.optiongroup;

import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Field;
import com.vaadin.terminal.gwt.client.ui.nativebutton.VNativeButton;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

public abstract class VOptionGroupBase extends Composite implements Field,
        ClickHandler, ChangeHandler, KeyPressHandler, Focusable {

    public static final String CLASSNAME_OPTION = "v-select-option";

    protected ApplicationConnection client;

    protected String paintableId;

    protected Set<String> selectedKeys;

    protected boolean immediate;

    protected boolean multiselect;

    protected boolean disabled;

    protected boolean readonly;

    protected int cols = 0;

    protected int rows = 0;

    protected boolean nullSelectionAllowed = true;

    protected boolean nullSelectionItemAvailable = false;

    /**
     * Widget holding the different options (e.g. ListBox or Panel for radio
     * buttons) (optional, fallbacks to container Panel)
     */
    protected Widget optionsContainer;

    /**
     * Panel containing the component
     */
    protected final Panel container;

    protected VTextField newItemField;

    protected VNativeButton newItemButton;

    public VOptionGroupBase(String classname) {
        container = new FlowPanel();
        initWidget(container);
        optionsContainer = container;
        container.setStyleName(classname);
        immediate = false;
        multiselect = false;
    }

    /*
     * Call this if you wish to specify your own container for the option
     * elements (e.g. SELECT)
     */
    public VOptionGroupBase(Widget w, String classname) {
        this(classname);
        optionsContainer = w;
        container.add(optionsContainer);
    }

    protected boolean isImmediate() {
        return immediate;
    }

    protected boolean isMultiselect() {
        return multiselect;
    }

    protected boolean isDisabled() {
        return disabled;
    }

    protected boolean isReadonly() {
        return readonly;
    }

    protected boolean isNullSelectionAllowed() {
        return nullSelectionAllowed;
    }

    protected boolean isNullSelectionItemAvailable() {
        return nullSelectionItemAvailable;
    }

    /**
     * @return "cols" specified in uidl, 0 if not specified
     */
    protected int getColumns() {
        return cols;
    }

    /**
     * @return "rows" specified in uidl, 0 if not specified
     */

    protected int getRows() {
        return rows;
    }

    abstract protected void setTabIndex(int tabIndex);

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == newItemButton
                && !newItemField.getText().equals("")) {
            client.updateVariable(paintableId, "newitem",
                    newItemField.getText(), true);
            newItemField.setText("");
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        if (multiselect) {
            client.updateVariable(paintableId, "selected", getSelectedItems(),
                    immediate);
        } else {
            client.updateVariable(paintableId, "selected", new String[] { ""
                    + getSelectedItem() }, immediate);
        }
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        if (event.getSource() == newItemField
                && event.getCharCode() == KeyCodes.KEY_ENTER) {
            newItemButton.click();
        }
    }

    protected abstract void buildOptions(UIDL uidl);

    protected abstract String[] getSelectedItems();

    protected String getSelectedItem() {
        final String[] sel = getSelectedItems();
        if (sel.length > 0) {
            return sel[0];
        } else {
            return null;
        }
    }

}