package com.jsoniter;

public class CustomizedObject {
    public String field2;
    public String field1;
    public int field3;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomizedObject that = (CustomizedObject) o;

        if (field3 != that.field3) return false;
        if (field2 != null ? !field2.equals(that.field2) : that.field2 != null) return false;
        return field1 != null ? field1.equals(that.field1) : that.field1 == null;

    }

    @Override
    public int hashCode() {
        int result = field2 != null ? field2.hashCode() : 0;
        result = 31 * result + (field1 != null ? field1.hashCode() : 0);
        result = 31 * result + field3;
        return result;
    }
}
