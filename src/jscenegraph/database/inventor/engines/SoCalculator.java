/**
 * 
 */
package jscenegraph.database.inventor.engines;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.fields.SoMFFloat;
import jscenegraph.database.inventor.fields.SoMFString;
import jscenegraph.database.inventor.fields.SoMFVec3f;


/**
 * @author Yves Boyadjian
 *
 */
public class SoCalculator extends SoEngine {
	
	public final SoMFFloat a = new SoMFFloat(),b = new SoMFFloat(),c = new SoMFFloat(),d = new SoMFFloat(),e = new SoMFFloat(),f = new SoMFFloat(),g = new SoMFFloat(),h = new SoMFFloat();
	
	public final SoMFVec3f A = new SoMFVec3f(),B = new SoMFVec3f(),C = new SoMFVec3f(),D = new SoMFVec3f(),E = new SoMFVec3f(),F = new SoMFVec3f(),G = new SoMFVec3f(),H = new SoMFVec3f();

	public final SoMFString expression = new SoMFString();
	
	public final SoEngineOutput oa = new SoEngineOutput(),ob = new SoEngineOutput(),oc = new SoEngineOutput(),od = new SoEngineOutput();
	public final SoEngineOutput oA = new SoEngineOutput(),oB = new SoEngineOutput(),oC = new SoEngineOutput(),oD = new SoEngineOutput();
	 	
	public SoCalculator() {
		super();
		engineHeader = SoSubEngine.SO_ENGINE_HEADER(SoCalculator.class,this);
	   	
		//TODO
	}
	
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
	 * @see com.openinventor.inventor.misc.SoBase#getTypeId()
	 */
	@Override
	public SoType getTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

}
