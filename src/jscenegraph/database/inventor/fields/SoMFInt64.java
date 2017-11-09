/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SoInput;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFInt64 extends SoMField<Long> {

	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.fields.SoMField#read1Value(jscenegraph.database.inventor.SoInput, int)
	 */
	@Override
	public boolean read1Value(SoInput in, int index) {
		final long[] ret = new long[1];
	    if (in.read(ret)) {
	    	values[index] = ret[0];
	    	return true;
	    }
		return false;
	}

	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.fields.SoMField#constructor()
	 */
	@Override
	protected Long constructor() {
		return new Long(0);
	}

	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.fields.SoMField#arrayConstructor(int)
	 */
	@Override
	protected Long[] arrayConstructor(int length) {
		return new Long[length];
	}

}
