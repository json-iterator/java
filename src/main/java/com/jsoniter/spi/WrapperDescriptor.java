package com.jsoniter.spi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WrapperDescriptor {
    /**
     * which method to call to set value
     */
    public String methodName;

    /**
     * the parameters to bind
     */
    public List<Binding> parameters = new ArrayList<Binding>();

    // optional
    public Method method;
}
