///////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2001, Eric D. Friedman All Rights Reserved.
// Copyright (c) 2009, Rob Eden All Rights Reserved.
// Copyright (c) 2009, Jeff Randall All Rights Reserved.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package com.jsoniter.gnu.trove.impl.hash;

import com.jsoniter.gnu.trove.procedure.TObjectProcedure;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An open addressed hashing implementation for Object types.
 * <p/>
 * Created: Sun Nov  4 08:56:06 2001
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: TObjectHash.java,v 1.1.2.6 2009/11/07 03:36:44 robeden Exp $
 */
abstract public class TObjectHash<T> extends THash {

    @SuppressWarnings({"UnusedDeclaration"})
    static final long serialVersionUID = -3461112548087185871L;


    /**
     * the set of Objects
     */
    public transient Object[] _set;

    public static final Object REMOVED = new Object(), FREE = new Object();

    /**
     * Indicates whether the last insertKey() call used a FREE slot. This field
     * should be inspected right after call insertKey()
     */
    protected boolean consumeFreeSlot;


    /**
     * Creates a new <code>TObjectHash</code> instance with the
     * default capacity and load factor.
     */
    public TObjectHash() {
        super();
    }


    /**
     * Creates a new <code>TObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TObjectHash(int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Creates a new <code>TObjectHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor      used to calculate the threshold over which
     *                        rehashing takes place.
     */
    public TObjectHash(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }


    public int capacity() {
        return _set.length;
    }


    protected void removeAt(int index) {
        _set[index] = REMOVED;
        super.removeAt(index);
    }


    /**
     * initializes the Object set of this hash table.
     *
     * @param initialCapacity an <code>int</code> value
     * @return an <code>int</code> value
     */
    public int setUp(int initialCapacity) {
        int capacity;

        capacity = super.setUp(initialCapacity);
        _set = new Object[capacity];
        Arrays.fill(_set, FREE);
        return capacity;
    }


    /**
     * Executes <tt>procedure</tt> for each element in the set.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the set terminated because
     *         the procedure returned false for some value.
     */
    @SuppressWarnings({"unchecked"})
    public boolean forEach(TObjectProcedure<? super T> procedure) {
        Object[] set = _set;
        for (int i = set.length; i-- > 0;) {
            if (set[i] != FREE
                    && set[i] != REMOVED
                    && !procedure.execute((T) set[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Searches the set for <tt>obj</tt>
     *
     * @param obj an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    @SuppressWarnings({"unchecked"})
    public boolean contains(Object obj) {
        return index(obj) >= 0;
    }


    /**
     * Locates the index of <tt>obj</tt>.
     *
     * @param obj an <code>Object</code> value
     * @return the index of <tt>obj</tt> or -1 if it isn't in the set.
     */
    protected int index(Object obj) {
        if (obj == null)
            return indexForNull();

        // From here on we know obj to be non-null
        final int hash = hash(obj) & 0x7fffffff;
        int index = hash % _set.length;
        Object cur = _set[index];


        if (cur == FREE) {
            return -1;
        }

        if (cur == obj || equals(obj, cur)) {
            return index;
        }

        return indexRehashed(obj, index, hash, cur);
    }

    /**
     * Locates the index of non-null <tt>obj</tt>.
     *
     * @param obj   target key, know to be non-null
     * @param index we start from
     * @param hash
     * @param cur
     */
    private int indexRehashed(Object obj, int index, int hash, Object cur) {
        final Object[] set = _set;
        final int length = set.length;

        // NOTE: here it has to be REMOVED or FULL (some user-given value)
        // see Knuth, p. 529
        int probe = 1 + (hash % (length - 2));

        final int loopIndex = index;

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            cur = set[index];
            //
            if (cur == FREE)
                return -1;

            //
            if ((cur == obj || equals(obj, cur)))
                return index;
        } while (index != loopIndex);

        return -1;
    }

    /**
     * Locates the index <tt>null</tt>.
     * <p/>
     * null specific loop exploiting several properties to simplify the iteration logic
     * - the null value hashes to 0 we so we can iterate from the beginning.
     * - the probe value is 1 for this case
     * - object identity can be used to match this case
     * <p/>
     * --> this result a simpler loop
     *
     */
    private int indexForNull() {
        int index = 0;
        for (Object o : _set) {
            if (o == null)
                return index;

            if (o == FREE)
                return -1;

            index++;
        }

        return -1;
    }

    /**
     * Alias introduced to avoid breaking the API. The new method name insertKey() reflects the
     * changes made to the logic.
     *
     * @param obj
     * @deprecated use {@link #insertKey} instead
     */
    @Deprecated
    protected int insertionIndex(T obj) {
        return insertKey(obj);
    }

    /**
     * Locates the index at which <tt>key</tt> can be inserted.  if
     * there is already a value equal()ing <tt>key</tt> in the set,
     * returns that value's index as <tt>-index - 1</tt>.
     * <p/>
     * If a slot is found the value is inserted. When a FREE slot is used the consumeFreeSlot field is
     * set to true. This field should be used in the method invoking insertKey() to pass to postInsertHook()
     *
     * @param key an <code>Object</code> value
     * @return the index of a FREE slot at which key can be inserted
     *         or, if key is already stored in the hash, the negative value of
     *         that index, minus 1: -index -1.
     */
    protected int insertKey(T key) {
        consumeFreeSlot = false;

        if (key == null)
            return insertKeyForNull();

        final int hash = hash(key) & 0x7fffffff;
        int index = hash % _set.length;
        Object cur = _set[index];

        if (cur == FREE) {
            consumeFreeSlot = true;
            _set[index] = key;  // insert value
            return index;       // empty, all done
        }

        if (cur == key || equals(key, cur)) {
            return -index - 1;   // already stored
        }

        return insertKeyRehash(key, index, hash, cur);
    }

    /**
     * Looks for a slot using double hashing for a non-null key values and inserts the value
     * in the slot
     *
     * @param key   non-null key value
     * @param index natural index
     * @param hash
     * @param cur   value of first matched slot
     */
    private int insertKeyRehash(T key, int index, int hash, Object cur) {
        final Object[] set = _set;
        final int length = set.length;
        // already FULL or REMOVED, must probe
        // compute the double hash
        final int probe = 1 + (hash % (length - 2));

        final int loopIndex = index;
        int firstRemoved = -1;

        /**
         * Look until FREE slot or we start to loop
         */
        do {
            // Identify first removed slot
            if (cur == REMOVED && firstRemoved == -1)
                firstRemoved = index;

            index -= probe;
            if (index < 0) {
                index += length;
            }
            cur = set[index];

            // A FREE slot stops the search
            if (cur == FREE) {
                if (firstRemoved != -1) {
                    _set[firstRemoved] = key;
                    return firstRemoved;
                } else {
                    consumeFreeSlot = true;
                    _set[index] = key;  // insert value
                    return index;
                }
            }

            if (cur == key || equals(key, cur)) {
                return -index - 1;
            }

            // Detect loop
        } while (index != loopIndex);

        // We inspected all reachable slots and did not find a FREE one
        // If we found a REMOVED slot we return the first one found
        if (firstRemoved != -1) {
            _set[firstRemoved] = key;
            return firstRemoved;
        }

        // Can a resizing strategy be found that resizes the set?
        throw new IllegalStateException("No free or removed slots available. Key set full?!!");
    }

    /**
     * Looks for a slot using double hashing for a null key value and inserts the value.
     * <p/>
     * null specific loop exploiting several properties to simplify the iteration logic
     * - the null value hashes to 0 we so we can iterate from the beginning.
     * - the probe value is 1 for this case
     * - object identity can be used to match this case
     *
     */
    private int insertKeyForNull() {
        int index = 0;
        int firstRemoved = -1;

        // Look for a slot containing the 'null' value as key
        for (Object o : _set) {
            // Locate first removed
            if (o == REMOVED && firstRemoved == -1)
                firstRemoved = index;

            if (o == FREE) {
                if (firstRemoved != -1) {
                    _set[firstRemoved] = null;
                    return firstRemoved;
                } else {
                    consumeFreeSlot = true;
                    _set[index] = null;  // insert value
                    return index;
                }
            }

            if (o == null) {
                return -index - 1;
            }

            index++;
        }

        // We inspected all reachable slots and did not find a FREE one
        // If we found a REMOVED slot we return the first one found
        if (firstRemoved != -1) {
            _set[firstRemoved] = null;
            return firstRemoved;
        }

        // We scanned the entire key set and found nothing, is set full?
        // Can a resizing strategy be found that resizes the set?
        throw new IllegalStateException("Could not find insertion index for null key. Key set full!?!!");
    }


    /**
     * Convenience methods for subclasses to use in throwing exceptions about
     * badly behaved user objects employed as keys.  We have to throw an
     * IllegalArgumentException with a rather verbose message telling the
     * user that they need to fix their object implementation to conform
     * to the general contract for java.lang.Object.
     *
     *
     * @param o1 the first of the equal elements with unequal hash codes.
     * @param o2 the second of the equal elements with unequal hash codes.
     * @throws IllegalArgumentException the whole point of this method.
     */
    protected final void throwObjectContractViolation(Object o1, Object o2)
            throws IllegalArgumentException {
        throw buildObjectContractViolation(o1, o2, "");
    }

    /**
     * Convenience methods for subclasses to use in throwing exceptions about
     * badly behaved user objects employed as keys.  We have to throw an
     * IllegalArgumentException with a rather verbose message telling the
     * user that they need to fix their object implementation to conform
     * to the general contract for java.lang.Object.
     *
     *
     * @param o1 the first of the equal elements with unequal hash codes.
     * @param o2 the second of the equal elements with unequal hash codes.
     * @param size
     *@param oldSize
     * @param oldKeys @throws IllegalArgumentException the whole point of this method.
     */
    protected final void throwObjectContractViolation(Object o1, Object o2, int size, int oldSize, Object[] oldKeys)
            throws IllegalArgumentException {
        String extra = dumpExtraInfo(o1, o2, size(), oldSize, oldKeys);


        throw buildObjectContractViolation(o1, o2, extra);
    }

    /**
     * Convenience methods for subclasses to use in throwing exceptions about
     * badly behaved user objects employed as keys.  We have to throw an
     * IllegalArgumentException with a rather verbose message telling the
     * user that they need to fix their object implementation to conform
     * to the general contract for java.lang.Object.
     *
     *
     * @param o1 the first of the equal elements with unequal hash codes.
     * @param o2 the second of the equal elements with unequal hash codes.
     * @throws IllegalArgumentException the whole point of this method.
     */
    protected final IllegalArgumentException buildObjectContractViolation(Object o1, Object o2, String extra ) {
        return new IllegalArgumentException("Equal objects must have equal hashcodes. " +
                "During rehashing, Trove discovered that the following two objects claim " +
                "to be equal (as in java.lang.Object.equals()) but their hashCodes (or " +
                "those calculated by your TObjectHashingStrategy) are not equal." +
                "This violates the general contract of java.lang.Object.hashCode().  See " +
                "bullet point two in that method's documentation. object #1 =" + objectInfo(o1) +
                "; object #2 =" + objectInfo(o2) + "\n" + extra);
    }


    protected boolean equals(Object notnull, Object two) {
        if (two == null || two == REMOVED)
            return false;

        return notnull.equals(two);
    }

    protected int hash(Object notnull) {
        return notnull.hashCode();
    }

    protected static String reportPotentialConcurrentMod(int newSize, int oldSize) {
        // Note that we would not be able to detect concurrent paired of put()-remove()
        // operations with this simple check
        if (newSize != oldSize)
            return "[Warning] apparent concurrent modification of the key set. " +
                    "Size before and after rehash() do not match " + oldSize + " vs " + newSize;

        return "";
    }

    /**
     *
     * @param newVal the key being inserted
     * @param oldVal the key already stored at that position
     * @param currentSize size of the key set during rehashing
     * @param oldSize size of the key set before rehashing
     * @param oldKeys the old key set
     */
    protected String dumpExtraInfo(Object newVal, Object oldVal, int currentSize, int oldSize, Object[] oldKeys) {
        StringBuilder b = new StringBuilder();
        //
        b.append(dumpKeyTypes(newVal, oldVal));

        b.append(reportPotentialConcurrentMod(currentSize, oldSize));
        b.append(detectKeyLoss(oldKeys, oldSize));

        // Is de same object already present? Double insert?
        if (newVal == oldVal) {
            b.append("Inserting same object twice, rehashing bug. Object= ").append(oldVal);
        }

        return b.toString();
    }

    /**
     * Detect inconsistent hashCode() and/or equals() methods
     *
     * @param keys
     * @param oldSize
     */
    private static String detectKeyLoss(Object[] keys, int oldSize) {
        StringBuilder buf = new StringBuilder();
        Set<Object> k = makeKeySet(keys);
        if (k.size() != oldSize) {
            buf.append("\nhashCode() and/or equals() have inconsistent implementation");
            buf.append("\nKey set lost entries, now got ").append(k.size()).append(" instead of ").append(oldSize);
            buf.append(". This can manifest itself as an apparent duplicate key.");
        }

        return buf.toString();
    }

    private static Set<Object> makeKeySet(Object[] keys) {
        Set<Object> types = new HashSet<Object>();
        for (Object o : keys) {
            if (o != FREE && o != REMOVED) {
                    types.add(o);
            }
        }

        return types;
    }

    private static String equalsSymmetryInfo(Object a, Object b) {
        StringBuilder buf = new StringBuilder();
        if (a == b) {
            return  "a == b";
        }

        if (a.getClass() != b.getClass()) {
            buf.append("Class of objects differ a=").append(a.getClass()).append(" vs b=").append(b.getClass());

            boolean aEb = a.equals(b);
            boolean bEa = b.equals(a);
            if (aEb != bEa) {
                buf.append("\nequals() of a or b object are asymmetric");
                buf.append("\na.equals(b) =").append(aEb);
                buf.append("\nb.equals(a) =").append(bEa);
            }
        }

        return buf.toString();
    }

    protected static String objectInfo(Object o) {
        return (o == null ? "class null" : o.getClass()) + " id= " + System.identityHashCode(o)
                + " hashCode= " + (o == null ? 0 : o.hashCode()) + " toString= " + String.valueOf(o);
    }

    private String dumpKeyTypes(Object newVal, Object oldVal) {
        StringBuilder buf = new StringBuilder();
        Set<Class<?>> types = new HashSet<Class<?>>();
        for (Object o : _set) {
            if (o != FREE && o != REMOVED) {
                if (o != null)
                    types.add(o.getClass());
                else
                    types.add(null);
            }
        }

        if (types.size() > 1) {
            buf.append("\nMore than one type used for keys. Watch out for asymmetric equals(). " +
                    "Read about the 'Liskov substitution principle' and the implications for equals() in java.");

            buf.append("\nKey types: ").append(types);
            buf.append(equalsSymmetryInfo(newVal, oldVal));
        }

        return buf.toString();
    }


    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
        out.writeByte(0);

        // SUPER
        super.writeExternal(out);
    }


    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // SUPER
        super.readExternal(in);
    }
} // TObjectHash
