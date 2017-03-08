/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SbRotationd;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFRotationd extends SoMField<SbRotationd> { //TODO

	@Override
	protected SbRotationd constructor() {
		return new SbRotationd();
	}

	@Override
	protected SbRotationd[] arrayConstructor(int length) {
		return new SbRotationd[length];
	}

}
