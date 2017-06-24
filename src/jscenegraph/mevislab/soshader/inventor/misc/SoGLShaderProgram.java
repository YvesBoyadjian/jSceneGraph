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

//! Class managing an OpenGL shader program.
//! \file SoGLShaderProgram.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.SbVec3s;
import jscenegraph.database.inventor.SoDebug;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.errors.SoError;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.mevis.foundation.mlopengl.GLSLProgram;
import jscenegraph.port.Util;

/**
 * @author Yves Boyadjian
 *
 */
public class SoGLShaderProgram extends SoGLShader implements SoUniformParameterBase {

   protected
  
      /*ml::*/GLSLProgram  _programRes;
  
      protected int _geometryInputType;
      protected int _geometryOutputType;
      protected int _geometryVerticesOut;

      enum LinkageResult {
         LR_FAILURE /*= 0*/,
         LR_SUCCESS /*= 1*/,
         LR_SKIPPED /*= 2*/
      };

   protected
  
      final List<SoGLShaderObject > _shaderObjects = new ArrayList<>();
      protected boolean                        _shouldLink;
      protected       boolean                        _isExecutable;

      protected String                      _name;

      protected int                           _assignedTextureUnits;

      //! Information structure for SoGLShaderObject containing location, type, and size.
      protected class UniformInfo {
         public int  location;
         public /*GLenum*/int type;
         public int  size;
         public UniformInfo() {
        	 location = -1;
        	 type = GL2.GL_INT;
        	 size = 0;        	 
         }
         public UniformInfo(int _location, /*GLenum*/int _type, int _size) {
        	 location = _location;
        	 type = _type;
        	 size = _size;
         }
      };
      protected Map<String, UniformInfo> _uniformInfo;

      protected int getUniform( String name, /*GLenum*/int type) { // java port
    	  return getUniform(name, type, null);
      }

      protected boolean         _alwaysReadErrorLog;
      protected boolean         _hadError;
      protected String     _errorLog;
      
      public void setAlwaysReadErrorLog(boolean flag) { _alwaysReadErrorLog = flag; }
      public boolean alwaysReadErrorLog() { return _alwaysReadErrorLog; }

      public boolean hadLinkError() { return _hadError; }
      public String getErrorLog() { return _errorLog; }

      public /*ml::*/GLSLProgram getProgramResource() {
         return _programRes;
      }
   
      public int        getProgramHandle() {
         return _programRes.getHandle();
      }

      public boolean        isExecutable() {
         return _isExecutable;
      }

      public boolean        shouldLink() {
         return _shouldLink;
      }
   
      public void setName( String name) { _name = name; }

      public String getName() { return _name; }

      //! Update the shader uniforms according to the current Inventor state.
      //! To be reimplemented in derived classes.
      public void          updateUniformsFromState(SoState state) {};
      public void          preLinkSetup() {};
      public void          postLinkSetup() {};

      //! Clears the assigned texture units to 0
      public void          clearAssignedTextureUnits() { _assignedTextureUnits = 0; }

      int                   getNumAssignedTextureUnits() { return _assignedTextureUnits; }
      void                  setNumAssignedTextureUnits(int num) { _assignedTextureUnits = num; }

      public void          setGeometryInputType(int type) {
         if (_geometryInputType != type) { _geometryInputType = type; scheduleLinking(); }
      }
      public void          setGeometryOutputType(int type) {
         if (_geometryOutputType != type) { _geometryOutputType = type; scheduleLinking(); }
      }
      public void          setGeometryVerticesOut(int number) {
         if (_geometryVerticesOut != number) { _geometryVerticesOut = number; scheduleLinking(); }
      }


public SoGLShaderProgram()
{
   _shouldLink    = false;
   _isExecutable  = false;
  
   _geometryInputType  = GL2.GL_NONE;
   _geometryOutputType = GL2.GL_NONE;
   _geometryVerticesOut = 0;

   _assignedTextureUnits = 0;

   _hadError = false;
   _alwaysReadErrorLog = false;
}

public void destructor()
{
   disable();

   removeAllShaderObjects();
}


public void
reset()
{
   disable();

   removeAllShaderObjects();
      
   _programRes.destroy();
   
   super.reset();

   _shouldLink    = false;
   _isExecutable  = false;
}
   
public void
addShaderObject(SoGLShaderObject shaderObject)
{
   if(!isSupported())
      return;
      
   if(shaderObject != null) {
      if(/*std::find(_shaderObjects.begin(), _shaderObjects.end(), shaderObject) == _shaderObjects.end()*/!_shaderObjects.contains(shaderObject)) {
         _shaderObjects./*push_back*/add(shaderObject);
         _shouldLink = true;
      }
      else {
        if (SoDebug.GetEnv("IV_DEBUG_SHADER") != null) {
          SoDebugError.postWarning("SoGLShaderProgram::addShaderObject", "Trying to add "+/*SO_SHADER_TYPE_TO_STRING*/(shaderObject.shaderType().toString())+" shader that has been added to the shader program already.");
        }
      }
   }
}

public void
removeShaderObject(SoGLShaderObject shaderObject)
{
   if(!isSupported())
      return;
      
   if(shaderObject != null) {
      //std::list<SoGLShaderObject *>::iterator i;
      if(/*(i = std::find(_shaderObjects.begin(), _shaderObjects.end(), shaderObject)) != _shaderObjects.end()*/_shaderObjects.contains(shaderObject)) {
         SoGLShaderObject so = _shaderObjects.get(_shaderObjects.indexOf(shaderObject));
         _shaderObjects./*erase*/remove(so);
         so.detach();

         _shouldLink = true;
      }
   }
}

public void
removeAllShaderObjects()
{
   if(!isSupported())
      return;
      
   Iterator<SoGLShaderObject> i = _shaderObjects.iterator();//std::list<SoGLShaderObject *>::iterator i;
   for(/*i = _shaderObjects.begin()*/; i.hasNext() /*!= _shaderObjects.end()*/;) {
      SoGLShaderObject so = i.next();
      /*i = */_shaderObjects./*erase*/remove(so); //TODO
      so.detach();
   }
   _shouldLink = true;
}

public void
enable()
{
   if(!isSupported())
      return;
      
   // Make sure shader program is build
   link();

   if(_isExecutable) {
     gl2.glUseProgram(_programRes.getHandle());
   }
}

public void
disable()
{
   if(!isSupported())
      return;
      
   if(_isExecutable) {
      _programRes.disable();
   }
}

public void
scheduleLinking()
{
   _shouldLink = true;
}

public boolean isValid()
{
  return _programRes.isValid();
}

public SoGLShaderProgram.LinkageResult
link()
{
   if(!_shouldLink && _programRes.isValid()) {
      return SoGLShaderProgram.LinkageResult.LR_SKIPPED;
   }
   
   _isExecutable = false;
   
   errClear("SoGLShaderProgram::link");

   if(!_programRes.isValid()) {
      // make sure that all shaders are detached to enforce that they are recompiled when the context has changed
      //std::list<SoGLShaderObject *>::iterator i;
      for(SoGLShaderObject i :_shaderObjects) {
        (i).detach();
      }
      _programRes.create();
   }
   
   errCheck("SoGLShaderProgram::link");
   int programHandle = _programRes.getHandle();

   if(!_shaderObjects.isEmpty()) {

      int attachedShadersCount = 0;
      boolean setupGeometryShaderProgram = false;

      //std::list<SoGLShaderObject *>::const_iterator i;
      for(SoGLShaderObject i : _shaderObjects/*.begin(); i != _shaderObjects.end(); i++*/) {
         SoGLShaderObject so = i;
         so.compile();
         so.attach(this);
         if (so.isActive()) {
            attachedShadersCount++;
            
            if (!setupGeometryShaderProgram && so.shaderType() == SoShader.ShaderType.GEOMETRY_SHADER) {
               setupGeometryShaderProgram = true;
            }
         }
      }
        
      if (SoGLShader.supportsGeometryShaders() && setupGeometryShaderProgram) {
         // Since we may not emit an unbounded number of points from a geometry shader, we are required to let OpenGL know
         // the maximum number of points any instance of the shader will emit. This parameter needs to be set after creating
         // the program, but before linking
         if (_geometryVerticesOut > 0) {
            gl2.glProgramParameteri/*EXT*/(programHandle, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, _geometryVerticesOut);
         }
        
         // We must tell GL what kind of primitives your geometry shader will accept as input before linking the program
         gl2.glProgramParameteri/*EXT*/(programHandle, GL2.GL_GEOMETRY_INPUT_TYPE_EXT, _geometryInputType);

         // Likewise, we must tell GL what kind of primitives your geometry shader will output before linking the program
         // This does not necessarily correspond to the value of GL_GEOMETRY_INPUT_TYPE_EXT
         gl2.glProgramParameteri/*EXT*/(programHandle, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, _geometryOutputType);
      }

      _shouldLink = false;

      // binding the locations needs to be done before linking:
      preLinkSetup();

      gl2.glLinkProgram(programHandle);

      final int[] success = new int[1];
      gl2.glGetProgramiv(programHandle, GL2.GL_LINK_STATUS, success,0);

      /*static*/ boolean debugShaders = SoDebug.GetEnv("IV_DEBUG_SHADER")!=null || SoDebug.GetEnv("IV_DEBUG_SHADER_LOG")!=null;
      _hadError = (success[0]==0);
      if(_alwaysReadErrorLog || (success[0]==0) || debugShaders) {
         final int[] logLength = new int[1];
         gl2.glGetProgramiv(programHandle, GL2.GL_INFO_LOG_LENGTH, logLength,0);

         if (logLength[0] > 1) {
            byte[] log = new byte[logLength[0]];
            gl2.glGetProgramInfoLog(programHandle, logLength[0], null,0, log,0);

            if (_name.length()!=0) {
              SoError.post("SoGLShaderProgram::link: "+_name+"\n"+log+"\nLinking "+attachedShadersCount+" shader object(s)");
            } else {
              SoError.post("SoGLShaderProgram::link:\n"+log+"\nLinking "+attachedShadersCount+" shader object(s)");
            }
            _errorLog = new String(log);

            //delete [] log; java port
         } else {
           _errorLog = "";//.makeEmpty();
         }
      } else {
        _errorLog = "";//.makeEmpty();
      }

      // No linker error?
      if(success[0]!=0) {
         
         if(SoDebug.GetEnv("IV_DEBUG_SHADER")!= null) {
            SoDebugError.postInfo("SoGLShaderProgram::link", "Successfully linked "+attachedShadersCount+" shader object(s)");
         }
         
         updateUniformInfo(programHandle);

         _isExecutable = true;

         postLinkSetup();

         return SoGLShaderProgram.LinkageResult.LR_SUCCESS;
      }

      return SoGLShaderProgram.LinkageResult.LR_FAILURE;
   }
   
   return SoGLShaderProgram.LinkageResult.LR_SKIPPED;
}

public void updateUniformInfo(int programHandle)
{
  _uniformInfo.clear();

  // Query program about active uniform variables

  final int[] numUniforms = new int[1], maxUniformLen = new int[1];
  gl2.glGetProgramiv(programHandle, GL2.GL_ACTIVE_UNIFORMS, numUniforms,0);
  gl2.glGetProgramiv(programHandle, GL2.GL_ACTIVE_UNIFORM_MAX_LENGTH, maxUniformLen,0);

  final int[]   uSize = new int[1];
  final/*GLenum*/int[]  uType = new int[1];
  byte[] uName = new byte[maxUniformLen[0]];
  int   uLocation;

  for(int uIndex = 0; uIndex < numUniforms[0]; uIndex++) {
    gl2.glGetActiveUniform(programHandle, uIndex, maxUniformLen[0], null, 0, uSize, 0, uType, 0, uName, 0);
    uLocation = gl2.glGetUniformLocation(programHandle, new String(uName));

    // Store only information about user-defined uniform variables
    String uNameStr = new String(uName); // java port
    if(Util.strncmp(uNameStr, "gl_", 3) != 0) {
      int pos = Util.strstr(uNameStr, "[");
      if (pos >=0) {
        // remove the array part(s) from the uniform
    	  uNameStr = uNameStr.substring(0, pos);//pos[0] = 0; java port    	  
      }
      _uniformInfo.put(uNameStr, new UniformInfo(uLocation, uType[0], uSize[0]));
    }
  }

  //delete [] uName; java port
}

public boolean
isActiveUniform(final String name)
{
   return (_uniformInfo./*find*/containsKey(name)/* != _uniformInfo.end()*/);
}

public int
getUniform(final String name, /*GLenum*/int type, final int[] num)
{
   UniformInfo i = _uniformInfo.get(name);
   if(i == null) {
      if (SoDebug.GetEnv("IV_DEBUG_SHADER")!= null) {
         SoDebugError.postWarning("SoGLShaderProgram::getUniform", "Uniform shader parameter '"+name+"' is not part of the program.");
      }
   
      return -1;
   }

   final UniformInfo ui = i;
   
   // Since there are so many sampler types in OpenGL 4.x, we don't type check on int:
   if(type == GL2.GL_INT) {
       type = ui.type;
   }
   
   // Check compatibility
   if(ui.type == type) {
      if(num[0]!=0 && ui.size < num[0]) {
         if (SoDebug.GetEnv("IV_DEBUG_SHADER")!= null) {
            SoDebugError.postWarning("SoGLShaderProgram::getUniform", "Uniform shader parameter '"+name+"' to small in size. Only the first "+ui.size+" of "+num[0]+" values assigned.");
         }
         num[0] = ui.size;
      }
      return ui.location;
   }

   if (SoDebug.GetEnv("IV_DEBUG_SHADER")!=null) {
     SoDebugError.postWarning("SoGLShaderProgram::getUniform", "Uniform shader parameter '"+name+"' incompatible type requested.");
   }

   return -1;
}

public void
set1f(final String name, final float value)
{
   int loc = getUniform(name, GL2.GL_FLOAT);
   if(loc >= 0) {
      gl2.glUniform1f(loc, value);
   }
}

public void
set2f(final String name, final float[] value)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC2);
   if(loc >= 0) {
      gl2.glUniform2f(loc, value[0], value[1]);
   }
}

public void
set3f(final String name, final float[] value)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC3);
   if(loc >= 0) {
      gl2.glUniform3f(loc, value[0], value[1], value[2]);
   }
}

public void
set4f(final String name, final float[] value)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC4);
   if(loc >= 0) {
      gl2.glUniform4f(loc, value[0], value[1], value[2], value[3]);
   }
}
public void          
set2f(
		final String name, final float value0, final float value1)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC2);
   if(loc >= 0) {
      gl2.glUniform2f(loc, value0, value1);
   }
}

public void          
set3f(final String name, final float value0, final float value1, final float value2)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC3);
   if(loc >= 0) {
      gl2.glUniform3f(loc, value0, value1, value2);
   }
}

public void    
set4f(final String name, final float value0, final float value1, final float value2, final float value3)
{
   int loc = getUniform(name, GL2.GL_FLOAT_VEC4);
   if(loc >= 0) {
      gl2.glUniform4f(loc, value0, value1, value2, value3);
   }
}
public void
set1fv(final String name, final int num, final float[] value)
{
   final int[] myNum = new int[1]; myNum[0] = num;
   int loc = getUniform(name, GL2.GL_FLOAT, myNum);
   if(loc >= 0) {
      gl2.glUniform1fv(loc, myNum[0], value, 0);
   }
}

public void
set2fv(final String name, final int num, final float[] value)
{
   final int[] myNum = new int[1]; myNum[0] = num;
   int loc = getUniform(name, GL2.GL_FLOAT_VEC2, myNum);
   if(loc >= 0) {
      gl2.glUniform2fv(loc, myNum[0], value,0);
   }
}

public void
set3fv(final String name, final int num, final float[] value)
{
   final int[] myNum = new int[1]; myNum[0] = num;
   int loc = getUniform(name, GL2.GL_FLOAT_VEC3, myNum);
   if(loc >= 0) {
      gl2.glUniform3fv(loc, myNum[0], value,0);
   }
}

public void
set4fv(final String name, final int num, final float[] value)
{
   final int[] myNum = new int[1]; myNum[0] = num;
   int loc = getUniform(name, GL2.GL_FLOAT_VEC4, myNum);
   if(loc >= 0) {
      gl2.glUniform4fv(loc, myNum[0], value,0);
   }
}

public void
set1i(final String name, final int value)
{
   int loc = getUniform(name, GL2.GL_INT);
   if(loc >= 0) {
      gl2.glUniform1i(loc, value);
   }
}

public void
set2i(final String name, final int[] value)
{
   int loc = getUniform(name, GL2.GL_INT_VEC2);
   if(loc >= 0) {
      gl2.glUniform2i(loc, value[0], value[1]);
   }
}

public void
set2i(final String name, final SbVec2s value)
{
   int loc = getUniform(name, GL2.GL_INT_VEC2);
   if(loc >= 0) {
      gl2.glUniform2i(loc, value.getValue()[0], value.getValue()[1]);
   }
}

public void
set3i(final String name, final int[] value)
{
   int loc = getUniform(name, GL2.GL_INT_VEC3);
   if(loc >= 0) {
      gl2.glUniform3i(loc, value[0], value[1], value[2]);
   }
}

public void
set3i(final String name, final SbVec3s value)
{
   int loc = getUniform(name, GL2.GL_INT_VEC3);
   if(loc >= 0) {
      gl2.glUniform3i(loc, value.getValue()[0], value.getValue()[1], value.getValue()[2]);
   }
}

public void
set4i(final String name, final int[] value)
{
   int loc = getUniform(name, GL2.GL_INT_VEC4);
   if(loc >= 0) {
      gl2.glUniform4i(loc, value[0], value[1], value[2], value[3]);
   }
}

public void
setMatrix3fv(final String name, int num, final float[] value, boolean transpose)
{
   int loc = getUniform(name, GL2.GL_FLOAT_MAT3);
   if(loc >= 0) {
      gl2.glUniformMatrix3fv(loc, num, transpose, value,0);
   }
}

public void
setMatrix4fv(final String name, int num, final float[] value, boolean transpose)
{
   int loc = getUniform(name, GL2.GL_FLOAT_MAT4);
   if(loc >= 0) {
      gl2.glUniformMatrix4fv(loc, num, transpose, value,0);
   }
}

public int shaderVersion(SoShader.ShaderType type) 
{
  int version = 100;
  //std::list<SoGLShaderObject *>::const_iterator i;
  for(SoGLShaderObject i : _shaderObjects/*.begin(); i != _shaderObjects.end();++i*/) {
    if ((i).shaderType() == type && (i).isActive()) {
      int localVersion = (i).version();
      if (localVersion > version) {
        version = localVersion;
      }
    }
  }
  return version;
}

public int addTextureSampler( final String name )
{
  int texUnit = _assignedTextureUnits++;
  set1i(name, texUnit);
  return texUnit;
}      
      
}
