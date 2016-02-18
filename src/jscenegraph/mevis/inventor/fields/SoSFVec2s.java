/**
 * 
 */
package jscenegraph.mevis.inventor.fields;

import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.fields.SoField;
import jscenegraph.database.inventor.fields.SoSField;


/**
 * @author Yves Boyadjian
 *
 */
public class SoSFVec2s extends SoSField<SbVec2s> {

	/* (non-Javadoc)
	 * @see com.openinventor.inventor.fields.SoField#copyFrom(com.openinventor.inventor.fields.SoField)
	 */
	@Override
	public void copyFrom(SoField f) {
		// TODO Auto-generated method stub

	}

	// Set value from 2 shorts.
	public void setValue(short x, short y) {
		  setValue(new SbVec2s(x, y));		
	}

	@Override
	protected SbVec2s constructor() {		
		return new SbVec2s();
	}

}
