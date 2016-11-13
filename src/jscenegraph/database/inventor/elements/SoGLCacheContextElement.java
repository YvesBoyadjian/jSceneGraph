/*
 *
 *  Copyright (C) 2000 Silicon Graphics, Inc.  All Rights Reserved. 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  Further, this software is distributed without any warranty that it is
 *  free of the rightful claim of any third person regarding infringement
 *  or the like.  Any license provided herein, whether implied or
 *  otherwise, applies only to this software file.  Patent licenses, if
 *  any, provided herein do not apply to combinations of this program with
 *  other software, or any other product whatsoever.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Contact information: Silicon Graphics, Inc., 1600 Amphitheatre Pkwy,
 *  Mountain View, CA  94043, or:
 * 
 *  http://www.sgi.com 
 * 
 *  For further information regarding this notice, see: 
 * 
 *  http://oss.sgi.com/projects/GenInfo/NoticeExplan/
 *
 */


/*
 * Copyright (C) 1990,91   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.1.1.1 $
 |
 |   Description:
 |      This file defines the SoGLCacheContextElement class.
 |
 |   Author(s)          : Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.elements;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.SbPList;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.misc.SoState;


///////////////////////////////////////////////////////////////////////////////
///
///  \class SoCacheContextElement
///  \ingroup Elements
///
///  Element that stores the cache context.  There is a method on the
///  render action to set this; if you are not rendering onto multiple
///  displays, you will not need to set this (assuming that caches are
///  shareable between different windows on the same display, which is
///  true of GLX).
///
///  This element should be set before traversal starts, and must not
///  be changed during traversal (you'll get a debug error if a node
///  tries to set the cache context during traversal).
///
///  This method also has API for quickly finding out whether or not
///  OpenGL extensions are supported.  Code that uses extension "foo"
///  should look something like:
///  #ifdef GL_EXT_foo
///      static int fooExtInt = -1;
///      if (fooExtInt == -1)
///          fooExtInt = SoGLCacheContextElement::getExtID("GL_EXT_foo");
///      if (SoGLCacheContextElement::extSupported(state, fooExtInt)) {
///          glFoo(... make extension foo calls...);
///      } else {
///  #endif
///          Extension not supported, make regular GL calls
///  #ifdef GL_EXT_foo
///      }
///  #endif
///
///  Arranging the code that way ensures that it both compiles on
///  systems that don't support the extension AND will run on any
///  OpenGL-capable machine, even if the application displays on
///  multiple displays (only some of which may support the extension).
///
//////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoGLCacheContextElement extends SoElement {
	
	// Internal struct:
private class extInfo {
    String string = new String();
    final SbPList support = new SbPList();
};

	
	
	//! the default maximum value for OpenInventor auto caching (it used to be 1000 in
	//! the original SGI sources, but that is too low for todays GPUs!)
	public static final int OIV_AUTO_CACHE_DEFAULT_MAX = 100000;

	
    private static SbPList     waitingToBeFreed;       //!< Allocated in ::init
    private static SbPList     extensionList;          //!< Allocated in ::init
	
	
    //! Two bits are stored.  Nodes that should be cached will set the
    //! DO_AUTO_CACHE bit, nodes that should NOT be cached will set the
    //! DONT_AUTO_CACHE bit.  By default, DO_AUTO_CACHE is FALSE unless
    //! remote rendering is being done.  DONT_AUTO_CACHE is FALSE by
    //! default.  Separators will auto cache if DO_AUTO_CACHE is TRUE
    //! and DONT_AUTO_CACHE is FALSE, otherwise they won't auto-cache.
    public enum AutoCache {
        DO_AUTO_CACHE(1),   //!< Hack warning: I rely on TRUE==DO_AUTO_CACHE
        DONT_AUTO_CACHE(2);
        
        int value;
        
        AutoCache(int value) {
        	this.value = value;        
        }
        public int getValue() {
        	return value;
        }
    };
	

	   protected		        GL2                 context;
	   protected	        boolean              is2PassTransp;
	   protected		        boolean              isRemoteRendering;
	   protected		        int                 autoCacheBits;
		    	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initializes element
//
// Use: public

public void
init(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
    context = null;
    is2PassTransp = false;
    autoCacheBits = 0;
}

    //! Used by Separators to set/reset the auto-caching bits:
public     static void         setAutoCacheBits(SoState state, int bits)
        { ((SoGLCacheContextElement )state.getElementNoPush(
            classStackIndexMap.get(SoGLCacheContextElement.class))).autoCacheBits = bits;
        }



public    static int          resetAutoCacheBits(SoState state)
        {
            SoGLCacheContextElement elt = (SoGLCacheContextElement )
                state.getElementNoPush(classStackIndexMap.get(SoGLCacheContextElement.class));
            int result = elt.autoCacheBits;
            //! Hack warning: I rely on TRUE==DO_AUTO_CACHE
            elt.autoCacheBits = elt.isRemoteRendering ? AutoCache.DO_AUTO_CACHE.getValue():0;
            
            return result;
        }

	   
	   
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Gets the cache context.  GL render caches do this when they are
//    opened (they need the context to free themselves properly).
//
// Use: public, static

public static GL2
get(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
    SoGLCacheContextElement       elt;

    elt = ( SoGLCacheContextElement )
        getConstElement(state, classStackIndexMap.get(SoGLCacheContextElement.class));

    return elt.context;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns TRUE if the current rendering context does 
//    mip-mapped textures quickly.
//
public boolean
areMipMapsFast(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
    return true;
}

	   
	   
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Overrides this method to compare contexts.
//
// Use: public

public boolean
matches( SoElement elt)
//
////////////////////////////////////////////////////////////////////////
{
    
    SoGLCacheContextElement cacheElt;

    cacheElt = (SoGLCacheContextElement ) elt;

    return (context       == cacheElt.context &&
            is2PassTransp == cacheElt.is2PassTransp);
}
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //     Create a copy of this instance suitable for calling matches()
	   //     on.
	   //
	   // Use: protected
	   
	  public SoElement 
	   copyMatchInfo()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       SoGLCacheContextElement result =
	           (SoGLCacheContextElement )getTypeId().createInstance();
	   
	       result.context = context;
	       result.is2PassTransp = is2PassTransp;
	   
	       return result;
	   }
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Frees up the given display lists right away, if possible.  If
//    not possible (because the state passed in is NULL or has a
//    different cache context than the display lists' state), this
//    adds the given display list/count to the list of display lists
//    that need to be freed the next time the given context is valid.
//    This method is necessary because nodes with caches can be
//    deleted at any time, but we can't necessarily send GL commands
//    to free up a display list at any time.
//
// Use: public, static

public static void
freeList(SoState state,
                                  SoGLDisplayList dl)
//
////////////////////////////////////////////////////////////////////////
{
    if (state != null  &&  get(state) == dl.getContext()) {
        dl.destructor();//delete dl;
    } else {
        waitingToBeFreed.append(dl);
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts an extension string to a unique integer to be used for
//    faster lookup (to avoid string comparisons)
//
// Use: public

public int
getExtID(String str)
//
////////////////////////////////////////////////////////////////////////
{
    for (int i = 0; i < extensionList.getLength(); i++) {
        extInfo e = (extInfo )(extensionList).operator_square_bracket(i);
        if (e.string.equals(str)) return i;
    }
    extInfo e = new extInfo();
    e.string = str;
    extensionList.append(e);
    return extensionList.getLength()-1;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Given a unique extension ID, return TRUE if that extension is
//    supported in this rendering context.
//
public boolean
extSupported(SoState state, int ext)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    if (ext >= extensionList.getLength()) {
        SoDebugError.post("SoGLCacheContextElement::extSupported",
                           "Bad extension ID passed; "+
                           "you MUST use SoGLCacheContextElement::getExtID");
    }
//#endif
    extInfo e = (extInfo )(extensionList).operator_square_bracket(ext);
    GL2 ctx = get(state);

    // The support list is a list of context,flag pairs (flag is TRUE
    // if the render context supports the extension).  This linear
    // search assumes that there will be a small number of render
    // contexts.
    for (int i = 0; i < e.support.getLength(); i+=2) {
        if (e.support.operator_square_bracket(i) == ctx) return (Boolean)(e.support.operator_square_bracket(i+1));
    }
    String glExtensions = ctx.glGetString(GL2.GL_EXTENSIONS);
    // Ask GL if supported:
    boolean supported = (glExtensions.indexOf(e.string) != -1);
    e.support.append(ctx);
    e.support.append(supported);

    return supported;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initializes SoGLCacheContextElement class.
//
// Use: internal

public static void
initClass(final Class<? extends SoElement> javaClass)
{
    SoElement.initClass(javaClass);//SO_ELEMENT_INIT_CLASS(SoGLCacheContextElement, SoElement);
    waitingToBeFreed = new SbPList();
    extensionList = new SbPList();
}

    //! Called by nodes to say that they should/shouldn't be
    //! auto-cached (pass TRUE if should, FALSE if shouldn't, don't
    //! call this method at all if the node doesn't care):
    public static void         shouldAutoCache(SoState state, int bits)
    {
        SoGLCacheContextElement elt = (SoGLCacheContextElement )
            state.getElementNoPush(classStackIndexMap.get(SoGLCacheContextElement.class));
        elt.autoCacheBits |= bits;
    }


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sets cache context
//
// Use: public, static

public static void
set(SoState state, GL2 ctx,
                             boolean is2PassTransparency,
                             boolean remoteRender)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    if (state.getDepth() != 1) {
        SoDebugError.post("SoGLCacheContextElement::set",
                           "must not be set during traversal");
    }
//#endif

    SoGLCacheContextElement elt = (SoGLCacheContextElement )
        state.getElementNoPush(classStackIndexMap.get(SoGLCacheContextElement.class));

    elt.context = ctx;
    elt.is2PassTransp = is2PassTransparency;
    elt.isRemoteRendering = remoteRender;
    if (remoteRender) elt.autoCacheBits = AutoCache.DO_AUTO_CACHE.getValue();
    else elt.autoCacheBits = 0;

    // Look through the list of display lists waiting to be freed, and
    // free any that match the context:
    for (int i = waitingToBeFreed.getLength()-1; i >= 0; i--) {
        SoGLDisplayList dl = (SoGLDisplayList )(waitingToBeFreed).operator_square_bracket(i);
        if (dl.getContext() == ctx) {
            waitingToBeFreed.remove(i);
            dl.destructor();//delete dl;
        }
    }
}


public static boolean       getIsRemoteRendering(SoState state)
        {
            SoGLCacheContextElement elt =
                ( SoGLCacheContextElement )
                    state.getConstElement(classStackIndexMap.get(SoGLCacheContextElement.class));
            return elt.isRemoteRendering;
        }


	   }
