package com.jsoniter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Any {

    private final Object val;
    public Object lastAccessed;

    public Any(Object val) {
        this.val = val;
    }

    public ValueType getValueType(Object... keys) {
        try {
            lastAccessed = getPath(val, keys);
            if (lastAccessed == null) {
                return ValueType.NULL;
            }
            Class<?> clazz = lastAccessed.getClass();
            if (clazz == String.class) {
                return ValueType.STRING;
            }
            if (clazz.isArray()) {
                return ValueType.ARRAY;
            }
            if (lastAccessed instanceof Number) {
                return ValueType.NUMBER;
            }
            if (lastAccessed instanceof List) {
                return ValueType.ARRAY;
            }
            return ValueType.OBJECT;
        } catch (ClassCastException e) {
            return ValueType.INVALID;
        } catch (IndexOutOfBoundsException e) {
            return ValueType.INVALID;
        }
    }


    public Map<String, Object> getMap(Object... keys) {
        return get(keys);
    }

    public List<Object> getList(Object... keys) {
        return get(keys);
    }

    public <T> T get(Object... keys) {
        try {
            return (T) (lastAccessed = getPath(val, keys));
        } catch (ClassCastException e) {
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean exists(Object... keys) {
        try {
            lastAccessed = getPath(val, keys);
            return true;
        } catch (ClassCastException e) {
            return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public String toString() {
        return toString(new Object[0]);
    }

    public String toString(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return "null";
        }
        return lastAccessed.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Any any = (Any) o;

        return val != null ? val.equals(any.val) : any.val == null;

    }

    @Override
    public int hashCode() {
        return val != null ? val.hashCode() : 0;
    }

    public int toInt(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return 0;
        }
        if (lastAccessed.getClass() == String.class) {
            return Integer.valueOf((String) lastAccessed);
        }
        Number number = (Number) lastAccessed;
        return number.intValue();
    }

    public short toShort(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return 0;
        }
        if (lastAccessed.getClass() == String.class) {
            return Short.valueOf((String) lastAccessed);
        }
        Number number = (Number) lastAccessed;
        return number.shortValue();
    }

    public long toLong(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return 0;
        }
        if (lastAccessed.getClass() == String.class) {
            return Long.valueOf((String) lastAccessed);
        }
        Number number = (Number) lastAccessed;
        return number.longValue();
    }

    public float toFloat(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return 0;
        }
        if (lastAccessed.getClass() == String.class) {
            return Float.valueOf((String) lastAccessed);
        }
        Number number = (Number) lastAccessed;
        return number.floatValue();
    }

    public double toDouble(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return 0;
        }
        if (lastAccessed.getClass() == String.class) {
            return Double.valueOf((String) lastAccessed);
        }
        Number number = (Number) lastAccessed;
        return number.doubleValue();
    }

    public boolean toBoolean(Object... keys) {
        get(keys);
        if (lastAccessed == null) {
            return false;
        }
        if (lastAccessed instanceof Number) {
            Number number = (Number) lastAccessed;
            return number.intValue() != 0;
        }
        if (lastAccessed.getClass().isArray()) {
            return Array.getLength(lastAccessed) != 0;
        }
        if (lastAccessed instanceof Collection) {
            Collection col = (Collection) lastAccessed;
            return col.size() != 0;
        }
        if (lastAccessed instanceof Map) {
            Map map = (Map) lastAccessed;
            return map.size() != 0;
        }
        return true;
    }

    private static Object getPath(Object val, Object... keys) {
        if (keys.length == 0) {
            return val;
        }
        Object key = keys[0];
        if ("*".equals(key)) {
            if (val.getClass().isArray()) {
                ArrayList result = new ArrayList(Array.getLength(val));
                for (int i = 0; i < Array.getLength(val); i++) {
                    Object nextVal = Array.get(val, i);
                    Object[] nextKeys = new Object[keys.length - 1];
                    System.arraycopy(keys, 1, nextKeys, 0, nextKeys.length);
                    result.add(getPath(nextVal, nextKeys));
                }
                return result;
            } else {
                List list = (List) val;
                ArrayList result = new ArrayList(list.size());
                for (Object e : list) {
                    Object nextVal = e;
                    Object[] nextKeys = new Object[keys.length - 1];
                    System.arraycopy(keys, 1, nextKeys, 0, nextKeys.length);
                    result.add(getPath(nextVal, nextKeys));
                }
                return result;
            }
        }
        if (key instanceof Integer) {
            Object nextVal = getFromArrayOrList(val, (Integer) key);
            Object[] nextKeys = new Object[keys.length - 1];
            System.arraycopy(keys, 1, nextKeys, 0, nextKeys.length);
            return getPath(nextVal, nextKeys);
        }
        if (key instanceof String) {
            Object nextVal = getFromMap(val, (String) key);
            Object[] nextKeys = new Object[keys.length - 1];
            System.arraycopy(keys, 1, nextKeys, 0, nextKeys.length);
            return getPath(nextVal, nextKeys);
        }
        throw new RuntimeException("invalid key type: " + key);
    }

    private static Object getFromMap(Object val, String key) {
        Map map = (Map) val;
        if (!map.containsKey(key)) {
            throw new IndexOutOfBoundsException(key + " not in " + map);
        }
        return map.get(key);
    }

    private static Object getFromArrayOrList(Object val, Integer key) {
        if (val.getClass().isArray()) {
            return Array.get(val, key);
        }
        List list = (List) val;
        return list.get(key);
    }
}
