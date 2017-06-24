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

//! Shader-releated types. 
//! \file SoShaderTypes.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.misc;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

/**
 * @author Yves Boyadjian
 *
 */
public class SoShader {

   public enum ShaderType {
      VERTEX_SHADER("vertex",GL2.GL_VERTEX_SHADER)/* = 0*/,
      FRAGMENT_SHADER("fragment",GL2.GL_FRAGMENT_SHADER),
      GEOMETRY_SHADER("geometry", GL3.GL_GEOMETRY_SHADER),
      
      // Must be last!
      NUM_SHADER_TYPES("",-1);
	   
	   private String string;
	   private int GLType;
	   
	   ShaderType(String string, int GLType) {
		   this.string = string;
		   this.GLType = GLType;
	   }
	   public String toString() {
		   return string;
	   }
   };
}
