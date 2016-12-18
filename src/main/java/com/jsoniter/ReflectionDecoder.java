package com.jsoniter;

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
    private Map<String, Binding> allBindings = new HashMap<String, Binding>();
    private List<Binding> ctorParams = new ArrayList<Binding>();
    private ThreadLocal<Object[]> tempTls;
    private ThreadLocal<Object[]> ctorArgsTls;
    private List<Binding> fields;
    private List<CustomizedSetter> setters;

    public ReflectionDecoder(Class clazz) {
        try {
            init(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final void init(Class clazz) throws Exception {
        CustomizedConstructor cctor = ExtensionManager.getCtor(clazz, true);
        ctorParams = cctor.parameters;
        int tempIdx = 0;
        for (Binding param : ctorParams) {
            tempIdx = addBinding(clazz, tempIdx, param);
        }
        this.ctor = cctor.ctor;
        this.staticFactory = cctor.staticFactory;
        fields = ExtensionManager.getFields(clazz, true);
        for (Binding field : fields) {
            tempIdx = addBinding(clazz, tempIdx, field);
        }
        setters = ExtensionManager.getSetters(clazz, true);
        for (CustomizedSetter setter : setters) {
            for (Binding param : setter.parameters) {
                tempIdx = addBinding(clazz, tempIdx, param);
            }
        }
        if (!ctorParams.isEmpty() || !setters.isEmpty()) {
            final int tempCount = tempIdx;
            tempTls = new ThreadLocal<Object[]>() {
                @Override
                protected Object[] initialValue() {
                    return new Object[tempCount];
                }
            };
            ctorArgsTls = new ThreadLocal<Object[]>() {
                @Override
                protected Object[] initialValue() {
                    return new Object[ctorParams.size()];
                }
            };
        }
    }

    private int addBinding(Class clazz, int tempIdx, Binding param) {
        param.idx = tempIdx;
        for (String fromName : param.fromNames) {
            if (allBindings.containsKey(fromName)) {
                throw new RuntimeException("name conflict found in " + clazz +": " + fromName);
            }
            allBindings.put(fromName, param);
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
            throw new RuntimeException(e);
        }
    }

    private final Object decodeWithCtorBinding(JsonIterator iter) throws Exception {
        Object[] temp = tempTls.get();
        Arrays.fill(temp, NOT_SET);
        for (String fieldName = iter.readObject(); fieldName != null; fieldName = iter.readObject()) {
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
                continue;
            }
            temp[binding.idx] = iter.read(binding.valueTypeLiteral);
        }
        Object[] ctorArgs = ctorArgsTls.get();
        Arrays.fill(ctorArgs, null);
        for (int i = 0; i < ctorParams.size(); i++) {
            Object arg = temp[ctorParams.get(i).idx];
            if (arg != NOT_SET) {
                ctorArgs[i] = arg;
            }
        }
        Object obj = createNewObject(ctorArgs);
        for (Binding field : fields) {
            Object val = temp[field.idx];
            if (val != NOT_SET) {
                field.field.set(obj, val);
            }
        }
        applySetters(temp, obj);
        return obj;
    }

    private void applySetters(Object[] temp, Object obj) throws Exception {
        for (CustomizedSetter setter : setters) {
            Object[] args = new Object[setter.parameters.size()];
            for (int i = 0; i < setter.parameters.size(); i++) {
                args[i] = temp[setter.parameters.get(i).idx];
            }
            setter.method.invoke(obj, args);
        }
    }

    private final Object decodeWithOnlyFieldBinding(JsonIterator iter) throws Exception {
        Object obj = createNewObject();
        for (String fieldName = iter.readObject(); fieldName != null; fieldName = iter.readObject()) {
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
                continue;
            }
            // TODO: when setter is single argument, decode like field
            binding.field.set(obj, iter.read(binding.valueTypeLiteral));
        }
        return obj;
    }

    private final Object decodeWithSetterBinding(JsonIterator iter) throws Exception {
        Object[] temp = tempTls.get();
        Arrays.fill(temp, null);
        Object obj = createNewObject();
        for (String fieldName = iter.readObject(); fieldName != null; fieldName = iter.readObject()) {
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
                continue;
            }
            if (binding.field == null) {
                temp[binding.idx] = iter.read(binding.valueTypeLiteral);
            } else {
                binding.field.set(obj, iter.read(binding.valueTypeLiteral));
            }
        }
        applySetters(temp, obj);
        return obj;
    }

    private Object createNewObject(Object... args) throws Exception {
        if (ctor == null) {
            return staticFactory.invoke(null, args);
        } else {
            return ctor.newInstance(args);
        }
    }
}
