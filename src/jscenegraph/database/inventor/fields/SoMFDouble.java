/**
 * 
 */
package jscenegraph.database.inventor.fields;

/**
 * @author Yves Boyadjian
 *
 */
public class SoMFDouble extends SoMField<Double> { //TODO

	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.fields.SoMField#constructor()
	 */
	@Override
	protected Double constructor() {
		return new Double(0);
	}

	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.fields.SoMField#arrayConstructor(int)
	 */
	@Override
	protected Double[] arrayConstructor(int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
