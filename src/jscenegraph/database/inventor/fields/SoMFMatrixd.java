/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SbMatrixd;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFMatrixd extends SoMField<SbMatrixd> { //TODO

	@Override
	protected SbMatrixd constructor() {
		return new SbMatrixd();
	}

	@Override
	protected SbMatrixd[] arrayConstructor(int length) {
		return new SbMatrixd[length];
	}

}
