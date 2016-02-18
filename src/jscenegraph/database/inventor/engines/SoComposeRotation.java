/**
 * 
 */
package jscenegraph.database.inventor.engines;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.engines.SoEngine;
import jscenegraph.database.inventor.engines.SoEngineOutput;
import jscenegraph.database.inventor.engines.SoEngineOutputData;
import jscenegraph.database.inventor.fields.SoMFFloat;
import jscenegraph.database.inventor.fields.SoMFVec3f;
import jscenegraph.port.Offset;


/**
 * @author Yves Boyadjian
 *
 */
public class SoComposeRotation extends SoEngine {
	
	// TODO : SO_COMPOSE_HEADER

	  public final SoMFVec3f axis = new SoMFVec3f();
	   
	  public final SoMFFloat angle = new SoMFFloat();
	   
	   
	   
	   
	  public final SoEngineOutput rotation = new SoEngineOutput();
	   
	  	
	/* (non-Javadoc)
	 * @see com.openinventor.inventor.engines.SoEngine#evaluate()
	 */
	@Override
	protected void evaluate() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.openinventor.inventor.engines.SoEngine#getOutputData()
	 */
	@Override
	public SoEngineOutputData getOutputData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.openinventor.inventor.fields.SoFieldContainer#plus(int)
	 */
	@Override
	public Object plus(Offset offset) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.openinventor.inventor.misc.SoBase#getTypeId()
	 */
	@Override
	public SoType getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

}
