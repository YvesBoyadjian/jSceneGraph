/**
 * 
 */
package jscenegraph.coin3d.glue;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

/**
 * @author Yves Boyadjian
 *
 */
public class cc_glglue {
	
	public GL2 contextid;
	GL2 gl2;

	public cc_glglue(GL2 gl2) {
		this.gl2 = gl2;
		this.contextid = gl2;
	}

	public GL2 getGL2() {
		return gl2;
	}

	public void glUniform1fARB(int location, float value) {
		gl2.glUniform1fARB(location, value);
	}

	public void glUniform2fARB(int location, float f, float g) {
		gl2.glUniform2fARB(location, f, g);
	}

	public void glUniform3fARB(int location, float f, float g, float h) {
		gl2.glUniform3fARB(location, f, g, h);
	}

	public void glUniform4fARB(int location, float f, float g, float h, float i) {
		gl2.glUniform4fARB(location, f, g, h, i);
	}

	public void glUniform1fvARB(int location, int i, float[] value) {
		gl2.glUniform1fvARB(location, i, value, 0);
	}

	public void glUniform2fvARB(int location, int i, float[] value) {
		gl2.glUniform2fvARB(location,i,value,0);
	}

	public void glUniform3fvARB(int location, int i, float[] value) {
		gl2.glUniform3fvARB(location, i, value,0);
	}

	public void glUniform4fvARB(int location, int i, float[] value) {
		gl2.glUniform4fvARB(location,i,value,0);
	}

	public void glUniformMatrix4fvARB(int location, int i, boolean b, float[] value) {
		gl2.glUniformMatrix4fvARB(location,i,b,value,0);
	}

	public void glUniform1iARB(int location, int value) {
		gl2.glUniform1iARB(location,value);
	}

	public void glUniform2iARB(int location, int i, int j) {
		gl2.glUniform2iARB(location,i,j);
	}

	public void glUniform3iARB(int location, int i, int j, int k) {
		gl2.glUniform3iARB(location,i,j,k);
	}

	public void glUniform4iARB(int location, int i, int j, int k, int l) {
		gl2.glUniform4iARB(location,i,j,k,l);
	}

	public void glUniform1ivARB(int location, int num, int[] value) {
		gl2.glUniform1ivARB(location,num,value,0);
	}

	public void glUniform2ivARB(int location, int num, int[] value) {
		gl2.glUniform2ivARB(location,num,value,0);
	}

	public void glUniform3ivARB(int location, int num, int[] v) {
		gl2.glUniform3ivARB(location,num,v,0);
	}

	public void glUniform4ivARB(int location, int num, int[] v) {
		gl2.glUniform4ivARB(location,num,v,0);
	}

	public int glGetUniformLocationARB(long pHandle, String name) {
		return gl2.glGetUniformLocationARB(pHandle, name);
	}

	public void glGetObjectParameterivARB(int pHandle, int glObjectActiveUniformsArb, int[] activeUniforms) {
		gl2.glGetObjectParameterivARB(pHandle, glObjectActiveUniformsArb, activeUniforms,0);
	}

	public void glGetActiveUniformARB(long pHandle, int i, int j, int[] length, int[] tmpSize, int[] tmpType,
			byte[] myName) {
		gl2.glGetActiveUniformARB(pHandle,i,j,length,0,tmpSize,0,tmpType,0,myName,0);
	}

	  static final String INVALID_VALUE = "GL_INVALID_VALUE";
	  static final String INVALID_ENUM = "GL_INVALID_ENUM";
	  static final String INVALID_OPERATION = "GL_INVALID_OPERATION";
	  static final String STACK_OVERFLOW = "GL_STACK_OVERFLOW";
	  static final String STACK_UNDERFLOW = "GL_STACK_UNDERFLOW";
	  static final String OUT_OF_MEMORY = "GL_OUT_OF_MEMORY";
	  static final String unknown = "Unknown OpenGL error";

/* Convert an OpenGL enum error code to a textual representation. */
public String
coin_glerror_string(/*GLenum*/int errorcode)
{
  switch (errorcode) {
  case GL2.GL_INVALID_VALUE:
    return INVALID_VALUE;
  case GL2.GL_INVALID_ENUM:
    return INVALID_ENUM;
  case GL2.GL_INVALID_OPERATION:
    return INVALID_OPERATION;
  case GL3.GL_STACK_OVERFLOW:
    return STACK_OVERFLOW;
  case GL3.GL_STACK_UNDERFLOW:
    return STACK_UNDERFLOW;
  case GL2.GL_OUT_OF_MEMORY:
    return OUT_OF_MEMORY;
  default:
    return unknown;
  }
}

public long glCreateShaderObjectARB(int sType) {
	return gl2.glCreateShaderObjectARB(sType);
}

public void glShaderSourceARB(long shaderHandle, int count, String srcStr, int[] length) {
	String[] strs = new String[1]; strs[0] = srcStr;
	gl2.glShaderSourceARB(shaderHandle, count, strs, length, 0);
}

public void glCompileShaderARB(long shaderHandle) {
	gl2.glCompileShaderARB(shaderHandle);
}

public void glDeleteObjectARB(long shaderHandle) {
	gl2.glDeleteObjectARB(shaderHandle);
}

public void glGetObjectParameterivARB(long shaderHandle, int glObjectCompileStatusArb, int[] flag) {
	gl2.glGetObjectParameterivARB(shaderHandle, glObjectCompileStatusArb, flag,0);
}

public void glAttachObjectARB(long programHandle, long shaderHandle) {
	gl2.glAttachObjectARB(programHandle, shaderHandle);
}

public void glDetachObjectARB(long programHandle, long shaderHandle) {
	gl2.glDetachObjectARB(programHandle, shaderHandle);
}

public void glGetInfoLogARB(long handle, int length, int[] charsWritten, byte[] infoLog) {
	gl2.glGetInfoLogARB(handle, length, charsWritten, 0, infoLog, 0);
}

public void glUseProgramObjectARB(long programhandle) {
	gl2.glUseProgramObjectARB(programhandle);
}

public void glLinkProgramARB(long programHandle) {
	gl2.glLinkProgramARB(programHandle);
}

public void glProgramParameteriEXT(long programHandle, int operator_square_bracket, int operator_square_bracket2) {
	gl2.glProgramParameteri((int)programHandle, operator_square_bracket, operator_square_bracket2);
}

public long glCreateProgramObjectARB() {
	return gl2.glCreateProgramObjectARB();
}

}
