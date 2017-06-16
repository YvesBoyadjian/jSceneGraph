// -----------------------------------------------------------------------
// 
// Copyright (c) 2001-2016, MeVis Medical Solutions AG, Bremen, Germany
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of MeVis Medical Solutions AG nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY MEVIS MEDICAL SOLUTIONS AG ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL MEVIS MEDICAL SOLUTIONS AG BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

//! Abstract base class for shader objects. 
//! \file SoGLShader.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.misc;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.glu.GLU;

import jscenegraph.database.inventor.SoDebug;
import jscenegraph.database.inventor.errors.SoDebugError;

/**
 * @author Yves Boyadjian
 *
 */
public class SoGLShader {
	
	public static final boolean GLEW_VERSION_2_0 = true; // java port
	public static final boolean GLEW_EXT_geometry_shader4 = true; // java port 
	public static final boolean GLEW_ARB_geometry_shader4 = true; // java port
	public static final boolean GLEW_VERSION_3_2 = true; // java port
	public static final int GLEW_ARB_texture_non_power_of_two = 1; // java port
	public static final int GLEW_ARB_texture_float = 1; // java port
	public static final int GLEW_ATI_texture_float = 1; // java port

	//! Starting with this GLSL version, we can no longer use deprecated GL state.
	public static final int SOSHADER_CORE_GLSL_VERSION  = 140;

   private
      
      static int     _isSupported = -1;

      private int _openGLError;
      
      //! Returns 'TRUE' if OpenGL error is found.
      protected boolean  error(GL2 gl2) { // java port
    	  return error(null, gl2);
      }
      protected boolean  error(String string, GL2 gl2) {
         errCheck(string, gl2); return _openGLError != GL2.GL_NO_ERROR;
      }

public SoGLShader()
{
}

public void
reset()
{
}

public boolean
isSupported(GL2 gl2)
{
   if(_isSupported < 0) {
      if(GLEW_VERSION_2_0) {
         _isSupported = true ? 1 : 0;

         if (SoDebug.GetEnv("IV_DEBUG_SHADER") != null) {
           printCapabilities(gl2);
         }
      }
      else {
         SoDebugError.post("SoGLShader::isSupported", "OpenGL shader API not supported, please upgrade your OpenGL driver.");

         _isSupported = false ? 1 : 0;
      }
   }

   return _isSupported == 1;
}

static boolean initialized = false;
static boolean supported = false;

public boolean supportsGeometryShaders()
{
  if (!initialized) {
    initialized = false;
    supported = (GLEW_EXT_geometry_shader4 ||
      GLEW_ARB_geometry_shader4 ||
      GLEW_VERSION_3_2);
  }
  return supported;
}

public void
printCapabilities(GL2 gl2)
{
   if(!isSupported(gl2))
      return;

   String capabilities = "";
   final int[] value = new int[1];

   // Get OpenGL release info.
   capabilities += "OpenGL release: ";
   capabilities += gl2.glGetString(GL2.GL_VERSION);
   capabilities += "\n";
   
   // Number of active vertex attributes allowed.
   gl2.glGetIntegerv(GL2.GL_MAX_VERTEX_ATTRIBS, value,0);
   capabilities += "Number of active vertex attributes allowed: ";
   capabilities += value[0];
   capabilities += "\n";

   // Number of active vertex shader uniform variables allowed.
   gl2.glGetIntegerv(GL2.GL_MAX_VERTEX_UNIFORM_COMPONENTS, value,0);
   capabilities += "Number of active vertex shader uniform variables allowed: ";
   capabilities += value[0];
   capabilities += "\n";
   
   // Number of varying variables allowed.
   gl2.glGetIntegerv(GL2.GL_MAX_VARYING_FLOATS, value,0);
   capabilities += "Number of varying variables allowed: ";
   capabilities += value[0];
   capabilities += "\n";
   
   // Number of active fragment shader uniform variables allowed.
   gl2.glGetIntegerv(GL2.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, value,0);
   capabilities += "Number of active fragment shader uniform variables allowed: ";
   capabilities += value[0];
   capabilities += "\n";
   
   // Number of texture units accessible from vertex shader.
   gl2.glGetIntegerv(GL2.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, value,0);
   capabilities += "Number of texture units accessible from vertex shader: ";
   capabilities += value[0];
   capabilities += "\n";
  
   // Number of texture units accessible from fragment shader.
   gl2.glGetIntegerv(GL2.GL_MAX_TEXTURE_IMAGE_UNITS, value,0);
   capabilities += "Number of texture units accessible from fragment shader: ";
   capabilities += value[0];
   capabilities += "\n";

   // Number of texture units accessible from vertex AND fragment shader.
   gl2.glGetIntegerv(GL2.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, value,0);
   capabilities += "Number of combined texture units accessible from shaders: ";
   capabilities += value[0];
   capabilities += "\n";

   // Number of texture coordinate sets available.
   gl2.glGetIntegerv(GL2.GL_MAX_TEXTURE_COORDS, value,0);
   capabilities += "Number of texture coordinate sets available: ";
   capabilities += value[0];
   capabilities += "\n";

   if (supportsGeometryShaders() /*!= 0*/) {
      capabilities += "'GL_EXT_geometry_shader4' extension present";
      capabilities += "\n";
      
      // Number of active vertex shader uniform variables allowed.
      gl2.glGetIntegerv(GL3.GL_MAX_GEOMETRY_UNIFORM_COMPONENTS_ARB, value,0);
      capabilities += "Number of active geometry shader uniform variables allowed: ";
      capabilities += value[0];
      capabilities += "\n";
     
      gl2.glGetIntegerv(GL3.GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS_EXT, value,0);
      capabilities += "Number of texture units accessible from geometry shader: ";
      capabilities += value[0];
      capabilities += "\n";
     
      gl2.glGetIntegerv(GL3.GL_MAX_GEOMETRY_OUTPUT_VERTICES_ARB, value,0);
      capabilities += "Number of vertices emitted by a geometry shader: ";
      capabilities += value[0];
      capabilities += "\n";
   } 

   if (GLEW_ARB_texture_non_power_of_two != 0) {
      capabilities += "'GL_ARB_texture_non_power_of_two' extension present";
      capabilities += "\n";
   }
   if (GLEW_ARB_texture_float != 0) {
      capabilities += "'GL_ARB_texture_float' extension present";
      capabilities += "\n";
   }
   if (GLEW_ATI_texture_float != 0) {
      capabilities += "'GL_ATI_texture_float' extension present";
      capabilities += "\n";
   }

   SoDebugError.postInfo("SoGLShader::printCapabilities", "\n"+ capabilities);
}

static String errString2;

public void
errClear(String string, GL2 gl2)
{
  _openGLError = gl2.glGetError();//ml.GLResource.getGLError(); // java port
  if (_openGLError != GL2.GL_NO_ERROR && _openGLError != GL2.GL_INVALID_OPERATION) {
	  GLU glu = new GLU();
    errString2 = glu.gluErrorString(_openGLError);
    
    SoDebugError.post("SoGLShader::errClear", "- "+string+" -  found previous OpenGL error: "+errString2+" (0x"+_openGLError+")");
  }
}

private static String errString;

public void
errCheck(String string, GL2 gl2) 
{
   
   _openGLError = gl2.glGetError();//ml.GLResource.getGLError(); // java port
   if (_openGLError != GL2.GL_NO_ERROR && _openGLError != GL2.GL_INVALID_OPERATION) {
		  GLU glu = new GLU();
      errString = glu.gluErrorString(_openGLError);

      SoDebugError.post("SoGLShader::errCheck", "- "+string+" -  OpenGL error: "+errString+" (0x"+_openGLError+")");
   }
}
      
}
