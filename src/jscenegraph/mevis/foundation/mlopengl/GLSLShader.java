/**
 * 
 */
package jscenegraph.mevis.foundation.mlopengl;

/**
 * @author Yves Boyadjian
 *
 */
public class GLSLShader extends GLResource {

  private int _shaderType;
  private int _id;
  
	/* (non-Javadoc)
	 * @see jscenegraph.mevis.foundation.mlopengl.GLResource#contextDestroyed()
	 */
	@Override
	public void contextDestroyed() {
		_id = 0;
	}

  //! get the program's handle (you need to create() the buffer before you get a handle)
  public int getHandle() { return _id; }
  
  //! check if the program is valid
  public boolean isValid() { return _id!=0; }
  
  //! create the program (requires valid GL context)
  public void create() {
	  //TODO
  }
  
  //! destroy the resource
  public void destroy() {
	  //TODO
  }
  
}
