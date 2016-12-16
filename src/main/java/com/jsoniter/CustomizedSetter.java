package com.jsoniter;

import java.util.ArrayList;
import java.util.List;

public class CustomizedSetter {
    /**
     * which method to call to set value
     */
    public String methodName;

    /**
     * the parameters to bind
     */
    public List<Binding> parameters = new ArrayList<Binding>();
}
