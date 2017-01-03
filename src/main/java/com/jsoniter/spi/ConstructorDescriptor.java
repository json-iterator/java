package com.jsoniter.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConstructorDescriptor {
    /**
     * set to null if use constructor
     * otherwise use static method
     */
    public String staticMethodName;
    // option 1: use constructor
    public Constructor ctor;
    // option 2: use static method
    public Method staticFactory;
    // option 3: create by extension
    public Extension objectFactory;

    /**
     * the parameters to call constructor or static method
     */
    public List<Binding> parameters = new ArrayList<Binding>();

    @Override
    public String toString() {
        return "ConstructorDescriptor{" +
                "staticMethodName='" + staticMethodName + '\'' +
                ", ctor=" + ctor +
                ", staticFactory=" + staticFactory +
                ", parameters=" + parameters +
                '}';
    }
}
