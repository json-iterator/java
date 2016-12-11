package com.jsoniter;

public class SimpleObject {
    public String field2;
    public String field1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        SimpleObject that = (SimpleObject) o;

        if (field2 != null ? !field2.equals(that.field2) : that.field2 != null) return false;
        return field1 != null ? field1.equals(that.field1) : that.field1 == null;

    }

    @Override
    public int hashCode() {
        int result = field2 != null ? field2.hashCode() : 0;
        result = 31 * result + (field1 != null ? field1.hashCode() : 0);
        return result;
    }
}
