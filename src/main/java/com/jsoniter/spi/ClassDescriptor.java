package com.jsoniter.spi;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassDescriptor {

    public Class clazz;
    public Map<String, Type> lookup;
    public ConstructorDescriptor ctor;
    public List<Binding> fields;
    public List<Binding> setters;
    public List<Binding> getters;
    public List<WrapperDescriptor> wrappers;
    public List<Method> unwrappers;
    public boolean asExtraForUnknownProperties;
    public Binding onMissingProperties;
    public Binding onExtraProperties;

    public List<Binding> allBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        if (setters != null) {
            bindings.addAll(setters);
        }
        if (getters != null) {
            bindings.addAll(getters);
        }
        if (ctor != null) {
            bindings.addAll(ctor.parameters);
        }
        if (wrappers != null) {
            for (WrapperDescriptor setter : wrappers) {
                bindings.addAll(setter.parameters);
            }
        }
        return bindings;
    }

    public List<Binding> allDecoderBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        bindings.addAll(setters);
        if (ctor != null) {
            bindings.addAll(ctor.parameters);
        }
        for (WrapperDescriptor setter : wrappers) {
            bindings.addAll(setter.parameters);
        }
        return bindings;
    }

    public List<Binding> allEncoderBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        bindings.addAll(getters);
        return bindings;
    }
}
