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
 |      This file defines the SoIndexedTriangleStripSet node class.
 |
 |   Author(s)          : Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.nodes;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.SoDebug;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoAction;
import jscenegraph.database.inventor.actions.SoGLRenderAction;
import jscenegraph.database.inventor.bundles.SoNormalBundle;
import jscenegraph.database.inventor.bundles.SoTextureCoordinateBundle;
import jscenegraph.database.inventor.caches.SoNormalCache;
import jscenegraph.database.inventor.elements.SoGLCacheContextElement;
import jscenegraph.database.inventor.elements.SoGLLazyElement;
import jscenegraph.database.inventor.elements.SoGLTextureCoordinateElement;
import jscenegraph.database.inventor.elements.SoLazyElement;
import jscenegraph.database.inventor.elements.SoShapeStyleElement;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.misc.SoNotList;
import jscenegraph.database.inventor.misc.SoNotRec;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoVertexPropertyCache.SoVPCacheFunc;

/**
 * @author Yves Boyadjian
 *
 */

////////////////////////////////////////////////////////////////////////////////
//! Indexed triangle strip set shape node.
/*!
\class SoIndexedTriangleStripSet
\ingroup Nodes
This shape node constructs triangle strips out of vertices located at
the coordinates specified in the \b vertexProperty  field
(from SoVertexShape), or the
current inherited coordinates. 
For optimal performance, the \b vertexProperty  field is recommended.


SoIndexedTriangleStripSet uses the
indices in the \b coordIndex  field (from SoIndexedShape) to
specify the vertices of the triangle strips. An index of
<tt>SO_END_STRIP_INDEX</tt> (-1) indicates that the current strip has ended
and the next one begins.


The vertices of the faces are transformed by the current
transformation matrix. The faces are drawn with the current light
model and drawing style.


Treatment of the current material and normal binding is as follows:
<tt>PER_PART</tt> specifies a material or normal per strip.  <tt>PER_FACE</tt>
binding specifies a material or normal for each triangle.
<tt>PER_VERTEX</tt> specifies a material or normal for each vertex.  The
corresponding <tt>_INDEXED</tt> bindings are the same, but use the
\b materialIndex  or \b normalIndex  indices (see SoIndexedShape).
The default material binding is <tt>OVERALL</tt>. The
default normal binding is <tt>PER_VERTEX_INDEXED</tt> 


If any normals (or materials) are specified, Inventor assumes 
you provide the correct number of them, as indicated by the binding.
You will see unexpected results
if you specify fewer normals (or materials) than the shape requires.
If no normals are specified, they will be generated automatically.

\par File Format/Default
\par
\code
IndexedTriangleStripSet {
  coordIndex 0
  materialIndex -1
  normalIndex -1
  textureCoordIndex -1
}
\endcode

\par Action Behavior
\par
SoGLRenderAction
<BR> Draws a strip set based on the current coordinates, normals, materials, drawing style, and so on. 
\par
SoRayPickAction
<BR> Picks on the strip set based on the current coordinates and transformation.  Details about the intersection are returned in an SoFaceDetail. 
\par
SoGetBoundingBoxAction
<BR> Computes the bounding box that encloses all vertices of the strip set with the current transformation applied to them. Sets the center to the average of the coordinates of all vertices. 
\par
SoCallbackAction
<BR> If any triangle callbacks are registered with the action, they will be invoked for each successive triangle forming the strips of the set. 

\par See Also
\par
SoIndexedTriangleSet, SoCoordinate3, SoDrawStyle, SoFaceDetail, SoIndexedFaceSet, SoTriangleStripSet, SoVertexProperty
*/
////////////////////////////////////////////////////////////////////////////////

public class SoIndexedTriangleStripSet extends SoIndexedShape {

	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_HEADER(SoIndexedTriangleStripSet.class,this);
	   
	   public                                                                     
	    static SoType       getClassTypeId()        /* Returns class type id */   
	                                    { return SoSubNode.getClassTypeId(SoIndexedTriangleStripSet.class);  }                   
	  public  SoType      getTypeId()      /* Returns type id      */
	  {
		  return nodeHeader.getClassTypeId();
	  }
	  public                                                                  
	    SoFieldData   getFieldData()  {
		  return nodeHeader.getFieldData();
	  }
	  public  static SoFieldData[] getFieldDataPtr()                              
	        { return SoSubNode.getFieldDataPtr(SoIndexedTriangleStripSet.class); }    	  
	        
//! This coordinate index indicates that the current triangle ends and the
//! next triangle begins
	public static final int SO_END_STRIP_INDEX     = (-1);

    //! This enum is used to indicate the current material or normal binding
    public enum Binding {
        OVERALL,
        PER_STRIP,      PER_STRIP_INDEXED,
        PER_TRIANGLE,   PER_TRIANGLE_INDEXED,
        PER_VERTEX,     PER_VERTEX_INDEXED
    };
    
    //! Number of strips, total number of triangles, and number of
    //! vertices per strip:
    private int         numStrips;
    private int         numTriangles;
    private int[]       numVertices;

    //!Typedef of pointer to method on IndexedTriangleStripSet;
    //!This will be used to simplify declaration and initialization.
    private interface PMTSS {
    	public void run(SoIndexedTriangleStripSet set,SoGLRenderAction action);
    }
                                                  
    
    //! Array of function pointers to render functions:
    private static PMTSS[] renderFunc = new PMTSS[32];
    
	static {
		renderFunc[0] = (soIndexedTriangleStripSet, action) ->  soIndexedTriangleStripSet.OmOn(action);
		renderFunc[1] = (soIndexedTriangleStripSet, action) ->  soIndexedTriangleStripSet.OmOnT(action);
	}
    
// Constants for influencing auto-caching algorithm:
    private final static int AUTO_CACHE_ITSS_MIN_WITHOUT_VP = 20;

// And the number above which we'll say caches definitely SHOULDN'T be
// built (because they'll use too much memory):
    private final static int AUTO_CACHE_ITSS_MAX = SoGLCacheContextElement.OIV_AUTO_CACHE_DEFAULT_MAX;

	        
	  
	/* (non-Javadoc)
	 * @see jscenegraph.database.inventor.nodes.SoShape#generatePrimitives(jscenegraph.database.inventor.actions.SoAction)
	 */
	@Override
	protected void generatePrimitives(SoAction action) {
		// TODO Auto-generated method stub

	}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Constructor
//
// Use: public

public SoIndexedTriangleStripSet()
//
////////////////////////////////////////////////////////////////////////
{
  nodeHeader.SO_NODE_CONSTRUCTOR(/*SoIndexedTriangleStripSet.class*/);

  numStrips = numTriangles = -1;
  numVertices = null;

  isBuiltIn = true;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Destructor
//
// Use: private

public void destructor()
//
////////////////////////////////////////////////////////////////////////
{
  if (numVertices != null) {
	  numVertices = null;
  }
}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Implements GL rendering.
//
// Use: extender

public void GLRender(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
  SoState state = action.getState();

  // Get ShapeStyleElement
  final SoShapeStyleElement shapeStyle = SoShapeStyleElement.get(state);

  SoVertexProperty vp = getVertexProperty();
  
  // First see if the object is visible and should be rendered now:
  if (shapeStyle.mightNotRender()) {
    if (vpCache.mightNeedSomethingFromState(shapeStyle)) {
      vpCache.fillInColorAndTranspAvail(vp, state);
    }
    if (! shouldGLRender(action))
      return;
  }

  if (vpCache.mightNeedSomethingFromState(shapeStyle)) {
    vpCache.fillInCache(vp, state);

    // Setup numVertices, numStrips and numTriangles:
    if (numStrips < 0)
      countStripsAndTris();

    if (vpCache.shouldGenerateNormals(shapeStyle)) {

      // See if there is a normal cache we can use. If not,
      // generate normals and cache them.
      SoNormalCache normCache = getNormalCache();
      if (normCache == null || ! normCache.isValid(state)) {

        int numVerts = 0;
        for (int i = 0; i < numStrips; i++)
          numVerts += numVertices[i];

        final SoNormalBundle nb = new SoNormalBundle(action, false);
        nb.initGenerator(numVerts);
        generateDefaultNormals(state, nb);
        normCache = getNormalCache();
      }
      vpCache.numNorms = normCache.getNum();
      vpCache.normalPtr = normCache.getNormalsFloat();
    }

    SoTextureCoordinateBundle tcb = null;
    int useTexCoordsAnyway = 0;
    if (vpCache.shouldGenerateTexCoords(shapeStyle)) {
      state.push();
      tcb = new SoTextureCoordinateBundle(action, true, true);
    }
    else if (shapeStyle.isTextureFunction() && vpCache.haveTexCoordsInVP()){
      state.push();
      useTexCoordsAnyway = SoVertexPropertyCache.Bits.TEXCOORD_BIT.getValue();
      SoGLTextureCoordinateElement.setTexGen(state, this, null);
    }

    // Now that normals have been generated, can set up pointers
    // (this is a method on SoIndexedShape):
    setupIndices(numStrips, numTriangles, shapeStyle.needNormals(), 
      (shapeStyle.needTexCoords() || useTexCoordsAnyway != 0));

    //If lighting or texturing is off, this vpCache and other things
    //need to be reconstructed when lighting or texturing is turned
    //on, so we set the bits in the VP cache:
    if(! shapeStyle.needNormals()) vpCache.needFromState |= 
      SoVertexPropertyCache.Bits.NORMAL_BITS.getValue();
    if(! shapeStyle.needTexCoords()) vpCache.needFromState |= 
      SoVertexPropertyCache.Bits.TEXCOORD_BIT.getValue();

    // If doing multiple colors, turn on ColorMaterial:
    if (vpCache.getNumColors() > 1) {
      SoGLLazyElement.setColorMaterial(state, true);
    }
    //
    // Ask LazyElement to setup:
    //
    SoGLLazyElement lazyElt = (SoGLLazyElement )
      SoLazyElement.getInstance(state);

    if(vpCache.colorIsInVtxProp()){
      lazyElt.send(state, SoLazyElement.masks.ALL_MASK.getValue());
      lazyElt.sendVPPacked(state, (IntBuffer)vpCache.getColors(0));
    }
    else lazyElt.send(state, SoLazyElement.masks.ALL_MASK.getValue());

    GLRenderInternal(action, useTexCoordsAnyway, shapeStyle);

    // If doing multiple colors, turn off ColorMaterial:
    if (vpCache.getNumColors() > 1) {
      SoGLLazyElement.setColorMaterial(state, false);
      ((SoGLLazyElement )SoLazyElement.getInstance(state)).
        reset(state, SoLazyElement.masks.DIFFUSE_MASK.getValue());
    }


    // Influence auto-caching algorithm:
    if (coordIndex.getNum() < AUTO_CACHE_ITSS_MIN_WITHOUT_VP &&
      vpCache.mightNeedSomethingFromState(shapeStyle)) {
        SoGLCacheContextElement.shouldAutoCache(state,
          SoGLCacheContextElement.AutoCache.DO_AUTO_CACHE.getValue());
    } else if (coordIndex.getNum() > AUTO_CACHE_ITSS_MAX) {
        SoGLCacheContextElement.shouldAutoCache(state,
          SoGLCacheContextElement.AutoCache.DONT_AUTO_CACHE.getValue());
    }           

    if (tcb != null) {
      tcb.destructor();
      state.pop();
    }
    else if (useTexCoordsAnyway != 0) 
      state.pop();
  }
  else {
    // If doing multiple colors, turn on ColorMaterial:
    if (vpCache.getNumColors() > 1) {
      SoGLLazyElement.setColorMaterial(state, true);
    }
    //
    // Ask LazyElement to setup:
    //
    SoGLLazyElement lazyElt = (SoGLLazyElement )
      SoLazyElement.getInstance(state);

    if(vpCache.colorIsInVtxProp()){
      lazyElt.send(state, SoLazyElement.masks.ALL_MASK.getValue());
      lazyElt.sendVPPacked(state, (IntBuffer)vpCache.getColors(0));
    }
    else lazyElt.send(state, SoLazyElement.masks.ALL_MASK.getValue());

    GLRenderInternal(action, 0, shapeStyle);

    if (vpCache.getNumColors() > 1) {
      SoGLLazyElement.setColorMaterial(state, false);
      ((SoGLLazyElement )SoLazyElement.getInstance(state)).
        reset(state, SoLazyElement.masks.DIFFUSE_MASK.getValue());
    }

    // Influence auto-caching algorithm:
    if (coordIndex.getNum() > AUTO_CACHE_ITSS_MAX) {

        SoGLCacheContextElement.shouldAutoCache(state,
          SoGLCacheContextElement.AutoCache.DONT_AUTO_CACHE.getValue());
    }           
  }
  return;
}

	
///////////////////////////////////////////////////////////////////////////
//
// Description:
//      Count vertices in each strip, construct numVertices array.
//      must be invoked whenever is built, before
//      normal generation.
//
// use: private
//
///////////////////////////////////////////////////////////////////////////
private void  countStripsAndTris()
{
  if (numStrips > 0) return; // Already counted
  numStrips = 0;
  int i, numVerts = 0;
  for(i = 0; i < coordIndex.getNum(); i++){
    if (coordIndex.operator_square_bracket(i) == SO_END_STRIP_INDEX || 
      (i == coordIndex.getNum()-1)) {
        ++numStrips;
    } 
    if (coordIndex.operator_square_bracket(i) != SO_END_STRIP_INDEX) {
      ++numVerts;
    }
  }
  numTriangles = numVerts - 2*numStrips;

  numVertices = new int[numStrips];
  // Then fill in its values:
  int ns = 0;
  int nv = 0;
  for(i = 0; i< coordIndex.getNum(); i++){
    if (coordIndex.operator_square_bracket(i) == SO_END_STRIP_INDEX ){
      numVertices[ns] = nv;
      nv=0;
      ns++;               
    }
    else {
      nv++;
      if (i == coordIndex.getNum()-1){
        numVertices[ns] = nv;
      }
    }       
  }
}    

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Keep things up to date when my fields change
//
// Use: protected

public void notify(SoNotList list)
//
////////////////////////////////////////////////////////////////////////
{
  // If coordIndex changes, must recount:
  if (list.getLastRec().getType() == SoNotRec.Type.CONTAINER &&
    list.getLastField() == coordIndex) {
//      if (numVertices)
//        delete[] numVertices; not needed in java
      numVertices = null;
      numStrips = numTriangles = -1;
  }

  super.notify(list);
}    

private void GLRenderInternal( SoGLRenderAction  action, int useTexCoordsAnyway, SoShapeStyleElement shapeStyle )
{
  // Call the appropriate render loop:
  (renderFunc[useTexCoordsAnyway | 
    vpCache.getRenderCase(shapeStyle)]).run(this, action);

//#ifdef DEBUG
  if (SoDebug.GetEnv("IV_DEBUG_LEGACY_RENDERING") != null) {
    SoDebugError.postInfo("GLRenderInternal", getTypeId().getName().getString()+" Immediate Mode Rendering: "+numTriangles+" Triangles");
  }
//#endif
}

//////////////////////////////////////////////////////////////////////////
// Following preprocessor-generated routines handle all combinations of
// Normal binding (per vertex, per face, per part, overall/none)
// Color Binding (per vertex, per face, per part, overall)
// Textures (on or off)
//////////////////////////////////////////////////////////////////////////

// Material overall:

private void OmOn (SoGLRenderAction action ) {
	
	GL2 gl2 = action.getCacheContext();
	
    final int ns = numStrips;
    final int[] numverts = numVertices;
    final int[] vertexIndex = coordIndex.getValuesInt(0);
    // Send one normal, if there are any normals in vpCache:
    if (vpCache.getNumNormals() > 0)
	vpCache.sendNormal(gl2, (FloatBuffer)vpCache.getNormals(0));
    Buffer vertexPtr = vpCache.getVertices(0);
    final int vertexStride = vpCache.getVertexStride();
    SoVPCacheFunc vertexFunc = vpCache.vertexFunc;
    int v;
    int vtxCtr = 0;
    int numvertsIndex = 0;
    for (int strip = 0; strip < ns; strip++) {
	final int nv = (numverts[numvertsIndex]);
	gl2.glBegin(GL2.GL_TRIANGLE_STRIP);
	for (v = 0; v < nv-1; v+=2) {
		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES);vtxCtr++;
	    (vertexFunc).run(gl2, vertexPtr);

		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES);vtxCtr++;
	    (vertexFunc).run(gl2, vertexPtr);           
	}
	if (v < nv) { // Leftovers
		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES);vtxCtr++;
	    (vertexFunc).run(gl2, vertexPtr);         
	}
	gl2.glEnd();
	vtxCtr++;
	numvertsIndex++;
    }
}


private void OmOnT (SoGLRenderAction action ) {
	
	GL2 gl2 = action.getCacheContext();
	
    final int ns = numStrips;
    final int[] numverts = numVertices;
    final int[] vertexIndex = coordIndex.getValuesInt(0);
    // Send one normal, if there are any normals in vpCache:
    if (vpCache.getNumNormals() > 0)
	vpCache.sendNormal(gl2, (FloatBuffer)vpCache.getNormals(0));
    Buffer vertexPtr = vpCache.getVertices(0);
    final int vertexStride = vpCache.getVertexStride();
    SoVPCacheFunc vertexFunc = vpCache.vertexFunc;
    Buffer texCoordPtr = vpCache.getTexCoords(0);
    final int texCoordStride = vpCache.getTexCoordStride();
    SoVPCacheFunc texCoordFunc = vpCache.texCoordFunc;
    final Integer[] tCoordIndx = getTexCoordIndices();
    int v;
    int vtxCtr = 0;
    int numvertsIndex = 0;
    for (int strip = 0; strip < ns; strip++) {
	final int nv = (numverts[numvertsIndex]);
	gl2.glBegin(GL2.GL_TRIANGLE_STRIP);
	for (v = 0; v < nv-1; v+=2) {
		texCoordPtr.position(texCoordStride*tCoordIndx[vtxCtr]/Float.BYTES);
		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES); vtxCtr++;
	    (texCoordFunc).run(gl2, texCoordPtr);
	    (vertexFunc).run(gl2, vertexPtr);         

		texCoordPtr.position(texCoordStride*tCoordIndx[vtxCtr]/Float.BYTES);
		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES); vtxCtr++;           
	    (texCoordFunc).run(gl2, texCoordPtr);
	    (vertexFunc).run(gl2, vertexPtr);         
	}
	if (v < nv) { // Leftovers
		texCoordPtr.position(texCoordStride*tCoordIndx[vtxCtr]/Float.BYTES);
		vertexPtr.position(vertexStride*vertexIndex[vtxCtr]/Float.BYTES); vtxCtr++;
	    (texCoordFunc).run(gl2, texCoordPtr);
	    (vertexFunc).run(gl2, vertexPtr);         
	}
	gl2.glEnd();
	vtxCtr++;
	numvertsIndex++;
    }
}


	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    This initializes the SoIndexedTriangleStripSet class.
//
// Use: internal

public static void initClass()
//
////////////////////////////////////////////////////////////////////////
{
    SO__NODE_INIT_CLASS(SoIndexedTriangleStripSet.class,
                        "IndexedTriangleStripSet", SoIndexedShape.class);
}

	
}
