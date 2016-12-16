package com.jsoniter;

import java.util.List;

public interface Extension {
    /**
     * Customize field of certain kind, for example having certain annotation
     *
     * @param field the field reflection object
     * @return null, if no special customization needed
     */
    Decoder createDecoder(Binding field);

    /**
     * Customize the binding source
     *
     * @param field the field reflection object
     * @return null, if fallback to default behavior. empty array to disable this binding
     */
    String[] getBindFrom(Binding field);

    /**
     * Customize which constructor to call
     *
     * @param clazz the instance class to create
     * @return null, if fallback to default behavior
     */
    CustomizedConstructor getConstructor(Class clazz);

    /**
     * Customize setters to call after instance is created and fields set
     *
     * @param clazz the class that is binding
     * @return null, if fallback to default behavior
     */
    List<CustomizedSetter> getSetters(Class clazz);
}
