package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

public class ReflectionDecoder implements Decoder {

    private static Object NOT_SET = new Object() {
        @Override
        public String toString() {
            return "NOT_SET";
        }
    };
    private Map<Slice, Binding> allBindings = new HashMap<Slice, Binding>();
    private String tempCacheKey;
    private String ctorArgsCacheKey;
    private int tempCount;
    private long expectedTracker;
    private int requiredIdx;
    private int tempIdx;
    private ClassDescriptor desc;

    public ReflectionDecoder(Class clazz) {
        try {
            init(clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private final void init(Class clazz) throws Exception {
        ClassDescriptor desc = ExtensionManager.getClassDescriptor(clazz, true);
        String cacheKey = TypeLiteral.create(clazz).getDecoderCacheKey();
        for (Binding param : desc.ctor.parameters) {
            addBinding(cacheKey, clazz, param);
        }
        this.desc = desc;
        if (desc.ctor.ctor == null && desc.ctor.staticFactory == null) {
            throw new JsonException("no constructor for: " + desc.clazz);
        }
        for (Binding field : desc.fields) {
            addBinding(cacheKey, clazz, field);
        }
        for (SetterDescriptor setter : desc.setters) {
            for (Binding param : setter.parameters) {
                addBinding(cacheKey, clazz, param);
            }
        }
        if (requiredIdx > 63) {
            throw new JsonException("too many required properties to track");
        }
        expectedTracker = Long.MAX_VALUE >> (63 - requiredIdx);
        if (!desc.ctor.parameters.isEmpty() || !desc.setters.isEmpty()) {
            tempCount = tempIdx;
            tempCacheKey = "temp@" + clazz.getCanonicalName();
            ctorArgsCacheKey = "ctor@" + clazz.getCanonicalName();
        }
    }

    private void addBinding(String cacheKey, Class clazz, final Binding binding) {
        if (binding.failOnMissing) {
            binding.mask = 1L << requiredIdx;
            requiredIdx++;
        }
        if (binding.failOnPresent) {
            binding.decoder = new Decoder() {
                @Override
                public Object decode(JsonIterator iter) throws IOException {
                    throw new JsonException("found should not present property: " + binding.name);
                }
            };
        }
        String fieldCacheKey = binding.name + "@" + cacheKey;
        if (binding.decoder == null) {
            // the field decoder might be registered directly
            binding.decoder = ExtensionManager.getDecoder(fieldCacheKey);
        }
        binding.idx = tempIdx;
        for (String fromName : binding.fromNames) {
            Slice slice = Slice.make(fromName);
            if (allBindings.containsKey(slice)) {
                throw new JsonException("name conflict found in " + clazz + ": " + fromName);
            }
            allBindings.put(slice, binding);
        }
        tempIdx++;
    }

    @Override
    public final Object decode(JsonIterator iter) throws IOException {
        try {
            if (desc.ctor.parameters.isEmpty()) {
                if (desc.setters.isEmpty()) {
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
            if (requiredIdx > 0) {
                throw new JsonException("missing required properties: " + collectMissingFields(0));
            }
            return obj;
        }
        long tracker = 0L;
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            onUnknownProperty(iter, fieldName);
        } else {
            if (binding.failOnMissing) {
                tracker |= binding.mask;
            }
            binding.field.set(obj, decode(iter, binding));
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                onUnknownProperty(iter, fieldName);
            } else {
                if (binding.failOnMissing) {
                    tracker |= binding.mask;
                }
                binding.field.set(obj, decode(iter, binding));
            }
        }
        if (tracker != expectedTracker) {
            throw new JsonException("missing required properties: " + collectMissingFields(tracker));
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
            if (requiredIdx > 0) {
                throw new JsonException("missing required properties: " + collectMissingFields(0));
            }
            return createNewObject(iter, temp);
        }
        long tracker = 0L;
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            onUnknownProperty(iter, fieldName);
        } else {
            if (binding.failOnMissing) {
                tracker |= binding.mask;
            }
            temp[binding.idx] = decode(iter, binding);
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                onUnknownProperty(iter, fieldName);
            } else {
                if (binding.failOnMissing) {
                    tracker |= binding.mask;
                }
                temp[binding.idx] = decode(iter, binding);
            }
        }
        if (tracker != expectedTracker) {
            throw new JsonException("missing required properties: " + collectMissingFields(tracker));
        }
        Object obj = createNewObject(iter, temp);
        for (Binding field : desc.fields) {
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
            if (requiredIdx > 0) {
                throw new JsonException("missing required properties: " + collectMissingFields(0));
            }
            return obj;
        }
        long tracker = 0L;
        Object[] temp = (Object[]) iter.tempObjects.get(tempCacheKey);
        if (temp == null) {
            temp = new Object[tempCount];
            iter.tempObjects.put(tempCacheKey, temp);
        }
        Arrays.fill(temp, NOT_SET);
        Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
        Binding binding = allBindings.get(fieldName);
        if (binding == null) {
            onUnknownProperty(iter, fieldName);
        } else {
            if (binding.failOnMissing) {
                tracker |= binding.mask;
            }
            if (binding.field == null) {
                temp[binding.idx] = decode(iter, binding);
            } else {
                binding.field.set(obj, decode(iter, binding));
            }
        }
        while (CodegenAccess.nextToken(iter) == ',') {
            fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            binding = allBindings.get(fieldName);
            if (binding == null) {
                onUnknownProperty(iter, fieldName);
            } else {
                if (binding.failOnMissing) {
                    tracker |= binding.mask;
                }
                if (binding.field == null) {
                    temp[binding.idx] = decode(iter, binding);
                } else {
                    binding.field.set(obj, decode(iter, binding));
                }
            }
        }
        if (tracker != expectedTracker) {
            throw new JsonException("missing required properties: " + collectMissingFields(tracker));
        }
        applySetters(temp, obj);
        return obj;
    }

    private Object decode(JsonIterator iter, Binding binding) throws IOException {
        Object value;
        if (binding.decoder == null) {
            value = CodegenAccess.read(iter, binding.valueTypeLiteral);
        } else {
            value = binding.decoder.decode(iter);
        }
        return value;
    }

    private void onUnknownProperty(JsonIterator iter, Slice fieldName) throws IOException {
        if (desc.failOnUnknownFields) {
            throw new JsonException("unknown property: " + fieldName.toString());
        } else {
            iter.skip();
        }
    }

    private List<String> collectMissingFields(long tracker) {
        List<String> missingFields = new ArrayList<String>();
        for (Binding binding : allBindings.values()) {
            if (binding.failOnMissing) {
                long mask = binding.mask;
                CodegenAccess.addMissingField(missingFields, tracker, mask, binding.name);
            }
        }
        return missingFields;
    }

    private void applySetters(Object[] temp, Object obj) throws Exception {
        for (SetterDescriptor setter : desc.setters) {
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
            ctorArgs = new Object[desc.ctor.parameters.size()];
            iter.tempObjects.put(ctorArgsCacheKey, ctorArgs);
        }
        Arrays.fill(ctorArgs, null);
        for (int i = 0; i < desc.ctor.parameters.size(); i++) {
            Object arg = temp[desc.ctor.parameters.get(i).idx];
            if (arg != NOT_SET) {
                ctorArgs[i] = arg;
            }
        }
        return createNewObject(ctorArgs);
    }

    private Object createNewObject(Object... args) throws Exception {
        if (desc.ctor.staticFactory != null) {
            return desc.ctor.staticFactory.invoke(null, args);
        } else {
            return desc.ctor.ctor.newInstance(args);
        }
    }
}
