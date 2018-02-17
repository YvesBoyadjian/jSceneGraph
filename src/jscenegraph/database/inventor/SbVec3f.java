/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved. 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 * 
 *  http://www.sgi.com 
 * 
 *  For further information regarding this notice, see: 
 * 
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */


/*
 * Copyright (C) 1990,91   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.3 $
 |
 |   Description:
 |      This file contains definitions of various linear algebra classes,
 |      such as vectors, coordinates, etc..
 |
 |   Classes:
 |      SbVec3f
 |      SbVec2f
 |      SbVec2s
 |      SbVec3s         //!< Extension to SGI OIV 2.1
 |      SbVec4f
 |      SbRotation
 |      SbMatrix
 |      SbViewVolume
 |
 |      SbLine
 |      SbPlane
 |      SbSphere
 |
 |   Author(s)          : Paul S. Strauss, Nick Thompson, 
 |                        David Mott, Alain Dumesny
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor;

import java.nio.FloatBuffer;
import java.util.function.DoubleConsumer;

import jscenegraph.port.Mutable;

////////////////////////////////////////////////////////////////////////////////
//! 3D vector class.
/*!
\class SbVec3f
\ingroup Basics
3D vector class used to store 3D vectors and points. This class is used
throughout Inventor for arguments and return values.
{}

\par See Also
\par
SbVec2f, SbVec4f, SbVec2s, SbRotation
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SbVec3f implements Cloneable, Mutable {
	
	protected final float[] vec = new float[3]; 
	
	// Default constructor. 
	public SbVec3f() {
		
	}
	
	// java port
	public SbVec3f(SbVec3f other) {
		vec[0] = other.vec[0];
		vec[1] = other.vec[1];
		vec[2] = other.vec[2];
	}
	
	// Constructor given vector components. 
	public SbVec3f(float[] v) {
		 vec[0] = v[0]; vec[1] = v[1]; vec[2] = v[2]; 
	}

	/**
	 * Constructor given vector components.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public SbVec3f(float x, float y, float z)
	{ 
		vec[0] = x; vec[1] = y; vec[2] = z; 
	}
	
	public static int sizeof() {
		return 4*3;
	}
	
	// Returns right-handed cross product of vector and another vector. 
	public SbVec3f cross(final SbVec3f v) {
		
		  return new SbVec3f(vec[1] * v.vec[2] - vec[2] * v.vec[1],
				    vec[2] * v.vec[0] - vec[0] * v.vec[2],
				    vec[0] * v.vec[1] - vec[1] * v.vec[0]);
				   
				  }
	
	// Returns dot (inner) product of vector and another vector. 
	public float dot(SbVec3f v) {
		
		  return (vec[0] * v.vec[0] +
				    vec[1] * v.vec[1] +
				    vec[2] * v.vec[2]);
				  
				  }
	
	// Returns vector components. 
    public final float[] getValue() { return vec; }
    
    /**
     * For JOGL
     * @return
     */
    public final FloatBuffer getValueGL() {
    	return FloatBuffer.wrap(vec);
    }
	
    //
// Returns 3 individual components
//

public void
getValue(final float[] x, final float[] y, final float[] z)
{
    x[0] = vec[0];
    y[0] = vec[1];
    z[0] = vec[2];
}
        
	//
	 // Returns geometric length of vector
	 //
	 
	 public float length()
	 {
	  return (float)Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
	 }
	 	
	  public float sqrLength() { return vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]; }
	
	// Changes vector to be unit length, returning 
	// the length before normalization. 
	public float normalize() {
		
		 float len = length();
		  
		   if (len != 0.0)
			   operator_mul_equal(1.0f / len);
		  
		   else setValue(0.0f, 0.0f, 0.0f);
		  
		   return len;
		 	}
	
	// Negates each component of vector in place. 
	public void negate() {
	     vec[0] = -vec[0];
	     vec[1] = -vec[1];
	     vec[2] = -vec[2];	     		
	}
	
	// Sets the vector components. 
	public SbVec3f setValue(float[] v) {
		 vec[0] = v[0]; vec[1] = v[1]; vec[2] = v[2]; return this; 
	}
	
	// Sets the vector components. 
	public SbVec3f setValue(float x, float y, float z) {
		
		vec[0] = x; vec[1] = y; vec[2] = z; 
		return this;
	}
	
	// copy operator (java port)
	public void copyFrom(Object other) {
		SbVec3f sbVec3f = (SbVec3f)other;
		vec[0] = sbVec3f.vec[0];
		vec[1] = sbVec3f.vec[1];
		vec[2] = sbVec3f.vec[2];
	}
	
	protected Object clone() {
		SbVec3f cloned = new SbVec3f();
		cloned.copyFrom(this);
		return cloned;
	}
	
	 //
	   // Component-wise vector addition operator
	   //
	   
	  public SbVec3f 
	   operator_plus_equal(SbVec3f v)
	   {
	       vec[0] += v.vec[0];
	       vec[1] += v.vec[1];
	       vec[2] += v.vec[2];
	   
	       return this;
	   }
	   	
	  //
// Component-wise vector subtraction operator
//

public SbVec3f 
operator_minus_equal(SbVec3f v)
{
    vec[0] -= v.vec[0];
    vec[1] -= v.vec[1];
    vec[2] -= v.vec[2];

    return this;
}

	  
	
	//
	 // Component-wise scalar multiplication operator
	 //
	 
	public SbVec3f operator_mul_equal(float d) {
		
		  vec[0] *= d;
		    vec[1] *= d;
		    vec[2] *= d;
		   
		    return this;
	}
	
	// Component-wise scalar division operator. 
	public SbVec3f operator_div_equal(float d) {
		return operator_mul_equal(1.0f / d); 
	}
	
	// Component-wise vector addition operator.
	public SbVec3f operator_add_equal(SbVec3f v) {
		 vec[0] += v.vec[0];
		 vec[1] += v.vec[1];
		 vec[2] += v.vec[2];
		  
		   return this;		  		
	}
	
	// java port
	public float operator_square_bracket(int i) {
		return vec[i];
	}
	
	// java port
	public float operator_square_bracket(int i, float value) {
		return vec[i] = value;
	}
	
	// Component-wise vector addition and subtraction operators. 
	public SbVec3f substract(SbVec3f v) {
	     vec[0] -= v.vec[0];
          vec[1] -= v.vec[1];
          vec[2] -= v.vec[2];
      
          return this;
     	}

	// Component-wise binary scalar multiplication operator. 
	public SbVec3f operator_mul(float d) {
		SbVec3f v = this;
	     return new SbVec3f(v.vec[0] * d,
	    		                     v.vec[1] * d,
	    		                     v.vec[2] * d);
	    		  	}

	 //
	   // Component-wise binary vector addition operator
	   //
	   
	  public SbVec3f operator_add(SbVec3f v2) {
		  SbVec3f v1 = this;
		     return new SbVec3f(v1.vec[0] + v2.vec[0],
		    		                     v1.vec[1] + v2.vec[1],
		    		                     v1.vec[2] + v2.vec[2]);
		    		  	}
    
	  //
	   // Component-wise binary vector subtraction operator
	   //
	   
	  public SbVec3f operator_minus(SbVec3f v2) {
		  SbVec3f v1 = this;
		     return new SbVec3f(v1.vec[0] - v2.vec[0],
		    		                      v1.vec[1] - v2.vec[1],
		    		                      v1.vec[2] - v2.vec[2]);		    		  		  
	  }
	  
	  //
	// Nondestructive unary negation - returns a new vector
	//
	  public SbVec3f operator_minus() {
		  return new SbVec3f(-vec[0], -vec[1], -vec[2]);
	  }

	  public SbVec3f operator_div(float d)
	              { return operator_mul(1.0f / d); }
	  
	  public boolean operator_not_equal( SbVec3f v2)
{ 
		  SbVec3f v1 = this;
		  return !(v1.operator_equal_equal(v2)); 
}


//
// Equality comparison operator. Componenents must match exactly.
//

public boolean
operator_equal_equal( SbVec3f v2)
{
	SbVec3f v1 = this;
    return (v1.vec[0] == v2.vec[0] &&
            v1.vec[1] == v2.vec[1] &&
            v1.vec[2] == v2.vec[2]);
}

	  
	  
//
// Returns principal axis that is closest (based on maximum dot
// product) to this vector.
//

private float TEST_AXIS( final SbVec3f axis, final SbVec3f bestAxis, float max) {
		float d;
	    if ((d = dot(axis)) > max) {                                              
	        max = d;                                                              
	        bestAxis.copyFrom( axis);                                                      
	    }
	    return max;
}

public SbVec3f
getClosestAxis()
{
    final SbVec3f     axis = new SbVec3f(0.0f, 0.0f, 0.0f), bestAxis = new SbVec3f();
    float max = -21.234f;

    axis.getValue()[0] = 1.0f;      // +x axis
    max = TEST_AXIS(axis,bestAxis,max);

    axis.getValue()[0] = -1.0f;     // -x axis
    max = TEST_AXIS(axis,bestAxis,max);
    axis.getValue()[0] = 0.0f;

    axis.getValue()[1] = 1.0f;      // +y axis
    max = TEST_AXIS(axis,bestAxis,max);

    axis.getValue()[1] = -1.0f;     // -y axis
    max = TEST_AXIS(axis,bestAxis,max);
    axis.getValue()[1] = 0.0f;

    axis.getValue()[2] = 1.0f;      // +z axis
    max = TEST_AXIS(axis,bestAxis,max);

    axis.getValue()[2] = -1.0f;     // -z axis
    max = TEST_AXIS(axis,bestAxis,max);

    return bestAxis;
}

// java port
public float x() {
	return getValue()[0];
}
  
//java port
public float y() {
	return getValue()[1];
}
  
//java port
public float z() {
	return getValue()[2];
}

/**
 * Allocates an array of SbVec3f
 * @param num
 * @return
 */
public static SbVec3f[] allocate(int num) {
	SbVec3f[] retVal = new SbVec3f[num];
	for(int i=0; i< num;i++) {
		retVal[i] = new SbVec3f();
	}
	return retVal;
}

/**
 * java port
 * @return
 */
	public DoubleConsumer[] getRef() {
		DoubleConsumer[] ref = new DoubleConsumer[3];
		ref[0] = value -> vec[0] = (float)value;
		ref[1] = value -> vec[1] = (float)value;
		ref[2] = value -> vec[2] = (float)value;
		return ref;
	}

//
// Equality comparison operator within a tolerance.
//

public boolean
equals(final SbVec3f v, float tolerance)
{
    final SbVec3f     diff = this.operator_minus(v);

    return diff.dot(diff) <= tolerance;
}

public float getX() { // java port
	return vec[0];
}

public float getY() {
	return vec[1];
}

public float getZ() {
	return vec[2];
}

public void setX(float f) { // java port
	vec[0] = f;
}

public void setY(float f) {
	vec[1] = f;
}

public void setZ(float f) {
	vec[2] = f;
}

public float getValueAt(int axis) { // java port
	return vec[axis];
}

/**
 * java port
 * @param f
 */
public void multiply(float f) {
	operator_mul_equal(f);
}

public void add(SbVec3f v) {
	operator_add_equal(v);
}

/**
 * java port
 * @param other
 */
public void setValue(SbVec3f other) {
	setValue(other.getValue());
}

}
