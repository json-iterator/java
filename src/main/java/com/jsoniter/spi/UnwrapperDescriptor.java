package com.jsoniter.spi;

import com.jsoniter.output.JsonStream;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class UnwrapperDescriptor {

    public Method method;

    public boolean isMap;

    public boolean isSimpleValue;

    public TypeLiteral mapValueTypeLiteral;

    public UnwrapperDescriptor(Method method) {
        this.method = method;
        if (isMapUnwrapper(method)) {
            this.isMap = true;
            Type mapType = method.getGenericReturnType();
            mapValueTypeLiteral = TypeLiteral.create(Object.class);
            if (mapType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) mapType;
                Type[] typeArgs = pType.getActualTypeArguments();
                if (typeArgs.length == 2) {
                    mapValueTypeLiteral = TypeLiteral.create(typeArgs[1]);
                }
            }
        } else if (isStreamUnwrapper(method)) {
            this.isMap = false;
        } else if (method.getParameterTypes().length == 0 && method.getReturnType() != void.class) { //A getter
            this.isSimpleValue = true;
            Type mapType = method.getReturnType();
            mapValueTypeLiteral = TypeLiteral.create(mapType);
        }
        else {
            throw new JsonException("invalid unwrapper method signature: " + method);
        }
    }

    private boolean isMapUnwrapper(Method method) {
        if (method.getParameterTypes().length != 0) {
            return false;
        }
        return Map.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isStreamUnwrapper(Method method) {
        if (method.getReturnType() != void.class) {
            return false;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            return false;
        }
        return parameterTypes[0] == JsonStream.class;
    }
}
