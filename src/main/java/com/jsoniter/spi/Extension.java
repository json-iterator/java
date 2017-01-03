package com.jsoniter.spi;

import java.lang.reflect.Type;

public interface Extension {
    /**
     * Choose the implementation class for interface types
     *
     * @param type the type to decode to, could be class or parameterized type
     * @return the implementation type to use
     */
    Type chooseImplementation(Type type);

    /**
     * Can this extension create object instance for given interface type
     *
     * @param clazz the interface
     * @return true if can create, false if can not
     */
    boolean canCreate(Class clazz);

    /**
     * Create object instance for given interface type
     *
     * @param clazz the interface
     * @return the object instance, throw exception if can not create
     */
    Object create(Class clazz);

    /**
     * Customize type decoding
     *
     * @param cacheKey cacheKey parameter
     * @param type     change how to decode the type
     * @return null, if no special customization needed
     */
    Decoder createDecoder(String cacheKey, Type type);

    // TODO: add createEncoder

    /**
     * Update binding is done for the class
     *
     * @param desc binding information
     */
    void updateClassDescriptor(ClassDescriptor desc);
}
