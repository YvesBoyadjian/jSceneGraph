/**
 * 
 */
package jscenegraph.database.inventor;

import java.util.function.DoubleConsumer;

/**
 * @author Yves Boyadjian
 *
 */
public class SbVec3d {

	  protected final double[]       vec = new double[3];         //!< Storage for vector components
	
//
// Changes vector to be unit length
//

public double normalize()
{
    double len = length();

    if (len != 0.0)
        (this).operator_mul_equal(1.0 / len);

    else setValue(0.0, 0.0, 0.0);

    return len;
}

    //! Accesses indexed component of vector
    public double operator_square_bracket(int i)            { return (vec[i]); }


    //! Sets the vector components.
    public SbVec3d setValue(double x, double y, double z)
         { vec[0] = x; vec[1] = y; vec[2] = z; return this; }


//
// Component-wise scalar multiplication operator
//

public SbVec3d operator_mul_equal(double d)
{
    vec[0] *= d;
    vec[1] *= d;
    vec[2] *= d;

    return this;
}


//
// Returns geometric length of vector
//

public double length()
{
    return Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
}

public void copyFrom(SbVec3d other) {
	vec[0] = other.vec[0];
	vec[1] = other.vec[1];
	vec[2] = other.vec[2];
}


/**
 * java port
 * @return
 */
	public DoubleConsumer[] getRef() {
		DoubleConsumer[] ref = new DoubleConsumer[3];
		ref[0] = value -> vec[0] = value;
		ref[1] = value -> vec[1] = value;
		ref[2] = value -> vec[2] = value;
		return ref;
	}
}
