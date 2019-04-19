package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

class ReflectionObjectDecoder {

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

    public ReflectionObjectDecoder(ClassInfo classInfo) {
        try {
            init(classInfo);
        } catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private final void init(ClassInfo classInfo) throws Exception {
        Class clazz = classInfo.clazz;
        ClassDescriptor desc = ClassDescriptor.getDecodingClassDescriptor(classInfo, true);
        for (Binding param : desc.ctor.parameters) {
            addBinding(classInfo, param);
        }
        this.desc = desc;
        if (desc.ctor.objectFactory == null && desc.ctor.ctor == null && desc.ctor.staticFactory == null) {
            throw new JsonException("no constructor for: " + desc.clazz);
        }
        for (Binding field : desc.fields) {
            addBinding(classInfo, field);
        }
        for (Binding setter : desc.setters) {
            addBinding(classInfo, setter);
        }
        for (WrapperDescriptor setter : desc.bindingTypeWrappers) {
            for (Binding param : setter.parameters) {
                addBinding(classInfo, param);
            }
        }
        if (requiredIdx > 63) {
            throw new JsonException("too many required properties to track");
        }
        expectedTracker = Long.MAX_VALUE >> (63 - requiredIdx);
        if (!desc.ctor.parameters.isEmpty() || !desc.bindingTypeWrappers.isEmpty()) {
            tempCount = tempIdx;
            tempCacheKey = "temp@" + clazz.getCanonicalName();
            ctorArgsCacheKey = "ctor@" + clazz.getCanonicalName();
        }
    }

    private void addBinding(ClassInfo classInfo, final Binding binding) {
        if (binding.fromNames.length == 0) {
            return;
        }
        if (binding.asMissingWhenNotPresent) {
            binding.mask = 1L << requiredIdx;
            requiredIdx++;
        }
        if (binding.asExtraWhenPresent) {
            binding.decoder = new Decoder() {
                @Override
                public Object decode(JsonIterator iter) throws IOException {
                    throw new JsonException("found should not present property: " + binding.name);
                }
            };
        }
        if (binding.decoder == null) {
            // field decoder might be special customized
            binding.decoder = JsoniterSpi.getDecoder(binding.decoderCacheKey());
        }
        if (binding.decoder == null) {
            binding.decoder = Codegen.getDecoder(binding.valueTypeLiteral.getDecoderCacheKey(), binding.valueType);
        }
        binding.idx = tempIdx;
        for (String fromName : binding.fromNames) {
            Slice slice = Slice.make(fromName);
            if (allBindings.containsKey(slice)) {
                throw new JsonException("name conflict found in " + classInfo.clazz + ": " + fromName);
            }
            allBindings.put(slice, binding);
        }
        tempIdx++;
    }

    public Decoder create() {
        if (desc.ctor.parameters.isEmpty()) {
            if (desc.bindingTypeWrappers.isEmpty()) {
                return new OnlyField();
            } else {
                return new WithWrapper();
            }
        } else {
            return new WithCtor();
        }
    }

    public class OnlyField implements Decoder {

        public Object decode(JsonIterator iter) throws IOException {
            try {
                return decode_(iter);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }

        private Object decode_(JsonIterator iter) throws Exception {
            if (iter.readNull()) {
                CodegenAccess.resetExistingObject(iter);
                return null;
            }
            Object obj = CodegenAccess.existingObject(iter) == null ? createNewObject() : CodegenAccess.resetExistingObject(iter);
            if (!CodegenAccess.readObjectStart(iter)) {
                if (requiredIdx > 0) {
                    if (desc.onMissingProperties == null) {
                        throw new JsonException("missing required properties: " + collectMissingFields(0));
                    } else {
                        setToBinding(obj, desc.onMissingProperties, collectMissingFields(0));
                    }
                }
                return obj;
            }
            Map<String, Object> extra = null;
            long tracker = 0L;
            Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                extra = onUnknownProperty(iter, fieldName, extra);
            } else {
                if (binding.asMissingWhenNotPresent) {
                    tracker |= binding.mask;
                }
                setToBinding(obj, binding, decodeBinding(iter, obj, binding));
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
                binding = allBindings.get(fieldName);
                if (binding == null) {
                    extra = onUnknownProperty(iter, fieldName, extra);
                } else {
                    if (binding.asMissingWhenNotPresent) {
                        tracker |= binding.mask;
                    }
                    setToBinding(obj, binding, decodeBinding(iter, obj, binding));
                }
            }
            if (tracker != expectedTracker) {
                if (desc.onMissingProperties == null) {
                    throw new JsonException("missing required properties: " + collectMissingFields(tracker));
                } else {
                    setToBinding(obj, desc.onMissingProperties, collectMissingFields(tracker));
                }
            }
            setExtra(obj, extra);
            return obj;
        }
    }

    public class WithCtor implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            try {
                return decode_(iter);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }

        private Object decode_(JsonIterator iter) throws Exception {
            if (iter.readNull()) {
                CodegenAccess.resetExistingObject(iter);
                return null;
            }
            if (iter.tempObjects == null) {
                iter.tempObjects = new HashMap<String, Object>();
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
            Map<String, Object> extra = null;
            long tracker = 0L;
            Slice fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                extra = onUnknownProperty(iter, fieldName, extra);
            } else {
                if (binding.asMissingWhenNotPresent) {
                    tracker |= binding.mask;
                }
                temp[binding.idx] = decodeBinding(iter, binding);
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
                binding = allBindings.get(fieldName);
                if (binding == null) {
                    extra = onUnknownProperty(iter, fieldName, extra);
                } else {
                    if (binding.asMissingWhenNotPresent) {
                        tracker |= binding.mask;
                    }
                    temp[binding.idx] = decodeBinding(iter, binding);
                }
            }
            if (tracker != expectedTracker) {
                throw new JsonException("missing required properties: " + collectMissingFields(tracker));
            }
            Object obj = createNewObject(iter, temp);
            setExtra(obj, extra);
            for (Binding field : desc.fields) {
                Object val = temp[field.idx];
                if (val != NOT_SET && field.fromNames.length > 0) {
                    field.field.set(obj, val);
                }
            }
            for (Binding setter : desc.setters) {
                Object val = temp[setter.idx];
                if (val != NOT_SET) {
                    setter.method.invoke(obj, val);
                }
            }
            applyWrappers(temp, obj);
            return obj;
        }
    }

    public class WithWrapper implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            try {
                return decode_(iter);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }

        private Object decode_(JsonIterator iter) throws Exception {
            if (iter.readNull()) {
                CodegenAccess.resetExistingObject(iter);
                return null;
            }
            Object obj = createNewObject();
            if (!CodegenAccess.readObjectStart(iter)) {
                if (requiredIdx > 0) {
                    if (desc.onMissingProperties == null) {
                        throw new JsonException("missing required properties: " + collectMissingFields(0));
                    } else {
                        setToBinding(obj, desc.onMissingProperties, collectMissingFields(0));
                    }
                }
                return obj;
            }
            Map<String, Object> extra = null;
            long tracker = 0L;
            if (iter.tempObjects == null) {
                iter.tempObjects = new HashMap<String, Object>();
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
                extra = onUnknownProperty(iter, fieldName, extra);
            } else {
                if (binding.asMissingWhenNotPresent) {
                    tracker |= binding.mask;
                }
                if (canNotSetDirectly(binding)) {
                    temp[binding.idx] = decodeBinding(iter, obj, binding);
                } else {
                    setToBinding(obj, binding, decodeBinding(iter, obj, binding));
                }
            }
            while (CodegenAccess.nextToken(iter) == ',') {
                fieldName = CodegenAccess.readObjectFieldAsSlice(iter);
                binding = allBindings.get(fieldName);
                if (binding == null) {
                    extra = onUnknownProperty(iter, fieldName, extra);
                } else {
                    if (binding.asMissingWhenNotPresent) {
                        tracker |= binding.mask;
                    }
                    if (canNotSetDirectly(binding)) {
                        temp[binding.idx] = decodeBinding(iter, obj, binding);
                    } else {
                        setToBinding(obj, binding, decodeBinding(iter, obj, binding));
                    }
                }
            }
            if (tracker != expectedTracker) {
                if (desc.onMissingProperties == null) {
                    throw new JsonException("missing required properties: " + collectMissingFields(tracker));
                } else {
                    setToBinding(obj, desc.onMissingProperties, collectMissingFields(tracker));
                }
            }
            setExtra(obj, extra);
            applyWrappers(temp, obj);
            return obj;
        }
    }

    private void setToBinding(Object obj, Binding binding, Object value) throws Exception {
        if (binding.field != null) {
            binding.field.set(obj, value);
        } else {
            binding.method.invoke(obj, value);
        }
    }

    private void setExtra(Object obj, Map<String, Object> extra) throws Exception {
        if (extra == null) {
            return;
        }
        if (desc.asExtraForUnknownProperties) {
            if (desc.onExtraProperties == null) {
                for (String fieldName : extra.keySet()) {
                    throw new JsonException("unknown property: " + fieldName);
                }
            } else {
                setToBinding(obj, desc.onExtraProperties, extra);
            }
        }
        for (Method wrapper : desc.keyValueTypeWrappers) {
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                Any value = (Any) entry.getValue();
                wrapper.invoke(obj, entry.getKey(), value.object());
            }
        }
    }

    private boolean canNotSetDirectly(Binding binding) {
        return binding.field == null && binding.method == null;
    }

    private Object decodeBinding(JsonIterator iter, Binding binding) throws Exception {
        Object value;
        value = binding.decoder.decode(iter);
        return value;
    }

    private Object decodeBinding(JsonIterator iter, Object obj, Binding binding) throws Exception {
        if (binding.valueCanReuse) {
            CodegenAccess.setExistingObject(iter, binding.field.get(obj));
        }
        return decodeBinding(iter, binding);
    }

    private Map<String, Object> onUnknownProperty(JsonIterator iter, Slice fieldName, Map<String, Object> extra) throws IOException {
        boolean shouldReadValue = desc.asExtraForUnknownProperties || !desc.keyValueTypeWrappers.isEmpty();
        if (shouldReadValue) {
            Any value = iter.readAny();
            if (extra == null) {
                extra = new HashMap<String, Object>();
            }
            extra.put(fieldName.toString(), value);
        } else {
            iter.skip();
        }
        return extra;
    }

    private List<String> collectMissingFields(long tracker) {
        List<String> missingFields = new ArrayList<String>();
        for (Binding binding : allBindings.values()) {
            if (binding.asMissingWhenNotPresent) {
                long mask = binding.mask;
                CodegenAccess.addMissingField(missingFields, tracker, mask, binding.name);
            }
        }
        return missingFields;
    }

    private void applyWrappers(Object[] temp, Object obj) throws Exception {
        for (WrapperDescriptor wrapper : desc.bindingTypeWrappers) {
            Object[] args = new Object[wrapper.parameters.size()];
            for (int i = 0; i < wrapper.parameters.size(); i++) {
                Object arg = temp[wrapper.parameters.get(i).idx];
                if (arg != NOT_SET) {
                    args[i] = arg;
                }
            }
            wrapper.method.invoke(obj, args);
        }
    }

    private Object createNewObject(JsonIterator iter, Object[] temp) throws Exception {
        if (iter.tempObjects == null) {
            iter.tempObjects = new HashMap<String, Object>();
        }
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
        if (desc.ctor.objectFactory != null) {
            return desc.ctor.objectFactory.create(desc.clazz);
        }
        if (desc.ctor.staticFactory != null) {
            return desc.ctor.staticFactory.invoke(null, args);
        } else {
            return desc.ctor.ctor.newInstance(args);
        }
    }
}
