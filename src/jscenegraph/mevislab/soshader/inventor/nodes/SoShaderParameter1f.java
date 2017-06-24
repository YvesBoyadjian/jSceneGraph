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

//! Open Inventor uniform shader node that defines one float parameter.
//! \file SoShaderParameter1f.h
//! \author Felix Ritter


package jscenegraph.mevislab.soshader.inventor.nodes;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.fields.SoSFFloat;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoSubNode;
import jscenegraph.mevislab.soshader.inventor.misc.SoUniformParameterBase;

/**
 * @author Yves Boyadjian
 *
 */
public class SoShaderParameter1f extends SoUniformShaderParameter {

	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_HEADER(SoShaderParameter1f.class,this);
	   
	   public                                                                     
	    static SoType       getClassTypeId()        /* Returns class type id */   
	                                    { return SoSubNode.getClassTypeId(SoShaderParameter1f.class);  }                   
	  public  SoType      getTypeId()      /* Returns type id      */
	  {
		  return nodeHeader.getClassTypeId();
	  }
	  public                                                                  
	    SoFieldData   getFieldData()  {
		  return nodeHeader.getFieldData();
	  }
	  public  static SoFieldData[] getFieldDataPtr()                              
	        { return SoSubNode.getFieldDataPtr(SoShaderParameter1f.class); }
	  
	   public

		      //! The parameter's value.
		     final  SoSFFloat value = new SoSFFloat();

	  
	   public SoShaderParameter1f()
	   {
	      nodeHeader.SO_NODE_CONSTRUCTOR(/*SoShaderParameter1f.class*/);

	      nodeHeader.SO_NODE_ADD_FIELD(value,"value", (0.f));
	   }

	
	/* (non-Javadoc)
	 * @see jscenegraph.mevislab.soshader.inventor.nodes.SoUniformShaderParameter#updateParameter(jscenegraph.mevislab.soshader.inventor.misc.SoUniformParameterBase, jscenegraph.database.inventor.misc.SoState)
	 */
	@Override
	public void updateParameter(SoUniformParameterBase uniformBase, SoState state) {
		   uniformBase.set1f(name.getValue(), value.getValue());
	}

	public static void
	initClass()
	{
	   SoSubNode.SO_NODE_INIT_CLASS(SoShaderParameter1f.class, SoUniformShaderParameter.class, "SoUniformShaderParameter");
	}

}
