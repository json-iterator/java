package com.jsoniter;

public class Slice implements Cloneable {

    private byte[] data;
    private int head;
    private int tail;
    private int hash;

    public Slice(byte[] data, int head, int tail) {
        this.data = data;
        this.head = head;
        this.tail = tail;
    }

    public void reset(byte[] data, int head, int tail) {
        this.data = data;
        this.head = head;
        this.tail = tail;
        this.hash = 0;
    }

    public final byte[] data() {
        return data;
    }

    public final int head() {
        return head;
    }

    public final int tail() {
        return tail;
    }

    public static Slice make(String str) {
        byte[] data = str.getBytes();
        return new Slice(data, 0, data.length);
    }

    @Override
    public Slice clone() {
        try {
            return (Slice) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new JsonException(e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (o.getClass() == String.class) {
            String str = (String) o;
            if ((tail - head) != str.length()) return false;
            for (int i = head, j = 0; i < tail; i++, j++)
                if (data[i] != str.charAt(j)) {
                    return false;
                }
            return true;
        }
        Slice slice = (Slice) o;
        if ((tail - head) != (slice.tail - slice.head)) return false;
        for (int i = head, j = slice.head; i < tail; i++, j++)
            if (data[i] != slice.data[j])
                return false;
        return true;
    }

    @Override
    public final int hashCode() {
        if (hash == 0 && tail - head > 0) {
            for (int i = head; i < tail; i++) {
                hash = 31 * hash + data[i];
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        return new String(data, head, tail - head);
    }
}
