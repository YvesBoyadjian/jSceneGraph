/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SbTime;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFTime extends SoMField<SbTime> { //TODO

	@Override
	protected SbTime constructor() {
		return new SbTime();
	}

	@Override
	protected SbTime[] arrayConstructor(int length) {		
		return new SbTime[length];
	}

}
