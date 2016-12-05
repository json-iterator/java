package com.github.jsoniter;

public class Slice {
    public byte[] data;
    public int len;

    public Slice(byte[] data, int len) {
        this.data = data;
        this.len = len;
    }

    public static Slice make(int len, int cap) {
        if (cap <= 0) {
            throw new IllegalArgumentException("cap must > 0");
        }
        return new Slice(new byte[cap], len);
    }

    public static Slice make(String str) {
        byte[] data = str.getBytes();
        return new Slice(data, data.length);
    }

    public void append(byte c) {
        if (len == data.length) {
            byte[] newData = new byte[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        data[len++] = c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slice slice = (Slice) o;

        if (len != slice.len) return false;

        for (int i = 0; i < len; i++)
            if (data[i] != slice.data[i])
                return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < len; i++) {
            result = 31 * result + data[i];
        }
        return result;
    }

    @Override
    public String toString() {
        return new String(data, 0, len);
    }
}
