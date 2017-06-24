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
//----------------------------------------------------------------------------------
//! Collection of OPenGL related resource classes, such as textures, buffers, 
//! fragment programs etc.
/*!
// \file    mlGLResources.h
// \author  Florian Link
// \date    2/2006
*/
//----------------------------------------------------------------------------------

package jscenegraph.mevis.foundation.mlopengl;

import com.jogamp.opengl.GL2;

/**
 * @author Yves Boyadjian
 *
 */
public abstract class GLResource {

	public abstract void contextDestroyed();
	
	public static int getGLError() {
		return GL2.GL_NO_ERROR; // TODO
	}
}
