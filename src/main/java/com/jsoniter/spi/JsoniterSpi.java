package com.jsoniter.spi;

import com.jsoniter.annotation.JsoniterConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsoniterSpi {

    static ThreadLocal<Config> currentConfig = new ThreadLocal<Config>() {
        @Override
        protected Config initialValue() {
            return JsoniterConfig.INSTANCE;
        }
    };
    private static List<Extension> extensions = new ArrayList<Extension>();
    private static Map<Class, Class> typeImpls = new HashMap<Class, Class>();
    private static int configIndex = 0;
    private static volatile Map<Object, String> configNames = new HashMap<Object, String>();
    private static volatile Map<String, MapKeyDecoder> mapKeyDecoders = new HashMap<String, MapKeyDecoder>();
    private static volatile Map<String, Encoder> encoders = new HashMap<String, Encoder>();
    private static volatile Map<String, Decoder> decoders = new HashMap<String, Decoder>();
    private static volatile Map<Class, Extension> objectFactories = new HashMap<Class, Extension>();

    public static String assignConfigName(Object obj) {
        String configName = configNames.get(obj);
        if (configName != null) {
            return configName;
        }
        return assignNewConfigName(obj);
    }

    private synchronized static String assignNewConfigName(Object obj) {
        String configName = configNames.get(obj);
        if (configName != null) {
            return configName;
        }
        configIndex++;
        configName = "jsoniter_codegen.cfg" + configIndex + ".";
        HashMap<Object, String> newCache = new HashMap<Object, String>(configNames);
        newCache.put(obj, configName);
        configNames = newCache;
        return configName;
    }

    public static void registerExtension(Extension extension) {
        if (!extensions.contains(extension)) {
            extensions.add(extension);
        }
    }

    // TODO: use composite pattern
    public static List<Extension> getExtensions() {
        ArrayList<Extension> combined = new ArrayList<Extension>(extensions);
        combined.add(currentConfig.get());
        return combined;
    }

    public static void registerMapKeyDecoder(Type mapKeyType, MapKeyDecoder mapKeyDecoder) {
        addNewMapDecoder(TypeLiteral.create(mapKeyType).getDecoderCacheKey(), mapKeyDecoder);
    }

    public synchronized static void addNewMapDecoder(String cacheKey, MapKeyDecoder mapKeyDecoder) {
        HashMap<String, MapKeyDecoder> newCache = new HashMap<String, MapKeyDecoder>(mapKeyDecoders);
        newCache.put(cacheKey, mapKeyDecoder);
        mapKeyDecoders = newCache;
    }

    public static MapKeyDecoder getMapKeyDecoder(String cacheKey) {
        return mapKeyDecoders.get(cacheKey);
    }

    public static void registerTypeImplementation(Class superClazz, Class implClazz) {
        typeImpls.put(superClazz, implClazz);
    }

    public static Class getTypeImplementation(Class superClazz) {
        return typeImpls.get(superClazz);
    }

    public static void registerTypeDecoder(Class clazz, Decoder decoder) {
        addNewDecoder(TypeLiteral.create(clazz).getDecoderCacheKey(), decoder);
    }

    public static void registerTypeDecoder(TypeLiteral typeLiteral, Decoder decoder) {
        addNewDecoder(typeLiteral.getDecoderCacheKey(), decoder);
    }

    public static void registerPropertyDecoder(Class clazz, String field, Decoder decoder) {
        addNewDecoder(field + "@" + TypeLiteral.create(clazz).getDecoderCacheKey(), decoder);
    }

    public static void registerPropertyDecoder(TypeLiteral typeLiteral, String field, Decoder decoder) {
        addNewDecoder(field + "@" + typeLiteral.getDecoderCacheKey(), decoder);
    }

    public static void registerTypeEncoder(Class clazz, Encoder encoder) {
        addNewEncoder(TypeLiteral.create(clazz).getEncoderCacheKey(), encoder);
    }

    public static void registerTypeEncoder(TypeLiteral typeLiteral, Encoder encoder) {
        addNewEncoder(typeLiteral.getDecoderCacheKey(), encoder);
    }

    public static void registerPropertyEncoder(Class clazz, String field, Encoder encoder) {
        addNewEncoder(field + "@" + TypeLiteral.create(clazz).getEncoderCacheKey(), encoder);
    }

    public static void registerPropertyEncoder(TypeLiteral typeLiteral, String field, Encoder encoder) {
        addNewEncoder(field + "@" + typeLiteral.getDecoderCacheKey(), encoder);
    }

    public static Decoder getDecoder(String cacheKey) {
        return decoders.get(cacheKey);
    }

    public synchronized static void addNewDecoder(String cacheKey, Decoder decoder) {
        HashMap<String, Decoder> newCache = new HashMap<String, Decoder>(decoders);
        newCache.put(cacheKey, decoder);
        decoders = newCache;
    }

    public static Encoder getEncoder(String cacheKey) {
        return encoders.get(cacheKey);
    }

    public synchronized static void addNewEncoder(String cacheKey, Encoder encoder) {
        HashMap<String, Encoder> newCache = new HashMap<String, Encoder>(encoders);
        newCache.put(cacheKey, encoder);
        encoders = newCache;
    }

    public static boolean canCreate(Class clazz) {
        if (objectFactories.containsKey(clazz)) {
            return true;
        }
        for (Extension extension : getExtensions()) {
            if (extension.canCreate(clazz)) {
                addObjectFactory(clazz, extension);
                return true;
            }
        }
        return false;
    }

    public static Object create(Class clazz) {
        return getObjectFactory(clazz).create(clazz);
    }

    public static Extension getObjectFactory(Class clazz) {
        return objectFactories.get(clazz);
    }

    private synchronized static void addObjectFactory(Class clazz, Extension extension) {
        HashMap<Class, Extension> copy = new HashMap<Class, Extension>(objectFactories);
        copy.put(clazz, extension);
        objectFactories = copy;
    }

    public static void setCurrentConfig(Config val) {
        currentConfig.set(val);
    }

    public static void clearCurrentConfig() {
        currentConfig.set(JsoniterConfig.INSTANCE);
    }

    public static Config getCurrentConfig() {
        return currentConfig.get();
    }
}
