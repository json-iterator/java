package com.jsoniter.spi;

import java.util.ArrayList;
import java.util.List;

public class ClassDescriptor {
    public Class clazz;
    public ConstructorDescriptor ctor;
    public List<Binding> fields;
    public List<SetterDescriptor> setters;
    public List<Binding> getters;
    public boolean failOnUnknownFields;

    public List<Binding> allDecoderBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        if (ctor != null) {
            bindings.addAll(ctor.parameters);
        }
        for (SetterDescriptor setter : setters) {
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
