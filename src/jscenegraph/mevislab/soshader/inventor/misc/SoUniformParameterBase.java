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

//! Base class for uniform shader parameters.
//! \file SoUniformParameterBase.h
//! \author Stephan Palmer, Felix Ritter


package jscenegraph.mevislab.soshader.inventor.misc;

import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.SbVec3s;

/**
 * @author Yves Boyadjian
 *
 */
public interface SoUniformParameterBase {

    public enum SamplerType {
        SAMPLER_1D,
        SAMPLER_2D,
        SAMPLER_3D,
        SAMPLER_CUBEMAP
      };

    //! Adds a texture sampler with the given name and returns the assigned texture unit
     int addTextureSampler(String name) ;//{ return 0; }
    
    // These functions are used to assign values to uniform parameter via OpenGL
     void set1f(String name, float value) ;//{}
     void set2f(String name, float[] value) ;//{}
     void set3f(String name, float[] value) ;//{}
     void set4f(String name, float[] value) ;//{}
     void set2f(String name, float  value0, float  value1) ;//{}
     void set3f(String name, float  value0, float  value1, float  value2) ;//{}
     void set4f(String name, float  value0, float  value1, float  value2, float  value3) ;//{}

     void set1fv(String name,  int num, float[] value) ;//{}
     void set2fv(String name,  int num, float[] value) ;//{}
     void set3fv(String name,  int num, float[] value) ;//{}
     void set4fv(String name,  int num, float[] value) ;//{}

     void set1i(String name,  int   value) ;//{}
     void set2i(String name,  int [] value) ;//{}
     void set2i(String name,  SbVec2s  value) ;//{}
     void set3i(String name,  int [] value) ;//{}
     void set3i(String name,  SbVec3s  value) ;//{}
     void set4i(String name,  int [] value) ;//{}

     default void setMatrix3f(String  name, float[]  values) {setMatrix3f( name, values, false);} // java port
     default void setMatrix3f(String  name, float[]  values, boolean transpose/* = false*/) { setMatrix3fv(name, 1, values, transpose); }
     default void setMatrix4f(String  name, float[]  values) {setMatrix4f(name, values, false);} // java port
     default void setMatrix4f(String  name, float[]  values, boolean transpose/* = false*/) { setMatrix4fv(name, 1, values, transpose); }
     default void setMatrix3fv(String name, int num, float[] value) {setMatrix3fv( name, num, value, false);} // java port
     void setMatrix3fv(String name, int num, float[] value, boolean transpose /*= false*/) ;//{}
     default void setMatrix4fv(String name, int num, float[] value) {setMatrix4fv( name, num, value, false);} // java port
     void setMatrix4fv(String name, int num, float[] value, boolean transpose /*= false*/) ;//{}


    // Provide additional information about the purpose of a uniform parameter. Those information
    // may be collected and analyzed by derived classes.
     default void setSamplerInfo(String name, SamplerType type, int texId) {}
}
