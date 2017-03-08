/**
 * 
 */
package jscenegraph.database.inventor;

import jscenegraph.port.Mutable;

/**
 * @author Yves Boyadjian
 *
 */
public class SbMatrixd implements Mutable { //TODO
	
	  private  final double[][]      matrix = new double[4][4];         //!< Storage for 4x4 matrix

	    //! Default constructor
	  public SbMatrixd() { }


//
// Constructor from a 4x4 array of elements
//

public SbMatrixd(final double[][] m)
{
    matrix[0][0] = m[0][0];
    matrix[0][1] = m[0][1];
    matrix[0][2] = m[0][2];
    matrix[0][3] = m[0][3];
    matrix[1][0] = m[1][0];
    matrix[1][1] = m[1][1];
    matrix[1][2] = m[1][2];
    matrix[1][3] = m[1][3];
    matrix[2][0] = m[2][0];
    matrix[2][1] = m[2][1];
    matrix[2][2] = m[2][2];
    matrix[2][3] = m[2][3];
    matrix[3][0] = m[3][0];
    matrix[3][1] = m[3][1];
    matrix[3][2] = m[3][2];
    matrix[3][3] = m[3][3];
}

	

	/* (non-Javadoc)
	 * @see jscenegraph.port.Mutable#copyFrom(java.lang.Object)
	 */
	@Override
	public void copyFrom(Object other) {
		// TODO Auto-generated method stub

	}

    //! Sets matrix to rotate by given rotation.
    public void        setRotate(final SbRotationd rotation) {
        rotation.getValue(this);    	
    }

	// java port
	public final double[][] getValue() {
		return matrix;
	}
	

//
// Returns 4x4 array of elements
//

public void getValue(double[][] m)
{
    m[0][0] = matrix[0][0];
    m[0][1] = matrix[0][1];
    m[0][2] = matrix[0][2];
    m[0][3] = matrix[0][3];
    m[1][0] = matrix[1][0];
    m[1][1] = matrix[1][1];
    m[1][2] = matrix[1][2];
    m[1][3] = matrix[1][3];
    m[2][0] = matrix[2][0];
    m[2][1] = matrix[2][1];
    m[2][2] = matrix[2][2];
    m[2][3] = matrix[2][3];
    m[3][0] = matrix[3][0];
    m[3][1] = matrix[3][1];
    m[3][2] = matrix[3][2];
    m[3][3] = matrix[3][3];
}

	
}
