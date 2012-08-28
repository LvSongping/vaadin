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

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.user.rebind.SourceWriter;

public class FieldProperty extends Property {

    private FieldProperty(JClassType beanType, JField field) {
        super(field.getName(), beanType, field.getType());
    }

    @Override
    public void writeSetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable, String valueVariable) {
        w.print("((%s) %s).%s = (%s)%s;", getBeanType()
                .getQualifiedSourceName(), beanVariable, getName(),
                getUnboxedPropertyTypeName(), valueVariable);
    }

    @Override
    public void writeGetterBody(TreeLogger logger, SourceWriter w,
            String beanVariable) {
        w.print("return ((%s) %s).%s;", getBeanType().getQualifiedSourceName(),
                beanVariable, getName());
    }

    public static Collection<FieldProperty> findProperties(JClassType type) {
        Collection<FieldProperty> properties = new ArrayList<FieldProperty>();

        List<JField> fields = getPublicFields(type);
        for (JField field : fields) {
            properties.add(new FieldProperty(type, field));
        }

        return properties;
    }

    private static List<JField> getPublicFields(JClassType type) {
        Set<String> names = new HashSet<String>();
        ArrayList<JField> fields = new ArrayList<JField>();
        for (JClassType subType : type.getFlattenedSupertypeHierarchy()) {
            JField[] subFields = subType.getFields();
            for (JField field : subFields) {
                if (field.isPublic() && !field.isStatic()
                        && names.add(field.getName())) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

}