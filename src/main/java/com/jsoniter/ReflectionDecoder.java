package com.jsoniter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

public class ReflectionDecoder implements Decoder {

    private static Object NOT_SET = new Object();
    private Constructor ctor;
    private Map<String, Binding> allBindings = new HashMap<String, Binding>();
    private List<Binding> ctorParams = new ArrayList<Binding>();
    private ThreadLocal<Object[]> tempTls;
    private ThreadLocal<Object[]> ctorArgsTls;
    private List<Binding> fields;

    public ReflectionDecoder(Class clazz) {
        try {
            init(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init(Class clazz) throws Exception {
        CustomizedConstructor cctor = ExtensionManager.getCtor(clazz, true);
        ctorParams = cctor.parameters;
        int tempIdx = 0;
        for (Binding param : ctorParams) {
            param.idx = tempIdx;
            for (String fromName : param.fromNames) {
                allBindings.put(fromName, param);
            }
            tempIdx++;
        }
        this.ctor = cctor.ctor;
        fields = ExtensionManager.getFields(clazz, true);
        for (Binding field : fields) {
            field.idx = tempIdx;
            for (String fromName : field.fromNames) {
                allBindings.put(fromName, field);
            }
            tempIdx++;
        }
        if (!ctorParams.isEmpty()) {
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

    @Override
    public Object decode(JsonIterator iter) throws IOException {
        try {
            if (ctorParams.isEmpty()) {
                return decodeWithoutCtorBinding(iter);
            } else {
                return decodeWithCtorBinding(iter);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object decodeWithCtorBinding(JsonIterator iter) throws Exception {
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
        Arrays.fill(ctorArgs, NOT_SET);
        for (int i = 0; i < ctorParams.size(); i++) {
            Object arg = temp[ctorParams.get(i).idx];
            if (arg != NOT_SET) {
                ctorArgs[i] = arg;
            }
        }
        Object obj = ctor.newInstance(ctorArgs);
        for (Binding field : fields) {
            Object val = temp[field.idx];
            if (val != NOT_SET) {
                field.field.set(obj, val);
            }
        }
        return obj;
    }

    private Object decodeWithoutCtorBinding(JsonIterator iter) throws Exception {
        Object obj = ctor.newInstance();
        for (String fieldName = iter.readObject(); fieldName != null; fieldName = iter.readObject()) {
            Binding binding = allBindings.get(fieldName);
            if (binding == null) {
                iter.skip();
                continue;
            }
            if (binding.field != null) {
                binding.field.set(obj, iter.read(binding.valueTypeLiteral));
            }
        }
        return obj;
    }
}
