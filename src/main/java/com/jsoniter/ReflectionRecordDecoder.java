package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectionRecordDecoder extends ReflectionObjectDecoder {

    private boolean useOnlyFieldRecord = false;

    public ReflectionRecordDecoder(ClassInfo classInfo) {

        super(classInfo);

        if (desc.clazz.isRecord() && !desc.fields.isEmpty() && tempCount == 0) {
            tempCount = tempIdx;
            tempCacheKey = "temp@" + desc.clazz.getName();
            ctorArgsCacheKey = "ctor@" + desc.clazz.getName();

            desc.ctor.parameters.addAll(desc.fields);
            useOnlyFieldRecord = true;
        }
    }

    @Override
    public Decoder create() {

        if (useOnlyFieldRecord)
            return new OnlyFieldRecord();

        if (desc.ctor.parameters.isEmpty()) {
            if (desc.bindingTypeWrappers.isEmpty()) {
                return new OnlyFieldRecord();
            } else {
                return new WithWrapper();
            }
        } else {
            return new WithCtor();
        }
    }

    public class OnlyFieldRecord implements Decoder {

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
            Object obj = createNewObject(iter, temp.clone());
            setExtra(obj, extra);
            applyWrappers(temp, obj);
            return obj;
        }

    }
}
