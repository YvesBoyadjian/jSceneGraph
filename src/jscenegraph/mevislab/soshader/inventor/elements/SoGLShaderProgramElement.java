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

//! Open Inventor element representing a SoGLShaderProgram in the Open Inventor state.
//! \file SoGLShaderProgramElement.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.elements;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.elements.SoElement;
import jscenegraph.database.inventor.elements.SoReplacedElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.mevislab.soshader.inventor.misc.SoGLShaderProgram;

/**
 * @author Yves Boyadjian
 *
 */
public class SoGLShaderProgramElement extends SoReplacedElement {

    protected SoGLShaderProgram  _glShaderProgram;
    
public void destructor()
{
  _glShaderProgram = null;
  super.destructor();
}

//void
//  SoGLShaderProgramElement::initClass()
//{
//  SO_ELEMENT_INIT_CLASS(SoGLShaderProgramElement, inherited);
//}

public void init(SoState state)
{

  super.init(state);

  _glShaderProgram = null;
}

public void set(SoState state, SoNode node, SoGLShaderProgram glShaderProgram)
{
  // Get an instance we can change (pushing if necessary)
  SoGLShaderProgramElement elt = (SoGLShaderProgramElement)getElement(state, classStackIndexMap.get(SoGLShaderProgramElement.class), node);

  if(elt != null) {
    elt._glShaderProgram = glShaderProgram;
    if (glShaderProgram != null) {
      glShaderProgram.enable();
    } else {
    	GL2 gl2 = state.getGL2(); // java port
      // just disable the shader program usage
      gl2.glUseProgram(0);
    }
  }
}

public static SoGLShaderProgram get(SoState state)
{

  SoGLShaderProgramElement elt = (SoGLShaderProgramElement )getConstElement(state, classStackIndexMap.get(SoGLShaderProgramElement.class));  

  return elt._glShaderProgram;
}

public void push(SoState state)
{

  SoGLShaderProgramElement prevElt = (SoGLShaderProgramElement )getNextInStack();

  // copy the current shader, since it is still active in the new element
  _glShaderProgram = prevElt._glShaderProgram;
}

public void pop(SoState state, SoElement prevTopElement)
{
  // Since popping this element has GL side effects, make sure any
  // open caches capture it.  We may not send any GL commands, but
  // the cache dependency must exist even if we don't send any GL
  // commands, because if the element changes, the _lack_ of GL
  // commands here is a bug (remember, GL commands issued here are
  // put inside the cache).
  capture(state);

  SoGLShaderProgramElement prevElt = (SoGLShaderProgramElement )prevTopElement;

  if(_glShaderProgram != null) {
    _glShaderProgram.enable();
  } else {
    // Disable previous shader program if no other shader was enabled
    if(prevElt._glShaderProgram != null) {
    	GL2 gl2 = state.getGL2(); // java port
      gl2.glUseProgram(0);
      gl2.glActiveTexture(GL2.GL_TEXTURE0);
    }
  }
}
    
}
