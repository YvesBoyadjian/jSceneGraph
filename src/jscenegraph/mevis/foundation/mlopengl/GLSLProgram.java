/**
 * 
 */
package jscenegraph.mevis.foundation.mlopengl;

/**
 * @author Yves Boyadjian
 *
 */
public class GLSLProgram extends GLResource {
	
	private int _id;

	  //! disable program
	public void disable() {
		//TODO
	}
	  
	  //! create the program (requires valid GL context)
	  public void create() {
		  //TODO
	  }

	  //! destroy the resource
	public void destroy() {
		//TODO
	}

	/* (non-Javadoc)
	 * @see jscenegraph.mevis.foundation.mlopengl.GLResource#contextDestroyed()
	 */
	@Override
	public void contextDestroyed() {
		_id = 0;
	}
	
	public int getHandle() {
		return _id;
	}

	public boolean isValid() {
		return _id!=0;
	}
	
  //! detach the given shader (this is safe to be called outside of a valid GL context)
  public void detachShader(final GLSLShader shader) {
	  //TODO
  }

	
}
