/**
 * 
 */
package jscenegraph.database.inventor;

import java.util.function.IntConsumer;

import jscenegraph.port.Mutable;

/**
 * @author Yves Boyadjian
 *
 */
public class SbVec3s implements Mutable {
	protected final short[] vec = new short[3]; 
	

	/* (non-Javadoc)
	 * @see jscenegraph.port.Mutable#copyFrom(java.lang.Object)
	 */
	@Override
	public void copyFrom(Object other) {
		operator_assign((SbVec3s)other);
	}

	// java port
	public void operator_assign(SbVec3s other) {
		vec[0] = other.vec[0];
		vec[1] = other.vec[1];
		vec[2] = other.vec[2];
	}

	public IntConsumer[] getRef() {
		IntConsumer[] ret = new IntConsumer[3];
		ret[0] = value -> vec[0] = (short) value;
		ret[1] = value -> vec[1] = (short) value;
		ret[2] = value -> vec[2] = (short) value;
		return ret;
	}
	
}
