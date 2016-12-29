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


package com.jsoniter.gnu.trove.iterator.hash;

import com.jsoniter.gnu.trove.impl.hash.THashIterator;
import com.jsoniter.gnu.trove.impl.hash.TObjectHash;


/**
 * Iterator for hashtables that use open addressing to resolve collisions.
 *
 * @author Eric D. Friedman
 * @author Rob Eden
 * @author Jeff Randall
 * @version $Id: TObjectHashIterator.java,v 1.1.2.4 2009/10/09 01:44:34 robeden Exp $
 */

public class TObjectHashIterator<E> extends THashIterator<E> {

    protected final TObjectHash _objectHash;


    public TObjectHashIterator( TObjectHash<E> hash ) {
        super( hash );
        _objectHash = hash;
    }


    @SuppressWarnings("unchecked")
    protected E objectAtIndex( int index ) {
        Object obj = _objectHash._set[index];
        if ( obj == TObjectHash.FREE || obj == TObjectHash.REMOVED ) {
            return null;
        }
        return (E) obj;
    }

} // TObjectHashIterator
