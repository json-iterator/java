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

import com.jsoniter.gnu.trove.strategy.HashingStrategy;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


/**
 * An open addressed hashing implementation for Object types.
 *
 * @author Rob Eden
 * @author Eric D. Friedman
 * @author Jeff Randall
 * @version $Id: TObjectHash.java,v 1.1.2.6 2009/11/07 03:36:44 robeden Exp $
 */
@SuppressWarnings( { "UnusedDeclaration" } )
abstract public class TCustomObjectHash<T> extends TObjectHash<T> {
	static final long serialVersionUID = 8766048185963756400L;

	protected HashingStrategy<? super T> strategy;


	/** FOR EXTERNALIZATION ONLY!!! */
	public TCustomObjectHash() {}

	
    /**
     * Creates a new <code>TManualObjectHash</code> instance with the
     * default capacity and load factor.
     */
    public TCustomObjectHash( HashingStrategy<? super T> strategy ) {
        super();

		this.strategy = strategy;
    }


    /**
     * Creates a new <code>TManualObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TCustomObjectHash( HashingStrategy<? super T> strategy, int initialCapacity ) {
        super( initialCapacity );

		this.strategy = strategy;
    }


    /**
     * Creates a new <code>TManualObjectHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor      used to calculate the threshold over which
     *                        rehashing takes place.
     */
    public TCustomObjectHash( HashingStrategy<? super T> strategy, int initialCapacity,
		float loadFactor ) {

        super( initialCapacity, loadFactor );

		this.strategy = strategy;
    }


	@Override
	protected int hash( Object obj ) {
		//noinspection unchecked
		return strategy.computeHashCode( ( T ) obj );
	}

	@Override
	protected boolean equals( Object one, Object two ) {
		//noinspection unchecked
		return two != REMOVED && strategy.equals( ( T ) one, ( T ) two );
	}


	@Override
    public void writeExternal( ObjectOutput out ) throws IOException {

        // VERSION
        out.writeByte( 0 );

        // SUPER
        super.writeExternal( out );

	    // STRATEGY
	    out.writeObject( strategy );
    }


    @Override
    public void readExternal( ObjectInput in )
            throws IOException, ClassNotFoundException {

        // VERSION
        in.readByte();

        // SUPER
        super.readExternal( in );

	    // STRATEGY
	    //noinspection unchecked
	    strategy = ( HashingStrategy<T> ) in.readObject();
    }
} // TCustomObjectHash
