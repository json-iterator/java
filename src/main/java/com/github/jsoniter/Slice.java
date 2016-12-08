package com.github.jsoniter;

public class Slice {
    public byte[] data;
    public int head;
    public int len;

    public Slice(byte[] data, int head, int len) {
        this.data = data;
        this.head = head;
        this.len = len;
    }

    public static Slice make(int len, int cap) {
        return new Slice(new byte[cap], 0, len);
    }

    public static Slice make(String str) {
        byte[] data = str.getBytes();
        return new Slice(data, 0, data.length);
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

        for (int i = head; i < len; i++)
            if (data[i] != slice.data[i])
                return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = head; i < len; i++) {
            result = 31 * result + data[i];
        }
        return result;
    }

    @Override
    public String toString() {
        return new String(data, head, len);
    }
}
