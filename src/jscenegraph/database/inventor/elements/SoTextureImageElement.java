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
 |      This file defines the SoTextureImageElement class.
 |
 |   Author(s)          : Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.elements;

import jscenegraph.database.inventor.SbColor;
import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;


///////////////////////////////////////////////////////////////////////////////
///
///  \class SoTextureImageElement
///  \ingroup Elements
///
///  Element storing the current texture image.
///
//////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoTextureImageElement extends SoReplacedElement {

    protected final SbVec2s     size = new SbVec2s();
    int         numComponents;
    byte[] bytes;
    int         wrapS, wrapT, model;
    final SbColor     blendColor = new SbColor();

	

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sets texture image in element accessed from state.
//
// Use: public, static

public static void
set(final SoState state, final SoNode node,
                           final SbVec2s size, int nc,
                           byte[] b,
                           int wrapS, int wrapT, int model,
                           final SbColor blendColor)
//
////////////////////////////////////////////////////////////////////////
{
    SoTextureImageElement       elt;

    // Get an instance we can change (pushing if necessary)
    elt = (SoTextureImageElement ) getElement(state, classStackIndexMap.get(SoTextureImageElement.class), node);

    elt.setElt(size, nc, b, wrapS, wrapT, model, blendColor);
}

        
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Really do the set.  This is virtual so the GLTextureImageElement
//    can easily override the generic behavior to send textures to GL.
//
// Use: protected, virtual
protected void
setElt(final SbVec2s _size, 
                              int _numComponents,
                              byte[] _bytes,
                              int _wrapS, int _wrapT, int _model,
                              final SbColor _blendColor)
                              {
	SoTextureImageElement_setElt(_size,_numComponents,_bytes,_wrapS,_wrapT,_model,_blendColor);
                              }

protected void
SoTextureImageElement_setElt(final SbVec2s _size, 
                              int _numComponents,
                              byte[] _bytes,
                              int _wrapS, int _wrapT, int _model,
                              final SbColor _blendColor)
//
////////////////////////////////////////////////////////////////////////
{
    size.copyFrom(_size);
    numComponents = _numComponents;
    bytes = _bytes;
    wrapS = _wrapS;
    wrapT = _wrapT;
    model = _model;
    blendColor.copyFrom(_blendColor);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns texture image from state.
//
// Use: public, static

public static byte[]
get(final SoState state, final SbVec2s _size, 
                           final int[] _numComponents, final int[] _wrapS, final int[] _wrapT,
                           final int[] _model, final SbColor _blendColor)
//
////////////////////////////////////////////////////////////////////////
{
     SoTextureImageElement elt;

    elt = ( SoTextureImageElement )
        getConstElement(state, classStackIndexMap.get(SoTextureImageElement.class));

    _size.copyFrom(elt.size);
    _numComponents[0] = elt.numComponents;
    _wrapS[0] = elt.wrapS;
    _wrapT[0] = elt.wrapT;
    _model[0] = elt.model;
    _blendColor.copyFrom(elt.blendColor);

    return elt.bytes;
}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns TRUE if the texture contains transparency info.
//
// Use: public, static

public static boolean
containsTransparency(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
    SoTextureImageElement elt;

    elt = ( SoTextureImageElement )
        getConstElement(state, classStackIndexMap.get(SoTextureImageElement.class));

    return (elt.numComponents == 2 || elt.numComponents == 4);
}


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
    super.init(state);

    int[] dummy = new int[1]; dummy[0] = numComponents;
    bytes = getDefault(size, dummy);
    numComponents = dummy[0];
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Get default image (a NULL 0 by 0 by 0 image)
//
// Use: public

public byte[]
getDefault(final SbVec2s s, final int[] nc)
//
////////////////////////////////////////////////////////////////////////
{
    s.getValue()[0] = 0;
    s.getValue()[1] = 0;
    nc[0] = 0;
    return null;
}


}
