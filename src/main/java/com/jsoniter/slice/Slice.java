package com.jsoniter.slice;

/**
 * Represents a part of a buffer
 * which can be converted to a String
 */
public interface Slice {

    /**
     * Returns underlying buffer
     * Don't mutate it, otherwise it will shoot your leg.
     */
    byte[] data();

    /**
     * Returns a byte at the specified offset
     */
    byte at(int pos);

    /**
     * Beginning of the range, inclusive
     */
    int head();

    /**
     * End of the range, exclusive
     */
    int tail();

    /**
     * Size of the range
     */
    int size();

    /**
     * Since slices are mutable, you need to make a copy in order to take it away
     */
    Slice shallowCopy();

    /**
     * Returns string representation of the range
     */
    String string();

    // also must implement hashCode, equals
    // toString implementation is for debug, should not be equal to string()

}
