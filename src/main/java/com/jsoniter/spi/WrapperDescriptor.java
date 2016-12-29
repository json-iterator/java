package com.jsoniter.spi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WrapperDescriptor {

    /**
     * the parameters to bind
     */
    public List<Binding> parameters = new ArrayList<Binding>();

    public Method method;
}
