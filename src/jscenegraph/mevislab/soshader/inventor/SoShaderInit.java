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

package jscenegraph.mevislab.soshader.inventor;

import jscenegraph.mevislab.soshader.inventor.elements.SoGLShaderProgramElement;
import jscenegraph.mevislab.soshader.inventor.elements.SoUniformShaderParameterElement;
import jscenegraph.mevislab.soshader.inventor.nodes.SoShaderParameter;
import jscenegraph.mevislab.soshader.inventor.nodes.SoShaderParameter1f;
import jscenegraph.mevislab.soshader.inventor.nodes.SoUniformShaderParameter;

/**
 * @author Yves Boyadjian
 *
 */
public class SoShaderInit {
	
	private static boolean initialized;

public static void
init() // TODO : implement missing classes
{
	if(initialized) {
		return;
	}
	initialized = true;
	
   // DON'T CHANGE ORDER !!!
   
   // Elements
   //SoSamplerInfoElement.initClass(SoSamplerInfoElement.class);
   //SoMultiPassFramebufferSamplerElement.initClass(SoMultiPassFramebufferSamplerElement.class);
   //SoFramebufferSamplerElement.initClass(SoFramebufferSamplerElement.class);
   //SoShaderObjectElement.initClass(SoShaderObjectElement.class);
   SoUniformShaderParameterElement.initClass(SoUniformShaderParameterElement.class);
   SoGLShaderProgramElement.initClass(SoGLShaderProgramElement.class);
   //SoGLShaderProgramAllowedElement.initClass(SoGLShaderProgramAllowedElement.class);
   //SoGLVertexAttributeElement.initClass(SoGLVertexAttributeElement.class);

   // Fields
   //SoMFShaderObject.initClass();
   //SoMFUniformShaderParameter.initClass();
   
   // Abstract nodes
   //SoShaderObject.initClass();
   SoShaderParameter.initClass();
   SoUniformShaderParameter.initClass();
   //SoSampler.initClass();
   //SoSampler2D.initClass();
   //SoImageSampler.initClass();
   //SoMLSampler.initClass();
   
   // Nodes
   /*
   SoCheckShaderSupport.initClass();
   SoClearShaderState.initClass();
   SoFragmentShader.initClass();
   SoVertexShader.initClass();
   SoGeometryShader.initClass();
   SoShaderProgram.initClass();
   */
   SoShaderParameter1f.initClass();
   /*
   SoShaderParameter1fv.initClass();
   SoShaderParameter2f.initClass();
   SoShaderParameter3f.initClass();
   SoShaderParameter4f.initClass();
   SoShaderParameterPlane.initClass();
   SoShaderParameterRotation.initClass();
   SoShaderParameter1i.initClass();
   SoShaderParameter2i.initClass();
   SoShaderParameter3i.initClass();
   SoShaderParameterColor.initClass();
   SoShaderParameterMatrix.initClass();
   SoShaderParameterMLImageProps.initClass();
   SoShaderParameterMLImageSize.initClass();
   SoMultiplePass.initClass();
   SoGLRenderState.initClass();
   SoMLSampler1D.initClass();
   SoMLSampler2D.initClass();
   SoMLSampler3D.initClass();
   SoMLSamplerCubeMap.initClass();
   SoFramebufferSampler.initClass();
   SoFramebufferSampler2D.initClass();
   SoFramebufferSampler3D.initClass();
   SoInheritedFramebufferSampler.initClass();
   SoMultiPassFramebufferSampler.initClass();
   SoBlendMode.initClass();

   SoVertexAttributeBase.initClass();
   SoVertexAttribute1f.initClass();
   SoVertexAttribute2f.initClass();
   SoVertexAttribute3f.initClass();
   SoVertexAttribute4f.initClass();
   SoVertexAttributeRotation.initClass();
   SoVertexAttribute1i.initClass();
   SoVertexAttribute1ui.initClass();
   SoVertexAttribute4ub.initClass();
*/
}

}
