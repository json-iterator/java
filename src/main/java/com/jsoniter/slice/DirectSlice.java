package com.jsoniter.slice;

/**
 * A slice of a byte buffer which can be directly converted to a String
 * Prohibits UTF-8 multibyte characters.
 */
public final class DirectSlice implements Slice {

    private byte[] data;
    private int head;
    private int tail;

    private int hash;
    /*package*/ String string; // should be visible for fast StringSlice#equals

    public DirectSlice(byte[] data, int head, int tail) {
        check(data, head, tail);
        this.data = data;
        this.head = head;
        this.tail = tail;
    }

    public void reset(byte[] data, int head, int tail) {
        check(data, head, tail);
        this.data = data;
        this.head = head;
        this.tail = tail;
        this.hash = 0;
        this.string = null;
    }

    private static void check(byte[] data, int head, int tail) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }

        if (head < 0) {
            throw new IllegalArgumentException("head < 0");
        }

        if (head > tail) {
            throw new IllegalArgumentException("head > tail");
        }

        if (tail > data.length) {
            throw new IllegalArgumentException("tail > data.length");
        }
    }

    @Override
    public byte[] data() {
        return data;
    }

    @Override
    public byte at(int pos) {
        return data[head + pos];
    }

    @Override
    public int head() {
        return head;
    }

    @Override
    public int tail() {
        return tail;
    }

    @Override
    public int size() {
        return tail - head;
    }

    @Override
    public Slice shallowCopy() {
        return new DirectSlice(data, head, tail);
    }

    @Override
    public String string() {
        return string == null ? string = new String(data, head, tail - head) : string;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Slice)) {
            return false;
        }

        if (string != null) {
            Class<?> cls = obj.getClass();
            if (cls == DirectSlice.class && ((DirectSlice) obj).string != null) {
                return string.equals(((DirectSlice) obj).string);
            }

            if (cls == StringSlice.class) {
                return string.equals(((StringSlice) obj).string());
            }
        }

        Slice that = (Slice) obj;
        final int thisHead = head;
        final int thisTail = tail;
        final int thatHead = that.head();
        final int thatTail = that.tail();

        if (thisTail - thisHead != thatTail - thatHead) {
            return false;
        }

        final byte[] thisData = data;
        final byte[] thatData = that.data();

        for (int thisI = thisHead, thatI = thatHead; thisI < thisTail; thisI++, thatI++) {
            if (thisData[thisI] != thatData[thatI]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (string != null) {
            return string.hashCode();
        }
        if (hash == 0 && tail - head > 0) {
            final int tail = this.tail;
            int hash = 0;
            byte[] data = this.data;
            for (int i = head; i < tail; i++) {
                hash = 31 * hash + data[i];
            }
            return this.hash = hash;
        }
        return hash;
    }

    @Override
    public String toString() {
        return "DirectSlice(" + size() + " bytes)";
    }

}
