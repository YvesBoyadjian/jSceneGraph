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

//! Abstract Open Inventor base class for uniform shader parameter nodes.
//! \file SoUniformShaderParameter.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.nodes;

import java.util.HashSet;
import java.util.Set;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoGLRenderAction;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoSubNode;
import jscenegraph.mevislab.soshader.inventor.elements.SoUniformShaderParameterElement;
import jscenegraph.mevislab.soshader.inventor.misc.SoUniformParameterBase;

/**
 * @author Yves Boyadjian
 *
 */
public abstract class SoUniformShaderParameter extends SoShaderParameter {

	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_ABSTRACT_HEADER(SoUniformShaderParameter.class,this);
	   
	   public                                                                     
	    static SoType       getClassTypeId()        /* Returns class type id */   
	                                    { return SoSubNode.getClassTypeId(SoUniformShaderParameter.class);  }                   
	  public  SoType      getTypeId()      /* Returns type id      */
	  {
		  return nodeHeader.getClassTypeId();
	  }
	  public                                                                  
	    SoFieldData   getFieldData()  {
		  return nodeHeader.getFieldData();
	  }
	  public  static SoFieldData[] getFieldDataPtr()                              
	        { return SoSubNode.getFieldDataPtr(SoUniformShaderParameter.class); }    	  	
	
      //! Auxiliary uniform shader parameters which are maintained by this node.
      public final Set<String> auxUniformParameters = new HashSet<>();


 public SoUniformShaderParameter()
{
   nodeHeader.SO_NODE_CONSTRUCTOR(/*SoUniformShaderParameter.class*/);
}

public static void
initClass()
{
   SoSubNode.SO_NODE_INIT_ABSTRACT_CLASS(SoUniformShaderParameter.class, SoShaderParameter.class, "SoShaderParameter");

   SO_ENABLE(SoGLRenderAction.class, SoUniformShaderParameterElement.class);
}

public void
GLRender(SoGLRenderAction action)
{
   SoUniformShaderParameterElement.add(action.getState(), this);
}

      //! Hook for internal preparation steps.
      //! The shader program has not yet been activated.
      public void updatePreparation(SoState state) {}
      
      //! Assigns or updates the parameter's value.
      //! The shader program is active.
      public abstract void updateParameter(SoUniformParameterBase uniformBase, SoState state);
   
}
