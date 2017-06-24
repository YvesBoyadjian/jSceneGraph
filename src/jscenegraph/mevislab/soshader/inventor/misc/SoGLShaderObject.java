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

//! Vertex or fragment shader object class. 
//! \file \SoGLShaderObject.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.misc;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.SoDebug;
import jscenegraph.database.inventor.errors.SoError;
import jscenegraph.mevis.foundation.mlopengl.GLSLShader;

/**
 * @author Yves Boyadjian
 *
 */
//! Vertex or fragment shader object class. 
public class SoGLShaderObject extends SoGLShader {

      protected SoShader.ShaderType  _shaderType;
      protected boolean       _isActive;

      protected int _version;

      protected String     _source;
      protected boolean       _shouldCompile;

      protected String     _name;

      protected SoGLShaderProgram _shaderProgram;
      
      protected GLSLShader _shaderRes;

      protected boolean         _alwaysReadErrorLog;
      protected boolean         _hadError;
      protected String     _errorLog;
      
      public SoShader.ShaderType  shaderType() {
         return _shaderType;
      }

public void
attach(SoGLShaderProgram shaderProgram)
{
   if(shaderProgram == null || _shaderProgram == shaderProgram)
      return;

   detach();

   if(_shaderRes.getHandle() != 0) {
      _shaderProgram = shaderProgram;
      if (_shaderProgram.getProgramHandle() != 0 && _isActive) {
         errClear("SoGLShaderObject::attach");
        
         gl2.glAttachShader(_shaderProgram.getProgramHandle(), _shaderRes.getHandle());

         errCheck("SoGLShaderObject::attach");
      }
   }
}

public void
detach()
{
   if( _shaderProgram == null || _shaderProgram.getProgramHandle() == 0 || _shaderRes.getHandle() == 0) {
      _shaderProgram = null;
      return;
   }

   errClear("SoGLShaderObject::detach");

   _shaderProgram.getProgramResource().detachShader(_shaderRes);

   errCheck("SoGLShaderObject::detach");

   _shaderProgram.removeShaderObject(this);

   _shaderProgram = null;
}

public void
compile()
{
   if(!isSupported())
      return;
   
   if(!_shouldCompile && _shaderRes.isValid())
      return;
   
   if(!_shaderRes.isValid()) {
      _shaderRes.create();
      if(!_shaderRes.isValid())
         return;
   }
  
   String source = adaptSourceString();

   /*static*/ boolean debugShaderString = (SoDebug.GetEnv("IV_DEBUG_SHADER_STRING")!=null);
   if (debugShaderString) {
     System.out.println(source);
   }

   String[] str = new String[1]; str[0] = source;
   gl2.glShaderSource(_shaderRes.getHandle(), 1, str, null,0);
   gl2.glCompileShader(_shaderRes.getHandle());

   final int[] success = new int[1];
   gl2.glGetShaderiv(_shaderRes.getHandle(), GL2.GL_COMPILE_STATUS, success, 0);

   /*static*/ boolean debugShaders = SoDebug.GetEnv("IV_DEBUG_SHADER")!=null || SoDebug.GetEnv("IV_DEBUG_SHADER_LOG")!=null;
   _hadError = (success[0]==0);
   if(_alwaysReadErrorLog || success[0]==0 || debugShaders) {
      final int[] logLength = new int[1];
      gl2.glGetShaderiv(_shaderRes.getHandle(), GL2.GL_INFO_LOG_LENGTH, logLength,0);

      if (logLength[0] > 1) {
         byte[] log = new byte[logLength[0]];
         gl2.glGetShaderInfoLog(_shaderRes.getHandle(), logLength[0], null,0, log,0);

         if (_name.length()!=0) {
           SoError.post("SoGLShaderObject::create: Compile error in "+_name+" "+_shaderType.toString()+" shader:\n"+ log);
         } else {
           SoError.post("SoGLShaderObject::create: Compile error in "+_shaderType.toString()+" shader:\n"+ log);
         }
         _errorLog = new String(log);
     
         //delete [] log; java port
      } else {
        _errorLog = "";//.makeEmpty();
      }
   } else {
     _errorLog = "";//.makeEmpty();
   }

   // No compiler error?
   if(success[0]!=0 && _shaderProgram != null) {
      _shaderProgram.scheduleLinking();
   }
}

      public boolean        isActive() {
         return _isActive;
      }

public String adaptSourceString()
{
  String findVersionNumber = "#version\\s+(\\d+)";
  String findVersionNumberCompat ="#version\\s+\\d+\\s+compatibility";
  String source = _source;
  _version = 100;

  // detect the version TODO
//  boolean versionDetected = false;
//  boolean compatibilityDetected = false;
//  boost::match_results<std::string::const_iterator> what;
//  if (boost::regex_search(source, what, boost::regex(findVersionNumber))) {
//    std::string result(what[1].first, what[1].second);
//    sscanf(result.c_str(), "%d", &_version);
//    versionDetected = true;
//
//    if (boost::regex_search(source, boost::regex(findVersionNumberCompat))) {
//      // compatibility detected, don't mess with the shader...
//      compatibilityDetected = true;
//    }
//  }
//
//  if (!_emulateLegacyOpenGL || compatibilityDetected) {
    return source;
//  }

  // TODO
//  std::ostringstream str;
//  std::ostringstream preamble;
//  std::string uniforms;
//  if (_version >= 330) {
//    // if layout locations are supported, use them:
//    preamble << "#define OI_LOCATION(loc) layout(location = loc)\n";
//  } else {
//    // otherwise define as empty
//    preamble << "#define OI_LOCATION(loc)\n";
//  }
//
//  if (_version >= SOSHADER_CORE_GLSL_VERSION) {
//    uniforms =
//      "layout (std140) uniform ClipPlaneBlock { \n"
//      "  vec4 oi_ClipPlane[6];\n"
//      "};\n"
//      "#define gl_ClipPlane oi_ClipPlane\n"
//
//      "uniform mat4 oi_ModelViewProjectionMatrix;\n"
//      "#define gl_ModelViewProjectionMatrix oi_ModelViewProjectionMatrix\n"
//      "uniform mat4 oi_ModelViewMatrix;\n"
//      "#define gl_ModelViewMatrix oi_ModelViewMatrix\n"
//
//      "uniform mat4 oi_ModelViewProjectionMatrixInv;\n"
//      "#define gl_ModelViewProjectionMatrixInverse oi_ModelViewProjectionMatrixInv\n"
//      "uniform mat4 oi_ModelViewMatrixInv;\n"
//      "#define gl_ModelViewMatrixInverse oi_ModelViewMatrixInv\n"
//
//      "uniform mat3 oi_ModelViewNormalMatrix;\n"
//      "#define gl_NormalMatrix oi_ModelViewNormalMatrix\n"
//
//      "uniform mat4 oi_ProjectionMatrix;\n"
//      "#define gl_ProjectionMatrix oi_ProjectionMatrix\n"
//      "uniform mat4 oi_ProjectionMatrixInv;\n"
//      "#define gl_ProjectionMatrixInverse oi_ProjectionMatrixInv\n"
//
//      "struct oi_MaterialParameters {\n"
//      " vec4 emission;   \n"
//      " vec4 ambient;    \n"
//      " vec4 diffuse;    \n"
//      " vec4 specular;   \n"
//      " float shininess; \n" 
//      "};\n"
//      "layout (std140) uniform MaterialBlock { \n"
//      "  oi_MaterialParameters oi_FrontMaterial;\n"
//      "};\n"
//      "#define gl_FrontMaterial oi_FrontMaterial\n"
//
//      "struct oi_LightSourceParameters { \n"
//      "  vec4 ambient;\n"
//      "  vec4 diffuse;\n"
//      "  vec4 specular;\n"
//      "  vec4 position;\n"
//      "  vec3 spotDirection;\n"
//      "  float spotExponent;\n"
//      // spotCutoff not required...
//      //"  float spotCutoff;\n"
//      "  float spotCosCutoff;\n"
//      "  float constantAttenuation;\n"
//      "  float linearAttenuation;\n"
//      "  float quadraticAttenuation;\n"
//      "};\n"
//      // fixed number of lights for the moment:
//      "layout (std140) uniform LightBlock { \n"
//      "  oi_LightSourceParameters oi_LightSource[8];\n"
//      "};\n"
//      "#define gl_LightSource oi_LightSource\n"
//      
//      "struct oi_LightModelParameters { vec4 ambient; };\n"
//      "uniform oi_LightModelParameters oi_LightModel;\n"
//      "#define gl_LightModel oi_LightModel\n";
//
//    if (_shaderType == SoShader::FRAGMENT_SHADER)
//    {
//      preamble << 
//        "OI_LOCATION(0) out vec4 oi_FragData0;\n"
//        "OI_LOCATION(1) out vec4 oi_FragData1;\n"
//        "OI_LOCATION(2) out vec4 oi_FragData2;\n"
//        "OI_LOCATION(3) out vec4 oi_FragData3;\n"
//        "OI_LOCATION(4) out vec4 oi_FragData4;\n"
//        "OI_LOCATION(5) out vec4 oi_FragData5;\n"
//        "OI_LOCATION(6) out vec4 oi_FragData6;\n"
//        "OI_LOCATION(7) out vec4 oi_FragData7;\n"
//        "#define gl_FragColor oi_FragData0\n"
//        "#define varying in\n";
//    }
//    else if (_shaderType == SoShader::VERTEX_SHADER)
//    {
//      preamble << 
//        "OI_LOCATION(0) in vec4 oi_Vertex;\n"
//        "OI_LOCATION(1) in vec3 oi_Normal;\n"
//        "OI_LOCATION(2) in vec4 oi_Color;\n"
//        "OI_LOCATION(3) in vec2 oi_TexCoord;\n"
//        "#define gl_Vertex oi_Vertex\n"
//        "#define gl_Normal oi_Normal\n"
//        "#define gl_Color  oi_Color\n"
//        "#define gl_MultiTexCoord0 oi_TexCoord\n"
//        "#define varying out\n";
//    }
//  } else {
//    // old shaders:
//    if (_shaderType == SoShader::FRAGMENT_SHADER)
//    {
//      preamble <<
//        "#define oi_FragData0 gl_FragData[0]\n"
//        "#define oi_FragData1 gl_FragData[1]\n"
//        "#define oi_FragData2 gl_FragData[2]\n"
//        "#define oi_FragData3 gl_FragData[3]\n"
//        "#define oi_FragData4 gl_FragData[4]\n"
//        "#define oi_FragData5 gl_FragData[5]\n"
//        "#define oi_FragData6 gl_FragData[6]\n"
//        "#define oi_FragData7 gl_FragData[7]\n";
//    }
//  }
//  if (_version != 100) {
//    str << "#version " << _version << "\n";
//  }
//  str << preamble.str();
//  str << uniforms;
//  str << "#line 1\n";
//  if (versionDetected) {
//    std::string::const_iterator sourceBegin = source.begin();
//    str << source.substr(std::distance(sourceBegin, what[1].second));
//  } else {
//    str << source;
//  }
//  source = str.str();
  //return source;
}

//! Get the used GLSL version (only available after compile()!)
public int version() { return _version; }

}
