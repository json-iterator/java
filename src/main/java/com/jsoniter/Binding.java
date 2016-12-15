package com.jsoniter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Binding {
    public Class clazz;
    public Field field; // might be null
    public String[] fromNames;
    public String name;
    public Type valueType;
}
