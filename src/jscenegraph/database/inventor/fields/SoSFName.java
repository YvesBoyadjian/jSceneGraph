/**
 * 
 */
package jscenegraph.database.inventor.fields;

import jscenegraph.database.inventor.SbName;

/**
 * @author Yves Boyadjian
 *
 */
public class SoSFName extends SoSField<SbName> {
	
	@Override
	protected SbName constructor() {		
		return new SbName();
	}

}
