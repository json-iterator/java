package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionDecoder implements Decoder {

    private static Object NOT_SET = new Object() {
        @Override
        public String toString() {
            return "NOT_SET";
        }
    };
    private Constructor ctor;
    private Method staticFactory;
    private Map<Slice, Binding> allBindings = new HashMap<Slice, Binding>();
    private List<Binding> ctorParams = new ArrayList<Binding>();
    private List<Binding> fields;
    private List<SetterDescriptor> setters;
    private String tempCacheKey;
    private String ctorArgsCacheKey;
    private int tempCount;

    public ReflectionDecoder(Class clazz) {
        try {
            init(clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private final void init(Class clazz) throws Exception {
        ClassDescriptor desc = ExtensionManager.getClassDescriptor(clazz, true);
        ctorParams = desc.ctor.parameters;
        int tempIdx = 0;
        for (Binding param : ctorParams) {
            tempIdx = addBinding(clazz, tempIdx, param);
        }
        this.ctor = desc.ctor.ctor;
        this.staticFactory = desc.ctor.staticFactory;
        if (this.ctor == null && this.staticFactory == null) {
            throw new JsonException("no constructor for: " + desc.clazz);
        }
        fields = desc.fields;
        for (Binding field : fields) {
            tempIdx = addBinding(clazz, tempIdx, field);
        }
        setters = desc.setters;
        for (SetterDescriptor setter : setters) {
            for (Binding param : setter.parameters) {
                tempIdx = addBinding(clazz, tempIdx, param);
            }
        }
        if (!ctorParams.isEmpty() || !setters.isEmpty()) {
            tempCount = tempIdx;
            tempCacheKey = "temp@" + clazz.getCanonicalName();
            ctorArgsCacheKey = "ctor@" + clazz.getCanonicalName();
        }
    }

    private int addBinding(Class clazz, int tempIdx, Binding param) {
        param.idx = tempIdx;
        for (String fromName : param.fromNames) {
            Slice slice = Slice.make(fromName);
            if (allBindings.containsKey(slice)) {
                throw new JsonException("name conflict found in " + clazz + ": " + fromName);
            }
            allBindings.put(slice, param);
        }
        tempIdx++;
        return tempIdx;
    }

    @Override
    public final Object decode(JsonIterator iter) throws IOException {
        try {
            if (ctorParams.isEmpty()) {
                if (setters.isEmpty()) {
                    return decodeWithOnlyFieldBinding(iter);
                } else {
                    return decodeWithSetterBinding(iter);
                }
            } else {
                return decodeWithCtorBinding(iter);
            }
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private final Object decodeWithOnlyFieldBinding(JsonIterator iter) throws Exception {
        if (iter.readNull()) {
            CodegenAccess.resetExistingObject(iter);
            return null;
        }
        Object obj = CodegenAccess.existingObject(iter) == null ? createNewObject() : CodegenAccess.resetExistingObject(iter);
        if (!CodegenAccess.readObjectStart(iter)) {
            return obj;
        }
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            iter.skip();
        } else {
            binding.field.set(obj, CodegenAccess.read(iter, binding.valueTypeLiteral));
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
            } else {
                binding.field.set(obj, CodegenAccess.read(iter, binding.valueTypeLiteral));
            }
        }
        return obj;
    }

    private final Object decodeWithCtorBinding(JsonIterator iter) throws Exception {
        if (iter.readNull()) {
            CodegenAccess.resetExistingObject(iter);
            return null;
        }
        Object[] temp = (Object[]) iter.tempObjects.get(tempCacheKey);
        if (temp == null) {
            temp = new Object[tempCount];
            iter.tempObjects.put(tempCacheKey, temp);
        }
        Arrays.fill(temp, NOT_SET);
        if (!CodegenAccess.readObjectStart(iter)) {
            return createNewObject(iter, temp);
        }
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            iter.skip();
        } else {
            temp[binding.idx] = CodegenAccess.read(iter, binding.valueTypeLiteral);
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
            } else {
                temp[binding.idx] = CodegenAccess.read(iter, binding.valueTypeLiteral);
            }
        }
        Object obj = createNewObject(iter, temp);
        for (Binding field : fields) {
            Object val = temp[field.idx];
            if (val != NOT_SET) {
                field.field.set(obj, val);
            }
        }
        applySetters(temp, obj);
        return obj;
    }

    private final Object decodeWithSetterBinding(JsonIterator iter) throws Exception {
        if (iter.readNull()) {
            CodegenAccess.resetExistingObject(iter);
            return null;
        }
        Object obj = createNewObject();
        if (!CodegenAccess.readObjectStart(iter)) {
            return obj;
        }
        Object[] temp = (Object[]) iter.tempObjects.get(tempCacheKey);
        if (temp == null) {
            temp = new Object[tempCount];
            iter.tempObjects.put(tempCacheKey, temp);
        }
        Arrays.fill(temp, NOT_SET);
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            iter.skip();
        } else {
            if (binding.field == null) {
                temp[binding.idx] = CodegenAccess.read(iter, binding.valueTypeLiteral);
            } else {
                binding.field.set(obj, CodegenAccess.read(iter, binding.valueTypeLiteral));
            }
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
            } else {
                if (binding.field == null) {
                    temp[binding.idx] = CodegenAccess.read(iter, binding.valueTypeLiteral);
                } else {
                    binding.field.set(obj, CodegenAccess.read(iter, binding.valueTypeLiteral));
                }
            }
        }
        applySetters(temp, obj);
        return obj;
    }

    private void applySetters(Object[] temp, Object obj) throws Exception {
        for (SetterDescriptor setter : setters) {
            Object[] args = new Object[setter.parameters.size()];
            for (int i = 0; i < setter.parameters.size(); i++) {
                args[i] = temp[setter.parameters.get(i).idx];
            }
            setter.method.invoke(obj, args);
        }
    }

    private Object createNewObject(JsonIterator iter, Object[] temp) throws Exception {
        Object[] ctorArgs = (Object[]) iter.tempObjects.get(ctorArgsCacheKey);
        if (ctorArgs == null) {
            ctorArgs = new Object[ctorParams.size()];
            iter.tempObjects.put(ctorArgsCacheKey, ctorArgs);
        }
        Arrays.fill(ctorArgs, null);
        for (int i = 0; i < ctorParams.size(); i++) {
            Object arg = temp[ctorParams.get(i).idx];
            if (arg != NOT_SET) {
                ctorArgs[i] = arg;
            }
        }
        return createNewObject(ctorArgs);
    }

    private Object createNewObject(Object... args) throws Exception {
        if (ctor == null) {
            return staticFactory.invoke(null, args);
        } else {
            return ctor.newInstance(args);
        }
    }
}
