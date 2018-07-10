package com.jsoniter.slice;

/**
 * Will help you when you need a {@link Slice}, but have a {@link String}
 */
public final class StringSlice implements Slice {

    private String string;
    private byte[] data = null;

    public StringSlice(String string) {
        this.string = string;
    }

    public void reset(String newValue) {
        this.string = newValue;
        this.data = null;
    }

    @Override
    public byte[] data() {
        return data == null ? data = string.getBytes() : data;
    }

    @Override
    public byte at(int pos) {
        return data()[pos];
    }

    @Override
    public int head() {
        return 0;
    }

    @Override
    public int tail() {
        return data().length;
    }

    @Override
    public int size() {
        return data().length;
    }

    @Override
    public Slice shallowCopy() {
        return new StringSlice(string);
    }

    @Override
    public String string() {
        return string;
    }

    @Override
    public boolean equals(Object obj) {
        // short way: compare strings which is JVM intrinsic
        if (obj.getClass() == StringSlice.class) {
            return string.equals(((StringSlice) obj).string);
        }

        if (obj.getClass() == DirectSlice.class && ((DirectSlice) obj).string != null) {
            return string.equals(((DirectSlice) obj).string);
        }

        if (!(obj instanceof Slice)) {
            return false;
        }

        Slice that = (Slice) obj;
        final int size = size();
        if (that.size() != size) {
            return false;
        }

        // long way: compare bytes
        byte[] thisData = data();
        byte[] thatData = that.data();
        final int thatHead = that.head();
        for (int i = 0; i < size; i++) {
            if (thisData[i] != thatData[thatHead + i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        // String#hashCode is JVM intrinsic,
        // it's lightning-fast & cached
        return string.hashCode();
    }

    @Override
    public String toString() {
        return "StringSlice(" + size() + " bytes)";
    }

}
