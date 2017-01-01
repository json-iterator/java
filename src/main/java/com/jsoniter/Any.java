package com.jsoniter;

import com.jsoniter.spi.TypeLiteral;

import java.util.Set;

public interface Any extends Iterable<Any> {
    ValueType valueType();
    <T> T bindTo(T obj, Object... keys);
    <T> T bindTo(T obj);
    <T> T bindTo(TypeLiteral<T> typeLiteral, T obj, Object... keys);
    <T> T bindTo(TypeLiteral<T> typeLiteral, T obj);
    Object asObject(Object... keys);
    Object asObject();
    <T> T as(Class<T> clazz, Object... keys);
    <T> T as(Class<T> clazz);
    <T> T as(TypeLiteral<T> typeLiteral, Object... keys);
    <T> T as(TypeLiteral<T> typeLiteral);
    boolean toBoolean(Object... keys);
    boolean toBoolean();
    int toInt(Object... keys);
    int toInt();
    long toLong(Object... keys);
    long toLong();
    float toFloat(Object... keys);
    float toFloat();
    double toDouble(Object... keys);
    double toDouble();
    String toString(Object... keys);
    String toString();
    int size();
    Set<String> keys();
    Any get(int index);
    Any get(Object key);
    Any get(Object... keys);
    Any require(Object... keys);
    JsonIterator parse();
}
