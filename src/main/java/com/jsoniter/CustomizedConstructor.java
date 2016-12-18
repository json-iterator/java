package com.jsoniter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CustomizedConstructor {
    /**
     * set to null if use constructor
     * otherwise use static method
     */
    public String staticMethodName;
    // optional
    public Constructor ctor;

    /**
     * the parameters to call constructor or static method
     */
    public List<Binding> parameters = new ArrayList<Binding>();
}
