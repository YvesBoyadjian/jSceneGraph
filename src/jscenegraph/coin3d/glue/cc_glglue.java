/**
 * 
 */
package jscenegraph.coin3d.glue;

import com.jogamp.opengl.GL2;

/**
 * @author Yves Boyadjian
 *
 */
public class cc_glglue {
	
	GL2 gl2;

	public cc_glglue(GL2 gl2) {
		this.gl2 = gl2;
	}

	public GL2 getGL2() {
		return gl2;
	}

}
