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
 |      This file defines the SoText3 node class.
 |
 |   Author(s)          : Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.nodes;

import jscenegraph.database.inventor.SbBox3f;
import jscenegraph.database.inventor.SbVec3f;
import jscenegraph.database.inventor.actions.SoAction;


////////////////////////////////////////////////////////////////////////////////
//! 3D text shape node.
/*!
\class SoText3
\ingroup Nodes
This node defines one or more strings of 3D text. In contrast with
SoText2, 3D text can be rotated, scaled, lighted, and textured,
just like all other 3D shapes. Each character in a 3D text string is
created by extruding an outlined version of the character (in the
current typeface) along the current profile, as defined by nodes
derived from SoProfile. The default text profile, if none is
specified, is a straight line segment one unit long.


The text origin is at (0,0,0) after applying the current
transformation. The scale of the text is affected by the \b size 
field of the current SoFont as well as the current transformation.


SoText3 uses the current set of materials when rendering. If the
material binding is <tt>OVERALL</tt>, then the whole text is drawn with the
first material. If it is <tt>PER_PART</tt> or <tt>PER_PART_INDEXED</tt>, the
front part of the text is drawn with the first material, the sides
with the second, and the back with the third.


Textures are applied to 3D text as follows.  On the front and back
faces of the text, the texture origin is at the base point of the
first string; the base point is at the lower left for justification
<tt>LEFT</tt>, at the lower right for <tt>RIGHT</tt>, and at the lower center
for <tt>CENTER</tt>. The texture is scaled equally in both S and T
dimensions, with the font height representing 1 unit. S increases to
the right on the front faces and to the left on the back faces. On the
sides, the texture is scaled the same as on the front and back. S is
equal to 0 at the rear edge of the side faces. The T origin can occur
anywhere along each character, depending on how that character's
outline is defined.

\par File Format/Default
\par
\code
Text3 {
string ""
spacing 1
justification LEFT
parts FRONT
}
\endcode

\par Action Behavior
\par
SoGLRenderAction
<BR> Draws text based on the current font, profiles, transformation, drawing style, material, texture, complexity, and so on. 
\par
SoRayPickAction
<BR> Performs a pick on the text. The string index and character position are available from the SoTextDetail. 
\par
SoGetBoundingBoxAction
<BR> Computes the bounding box that encloses the text. 
\par
SoCallbackAction
<BR> If any triangle callbacks are registered with the action, they will be invoked for each successive triangle used to approximate the text geometry. 

\par See Also
\par
SoFont, SoProfile, SoText2, SoTextDetail
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 * TODO : classe à implémenter
 */
public class SoText3 extends SoShape {
	
   public
    //! Justification types
    enum Justification {
        LEFT    ( 0x01),
        RIGHT   ( 0x02),
        CENTER  ( 0x03);
        
        private int value;
        
        Justification(int value) {
        	this.value = value;
        }
        
        public int getValue() {
        	return value;
        }
    };

    //! Justification types
    public enum Part {
        FRONT   ( 0x01),
        SIDES   ( 0x02),
        BACK    ( 0x04),
        ALL     ( 0x07);
        
        private int value;
        
        Part(int value) {
        	this.value = value;
        }
        
        public int getValue() {
        	return value;
        }
    };

	

	@Override
	public void computeBBox(SoAction action, SbBox3f box, SbVec3f center) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void generatePrimitives(SoAction action) {
		// TODO Auto-generated method stub
		
	}

}
