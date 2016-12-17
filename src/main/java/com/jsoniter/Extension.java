package com.jsoniter;

import java.lang.reflect.Type;
import java.util.List;

public interface Extension {
    /**
     * Customize type decoding
     *
     *
     * @param cacheKey
     * @param type change how to decode the type
     * @return null, if no special customization needed
     */
    Decoder createDecoder(String cacheKey, Type type);

    /**
     * Customize the binding source or decoder
     *
     * @param field binding information
     * @return true, if stops other extension from customizing same field
     */
    boolean updateBinding(Binding field);

    /**
     * Customize which constructor to call
     *
     * @param clazz the class of instance to create
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
