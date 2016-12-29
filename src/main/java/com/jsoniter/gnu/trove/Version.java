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

package com.jsoniter.gnu.trove;

/**
 * Simple class meant as a possible main class (via manifest) to report the
 * implementation version of the trove4j jar.
 * <p/>
 * This may be useful to ask feedback WITH build version information
 * <p/>
 * The Main-Class entry in the manifest.mf should be set during the build as well
 * as the Implementation-Version manifest attribute should be set as well.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Johan Parent
 * Date: 3/03/11
 * Time: 22:10
 */
public class Version {
    public static void main(String[] args) {
        System.out.println(getVersion());
    }

    /**
     * Returns the implementation version of trove4j. Intended for applications
     * wanting to return the version of trove4j they are using
     * <p/>
     * NOTE: this method will only return a useful version when working
     * with a trove4j jar as it requires a manifest file
     *
     * @return
     */
    public static String getVersion() {
        String version = Version.class.getPackage().getImplementationVersion();
        //
        if (version != null) {
            return "trove4j version " + version;
        }

        return "Sorry no Implementation-Version manifest attribute available";
    }
}
