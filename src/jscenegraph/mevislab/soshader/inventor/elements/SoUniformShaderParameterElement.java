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

//! Open Inventor element storing a list of shader parameter nodes.
//! \file SoUniformShaderParameterElement.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.elements;

import jscenegraph.database.inventor.SoNodeList;
import jscenegraph.database.inventor.elements.SoAccumulatedElement;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.mevislab.soshader.inventor.nodes.SoUniformShaderParameter;

/**
 * @author Yves Boyadjian
 *
 */
public class SoUniformShaderParameterElement extends SoAccumulatedElement {

   protected

      //! List of shader parameter nodes.
      final SoNodeList  _shaderParameters = new SoNodeList();


public void
init(SoState state)
{
   super.init(state);
      
   _shaderParameters.truncate(0);
}

public static void
add(SoState state, SoUniformShaderParameter shaderParameter)
{
   // Get an instance we can change (pushing if necessary)
   SoUniformShaderParameterElement elt = (SoUniformShaderParameterElement)getElement(state, classStackIndexMap.get(SoUniformShaderParameterElement.class));
   
   if(elt != null && shaderParameter != null) {
      if(elt._shaderParameters.find(shaderParameter) < 0) {
         elt._shaderParameters.append(shaderParameter);
      }
      elt.addNodeId(shaderParameter);
   }
}

public void
set(SoState state, SoUniformShaderParameter shaderParameter)
{
   // Get an instance we can change (pushing if necessary)
   SoUniformShaderParameterElement elt = (SoUniformShaderParameterElement)getElement(state, classStackIndexMap.get(SoUniformShaderParameterElement.class));
   
   if(elt != null && shaderParameter != null) {
      elt._shaderParameters.truncate(0);
      elt._shaderParameters.append(shaderParameter);
      elt.addNodeId(shaderParameter);
   }
}

public SoNodeList 
get(SoState state)
{
   final SoUniformShaderParameterElement elt = (SoUniformShaderParameterElement )getConstElement(state, classStackIndexMap.get(SoUniformShaderParameterElement.class));  
  
   return elt._shaderParameters;
}

public void
clear(SoState state, SoNode node)
{
   // Get an instance we can change (pushing if necessary)
   SoUniformShaderParameterElement elt = (SoUniformShaderParameterElement)getElement(state, classStackIndexMap.get(SoUniformShaderParameterElement.class));
   
   if(elt != null) {
      elt._shaderParameters.truncate(0);
      elt.addNodeId(node);
   }
}

public void
push(SoState state)
{
   SoUniformShaderParameterElement prevElt = (SoUniformShaderParameterElement )getNextInStack();
   
   // Rely on SoNodeList::operator = to do the right thing...
   _shaderParameters .copyFrom( prevElt._shaderParameters);
   nodeIds           .copyFrom( prevElt.nodeIds);
}
}
