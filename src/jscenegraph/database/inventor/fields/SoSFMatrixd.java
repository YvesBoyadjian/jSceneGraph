/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SbMatrixd;

/**
 * @author Yves Boyadjian
 *
 */
public class SoSFMatrixd extends SoSField<SbMatrixd> { //TODO

	@Override
	protected SbMatrixd constructor() {
		return new SbMatrixd();
	}

}
