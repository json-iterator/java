package com.jsoniter;

public class Slice {

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

    public final byte at(int pos) {
        return data[head + pos];
    }

    public final int len() {
        return tail - head;
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
    public final boolean equals(Object o) {
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
