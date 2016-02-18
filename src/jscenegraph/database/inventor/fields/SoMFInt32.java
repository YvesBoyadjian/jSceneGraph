/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved. 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 * 
 *  http://www.sgi.com 
 * 
 *  For further information regarding this notice, see: 
 * 
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */


/*
 * Copyright (C) 1990,91   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.1.1.1 $
 |
 |   Classes:
 |      SoMFInt32
 |
 |   Author(s)          : Paul S. Strauss
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.fields;


////////////////////////////////////////////////////////////////////////////////
//! Multiple-value field containing any number of int32_t integers.
/*!
\class SoMFInt32
\ingroup Fields
A multiple-value field that contains any number of int32_t (32-bit)
integers.


SoMFInt32s are written to file as one or more integer values, in
decimal, hexadecimal or octal format.  When more than one value is
present, all of the values are enclosed in square brackets and
separated by commas; for example:
\code
[ 17, -0xE20, -518820 ]
\endcode

*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFInt32 extends SoMField<Integer> {

    public void setValues(int start, int[] newValues) {
    	Integer[] newIValues = new Integer[newValues.length];
    	for(int i=0;i<newValues.length;i++) {
    		newIValues[i] = new Integer(newValues[i]);
    	}
    	super.setValues(start,newIValues);
    }

	@Override
	protected Integer constructor() {
		return new Integer(0);
	}

	@Override
	protected Integer[] arrayConstructor(int length) {
		return new Integer[length];
	}
	
	// java port
	public int[] getValuesInt(int index) {
		Integer[] valuesInteger = getValues(index);
		int returnLength = valuesInteger.length;
		int[] returnValue = new int[returnLength];
		for(int i=0; i< returnLength;i++) {
			returnValue[i] = valuesInteger[i];
		}
		return returnValue;
	}
}
