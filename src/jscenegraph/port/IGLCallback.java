/**
 * 
 */
package jscenegraph.port;

import javax.media.opengl.GL2;

/**
 * @author Yves Boyadjian
 *
 */
public interface IGLCallback {

	void run(GL2 gl, Object data);
}
