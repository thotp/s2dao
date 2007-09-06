/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.dao.AnnotationReaderFactory;
import org.seasar.dao.BeanAnnotationReader;
import org.seasar.dao.ColumnNaming;
import org.seasar.dao.DtoMetaData;
import org.seasar.dao.DtoMetaDataFactory;
import org.seasar.dao.PropertyTypeFactory;
import org.seasar.dao.PropertyTypeFactoryBuilder;
import org.seasar.dao.ValueTypeFactory;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author higa
 *
 */
public class DtoMetaDataFactoryImpl implements DtoMetaDataFactory, Disposable {

    public static final String annotationReaderFactory_BINDING = "bindingType=must";

    public static final String valueTypeFactory_BINDING = "bindingType=must";

    public static final String propertyTypeFactoryBuilder_BINDING = "bindingType=must";

    public static final String columnNaming_BINDING = "bindingType=must";

    protected AnnotationReaderFactory annotationReaderFactory;

    protected ValueTypeFactory valueTypeFactory;

    protected PropertyTypeFactoryBuilder propertyTypeFactoryBuilder;

    protected ColumnNaming columnNaming;

    private Map cache = new HashMap(100);

    protected boolean initialized = false;

    public AnnotationReaderFactory getAnnotationReaderFactory() {
        return annotationReaderFactory;
    }

    public void setAnnotationReaderFactory(
            AnnotationReaderFactory annotationReaderFactory) {
        this.annotationReaderFactory = annotationReaderFactory;
    }

    /**
     * @return Returns the valueTypeFactory.
     */
    public ValueTypeFactory getValueTypeFactory() {
        return valueTypeFactory;
    }

    /**
     * @param valueTypeFactory The valueTypeFactory to set.
     */
    public void setValueTypeFactory(ValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    public void setPropertyTypeFactoryBuilder(
            PropertyTypeFactoryBuilder propertyTypeFactoryBuilder) {
        this.propertyTypeFactoryBuilder = propertyTypeFactoryBuilder;
    }

    public void setColumnNaming(ColumnNaming columnNaming) {
        this.columnNaming = columnNaming;
    }

    public synchronized DtoMetaData getDtoMetaData(Class dtoClass) {
        if (!initialized) {
            DisposableUtil.add(this);
            initialized = true;
        }
        String key = dtoClass.getName();
        DtoMetaDataImpl dmd = (DtoMetaDataImpl) cache.get(key);
        if (dmd != null) {
            return dmd;
        }
        BeanAnnotationReader reader = annotationReaderFactory
                .createBeanAnnotationReader(dtoClass);
        PropertyTypeFactory propertyTypeFactory = createPropertyTypeFactory(
                dtoClass, reader);
        dmd = new DtoMetaDataImpl();
        dmd.setBeanClass(dtoClass);
        dmd.setBeanAnnotationReader(reader);
        dmd.setPropertyTypeFactory(propertyTypeFactory);
        dmd.initialize();
        cache.put(key, dmd);
        return dmd;
    }

    public synchronized void dispose() {
        cache.clear();
        initialized = false;
    }

    protected PropertyTypeFactory createPropertyTypeFactory(Class dtoClass,
            BeanAnnotationReader beanAnnotationReader) {
        return propertyTypeFactoryBuilder.build(dtoClass, beanAnnotationReader,
                valueTypeFactory, columnNaming);
    }
}
