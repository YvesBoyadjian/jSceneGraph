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
 |      This file defines the SoShape node class.
 |
 |   Author(s)          : Paul S. Strauss
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.nodes;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_COLOR_ARRAY;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_NORMAL_ARRAY;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_TEXTURE_COORD_ARRAY;
import static javax.media.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;
import javax.media.opengl.glu.gl2.GLUgl2;

import jscenegraph.database.inventor.SbBox2f;
import jscenegraph.database.inventor.SbBox3f;
import jscenegraph.database.inventor.SbColor;
import jscenegraph.database.inventor.SbMatrix;
import jscenegraph.database.inventor.SbVec2f;
import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.SbVec3f;
import jscenegraph.database.inventor.SbVec4f;
import jscenegraph.database.inventor.SoPickedPoint;
import jscenegraph.database.inventor.SoPrimitiveVertex;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoAction;
import jscenegraph.database.inventor.actions.SoCallbackAction;
import jscenegraph.database.inventor.actions.SoGLRenderAction;
import jscenegraph.database.inventor.actions.SoGetBoundingBoxAction;
import jscenegraph.database.inventor.actions.SoRayPickAction;
import jscenegraph.database.inventor.bundles.SoMaterialBundle;
import jscenegraph.database.inventor.details.SoDetail;
import jscenegraph.database.inventor.details.SoFaceDetail;
import jscenegraph.database.inventor.details.SoPointDetail;
import jscenegraph.database.inventor.elements.SoComplexityTypeElement;
import jscenegraph.database.inventor.elements.SoDrawStyleElement;
import jscenegraph.database.inventor.elements.SoGLCacheContextElement;
import jscenegraph.database.inventor.elements.SoGLLazyElement;
import jscenegraph.database.inventor.elements.SoGLTextureEnabledElement;
import jscenegraph.database.inventor.elements.SoLazyElement;
import jscenegraph.database.inventor.elements.SoModelMatrixElement;
import jscenegraph.database.inventor.elements.SoPickStyleElement;
import jscenegraph.database.inventor.elements.SoProjectionMatrixElement;
import jscenegraph.database.inventor.elements.SoShapeHintsElement;
import jscenegraph.database.inventor.elements.SoViewingMatrixElement;
import jscenegraph.database.inventor.elements.SoViewportRegionElement;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.mevis.inventor.misc.SoVBO;
import jscenegraph.port.Offset;


////////////////////////////////////////////////////////////////////////////////
//! Abstract base class for all shape nodes.
/*!
\class SoShape
\ingroup Nodes
This node is the abstract base class for all shape (geometry) nodes.
All classes derived from SoShape draw geometry during render
traversal.

\par See Also
\par
SoCone, SoCube, SoCylinder, SoIndexedNurbsCurve, SoIndexedNurbsSurface, SoNurbsCurve, SoNurbsSurface, SoShapeHints, SoShapeKit, SoSphere, SoText2, SoText3, SoVertexShape
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public abstract class SoShape extends SoNode {

	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_ABSTRACT_HEADER(SoShape.class,this);
	   	
	public                                                                     
    static SoType       getClassTypeId()        /* Returns class type id */   
                                    { return SoSubNode.getClassTypeId(SoShape.class); }                   
    public SoType      getTypeId()       /* Returns type id      */
    {
		return nodeHeader.getClassTypeId();		    	
    }
  public                                                                  
    SoFieldData   getFieldData() {
	  return nodeHeader.getFieldData(); 
  }
  public  static SoFieldData[] getFieldDataPtr()                              
        { return SoSubNode.getFieldDataPtr(SoShape.class); }              
	
      //! This type is used by the triangle shape generation methods
    //! (beginShape, etc.)
  public  enum TriangleShape {
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        TRIANGLES,
        POLYGON
    };

    //! Helper struct used by primitive types like cube, sphere, ...
    public class SimpleVertexArrayCache {
      SimpleVertexArrayCache() {
    	  numVertices = 0;
    	  useTexCoords = false;
    	  useNormals = false;
    	  useColors = false;
    	  vertexOffset = 0;
    	  normalOffset = 0;
    	  texCoordOffset = 0;
    	  colorOffset = 0;    	  
      }

      void drawArrays(SoShape shape, SoGLRenderAction action, /*GLenum*/int primitiveType) {
  SoState state = action.getState();
  enableVertexAttributes(state);

  if (shape._preVertexArrayRenderingCB != null) {
    (shape._preVertexArrayRenderingCB).run(shape, action, true, numVertices);
  }

  SoLazyElement.drawArrays(state, primitiveType, 0, numVertices);

  if (shape._postVertexArrayRenderingCB != null) {
    (shape._postVertexArrayRenderingCB).run(shape, action, true, numVertices);
  }

  disableVertexAttributes(state);
    	  
      }

      void enableVertexAttributes(SoState state) {
  boolean useVertexAttributes = SoLazyElement.shouldUseVertexAttributes(state);
  
  GL2 gl2 = state.getGL2();

  if (useVertexAttributes) {
	  gl2.glVertexAttribPointer(SoLazyElement.VertexAttribs.ATTRIB_VERTEX.getValue(), 3, GL_FLOAT, false, 0, vertexOffset);
    gl2.glEnableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_VERTEX.getValue());
    if (useNormals) {
    	gl2.glVertexAttribPointer(SoLazyElement.VertexAttribs.ATTRIB_NORMAL.getValue(), 3, GL_FLOAT, false, 0, normalOffset);
      gl2.glEnableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_NORMAL.getValue());
    }
    if (useTexCoords) {
    	gl2.glVertexAttribPointer(SoLazyElement.VertexAttribs.ATTRIB_TEXCOORD.getValue(), 2, GL_FLOAT, false, 0, texCoordOffset);
      gl2.glEnableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_TEXCOORD.getValue());
    }
    if (useColors) {
    	gl2.glVertexAttribPointer(SoLazyElement.VertexAttribs.ATTRIB_COLOR.getValue(), 4, GL_UNSIGNED_BYTE,true, 0, colorOffset);
      gl2.glEnableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_COLOR.getValue());
    } else {
      // we need to send the single color on the correct vertex attribute...
      SbColor color = SoGLLazyElement.getDiffuse(state, 0);
      gl2.glVertexAttrib4fv(SoLazyElement.VertexAttribs.ATTRIB_COLOR.getValue(), color.getValue(),0);
    }
  } else {
	  gl2.glVertexPointer(3, GL_FLOAT, 0, vertexOffset);
    gl2.glEnableClientState(GL_VERTEX_ARRAY);
    if (useNormals) {
    	gl2.glNormalPointer(GL_FLOAT, 0, normalOffset);
      gl2.glEnableClientState(GL_NORMAL_ARRAY);
    }
    if (useTexCoords) {
    	gl2.glTexCoordPointer(2, GL_FLOAT, 0, texCoordOffset);
      gl2.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    if (useColors) {
    	gl2.glColorPointer(4, GL_UNSIGNED_BYTE, 0, colorOffset);
      gl2.glEnableClientState(GL_COLOR_ARRAY);
    }
  }
      
      }

      void disableVertexAttributes(SoState state) {
    	  
    	  GL2 gl2 = state.getGL2();
    	  
  boolean useVertexAttributes = SoLazyElement.shouldUseVertexAttributes(state);
  if (useVertexAttributes) {
    gl2.glDisableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_VERTEX.getValue());
    if (useNormals) {
    	gl2.glDisableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_NORMAL.getValue());
    }
    if (useTexCoords) {
    	gl2.glDisableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_TEXCOORD.getValue());
    }
    if (useColors) {
    	gl2.glDisableVertexAttribArray(SoLazyElement.VertexAttribs.ATTRIB_COLOR.getValue());
    }
  } else {
	  gl2.glDisableClientState(GL_VERTEX_ARRAY);
    if (useNormals) {
    	gl2.glDisableClientState(GL_NORMAL_ARRAY);
    }
    if (useTexCoords) {
    	gl2.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    if (useColors) {
    	gl2.glDisableClientState(GL_COLOR_ARRAY);
    }
  }      
      }

      final SoVBO vbo = new SoVBO(GL_ARRAY_BUFFER);

      int numVertices;

      int vertexOffset;
      int normalOffset;
      int texCoordOffset;
      int colorOffset;

      boolean useTexCoords;
      boolean useNormals;
      boolean useColors;
    };

    
  
  
    private

    //! These are used when rendering or picking a shape whose
    //! complexity is set to BOUNDING_BOX. The SoGetBoundingBoxAction
    //! is used to compute the bounding box of the shape, and the cube
    //! is used as a surrogate object when rendering it.
    static SoGetBoundingBoxAction       bboxAct;
    private static SoCube                       bboxCube;

      //! These are used when using generatePrimitives() to do rendering.
    //! They store global info that is needed to render each primitive
    //! correctly.
  private  static boolean       sendTexCoords;  //!< TRUE if coords should be sent
	private    static SoMaterialBundle matlBundle;//!< Bundle used to send materials

    //! These are used for triangle primitive generation (beginShape, etc.)
	private static TriangleShape        primShapeType;  //!< Type of shape generated
	private static SoFaceDetail faceDetail;    //!< Detail used for each primitive
	private static int          nestLevel;      //!< Level of beginShape() nesting
	private static SoAction     primAction;    //!< Action primitives generated for
	private static int          primVertNum;    //!< Number of vertices so far
	private static int          polyVertNum;    //!< Number of poly vertices so far
	private static SoShape      primShape;     //!< Shape primitives generated for

    //! These are static for speed, so we don't have to allocate them
    //! once for each polygon or set of polygons.
	private static SoPrimitiveVertex[] primVerts;        //!< Array of saved vertices
	private static SoPointDetail[]     vertDetails;      //!< Array of vertex details
	private static SoPrimitiveVertex[] polyVerts;        //!< Array of saved poly vertices
	private static SoPointDetail[]     polyDetails;      //!< Array of poly details
	private static int numPolyVertsAllocated;   //!< Size of polyVerts array
	private static GLUtessellator  tobj;   //!< Tesselator (for concave polygons)
    
	  public
	    //! callback used for pre/post vertex array rendering on SoVertexShapes (MeVis ONLY)
	    interface VertexArrayRenderingCB {
		  void run(SoNode shape, SoGLRenderAction action, boolean useVbo, int numVertices);
	  }
	  	
	protected static VertexArrayRenderingCB _preVertexArrayRenderingCB;
    protected static VertexArrayRenderingCB _postVertexArrayRenderingCB;
  
  
	protected SoShape() {
		nodeHeader.SO_NODE_CONSTRUCTOR();
	}
	
	@Override
	public Object plus(Offset offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    Overrides method in SoNode to return FALSE.
	   //
	   // Use: public
	   
	   public boolean
	   affectsState()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       return false;
	   }
	   	

	 ////////////////////////////////////////////////////////////////////////
	  //
	  // Description:
	  //    This initializes the SoShape class.
	  //
	  // Use: internal
	  
	 public static void
	  initClass()
	  //
	  ////////////////////////////////////////////////////////////////////////
	  {
	    SoSubNode.SO__NODE_INIT_ABSTRACT_CLASS(SoShape.class, "Shape", SoNode.class);
	  }
	  
    //! Computes bounding box for subclass using information in the
    //! given action (which may not necessarily be an
    //! SoGetBoundingBoxAction). This is used by getBoundingBox() and
    //! when rendering or picking a shape with bounding-box complexity.
    //! Subclasses must define this method.
    public abstract void        computeBBox(final SoAction action, final SbBox3f box,
                                    final SbVec3f center);
	 
    //! register global pre callback for vertex array rendering (MeVis ONLY)
    public static void setPreVertexArrayRenderingCallback(VertexArrayRenderingCB cb) {
    	_preVertexArrayRenderingCB = cb;
    }
    //! register global post callback for vertex array rendering (MeVis ONLY) 
    public static void setPostVertexArrayRenderingCallback(VertexArrayRenderingCB cb) {
    	_postVertexArrayRenderingCB = cb;
    }
    
	  
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Computes bounding box for a shape, using the virtual
//    computeBBox() method.
//
// Use: extender

public void
getBoundingBox(SoGetBoundingBoxAction action)
//
////////////////////////////////////////////////////////////////////////
{
    final SbBox3f     bbox = new SbBox3f();
    final SbVec3f     center = new SbVec3f();

    computeBBox(action, bbox, center);

    // 2004-09-01 Felix: Ignore bounding box and center if volume is empty
    if(bbox.isEmpty())
        return;

    action.extendBy(bbox);

    // Make sure the center is first transformed by the current local
    // transformation matrix
    action.setCenter(center, true);
}

    //! This method MUST be defined by each subclass to generate
    //! primitives (triangles, line segments, points) that represent
    //! the shape.
    protected abstract void generatePrimitives(SoAction action);


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns TRUE if the shape should be rendered now.
//
// Use: protected 

protected boolean
shouldGLRender(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // SoNode has already checked for render abort, so don't need to
    // do it now

    // Check if the shape is invisible
    if (SoDrawStyleElement.get(action.getState()) ==
        SoDrawStyleElement.Style.INVISIBLE)
        return false;

    // If the shape is transparent and transparent objects are being
    // delayed, don't render now
    if (action.handleTransparency())
        return false;

    // If the current complexity is BOUNDING_BOX, just render the
    // cuboid surrounding the shape and tell the shape to stop
    if (SoComplexityTypeElement.get(action.getState()) ==
        SoComplexityTypeElement.Type.BOUNDING_BOX) {
        GLRenderBoundingBox(action);
        return false;
    }

    // Otherwise, go ahead and render the object
    return true;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Implements rendering by rendering each primitive generated by
//    subclass.
//
// Use: extender
public void
GLRender(SoGLRenderAction action) {
	SoShape_GLRender(action);
}


public void
SoShape_GLRender(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // First see if the object is visible and should be rendered now
    if (shouldGLRender(action)) {

        SoState state = action.getState();

        //
        // Set up some info in instance that will be used during
        // rendering of generated primitives
        //

        // Send the first material and remember it was sent
        final SoMaterialBundle        mb = new SoMaterialBundle(action);
        matlBundle = mb;
        matlBundle.sendFirst();

        // See if textures are enabled and we need to send texture coordinates
        sendTexCoords = (SoGLTextureEnabledElement.get(state));

        // Generate primitives to approximate the shape. Each
        // primitive will be rendered separately (through callbacks).
        generatePrimitives(action);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Implements picking along a ray by intersecting the ray with each
//    primitive generated by subclass.
//
// Use: extender

public void
rayPick(SoRayPickAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // First see if the object is pickable
    if (shouldRayPick(action)) {

        // Compute the picking ray in the space of the shape
        computeObjectSpaceRay(action);

        // Generate primitives to approximate the shape. Each
        // primitive will be intersected (through callbacks) with the
        // ray.
        generatePrimitives(action);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Implements the generation of primitives for the shape.
//
// Use: extender

public void
callback(SoCallbackAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // First see if the object should have primitives generated for it.
    if (action.shouldGeneratePrimitives(this)) {

        // Generate primitives to approximate the shape. Each primitive
        // will be sent back to the application through callbacks.
        generatePrimitives(action);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Applies render action to the bounding box surrounding the shape.
//    This is used to render shapes when BOUNDING_BOX complexity is on.
//
// Use: protected

protected void
GLRenderBoundingBox(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // Create a surrogate cube to render, if not already done
    if (bboxCube == null) {
        bboxCube = new SoCube();
        bboxCube.ref();
    }

    // Compute the bounding box of the shape, using the virtual
    // computeBBox() method. By using this method (rather than by
    // applying an SoGetBoundingBoxAction), we can make sure that any
    // elements used to compute the bounding box are known to any
    // currently open caches in the render action. Otherwise, objects
    // (such as 2D text) that use extra elements to compute bounding
    // boxes would not be rendered correctly when cached.
    final SbBox3f     box = new SbBox3f();
    final SbVec3f     center = new SbVec3f();
    computeBBox(action, box, center);

    // Render the cube using a special method that is designed for
    // this task
    bboxCube.GLRenderBoundingBox(action, box);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns TRUE if the shape may be picked.
//
// Use: protected, static

protected boolean
shouldRayPick(SoRayPickAction action)
//
////////////////////////////////////////////////////////////////////////
{
    boolean      shapeShouldPick = false;

    switch (SoPickStyleElement.get(action.getState())) {

      case SHAPE:
        shapeShouldPick = true;
        break;

      case BOUNDING_BOX:
        // Just pick the cuboid surrounding the shape
        rayPickBoundingBox(action);
        shapeShouldPick = false;
        break;

      case UNPICKABLE:
        shapeShouldPick = false;
        break;
    }

    return shapeShouldPick;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    This is called by a subclass before rendering to set up shape
//    hints for a solid object.
//
// Use: protected

protected void
beginSolidShape(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    SoState state = action.getState();
    state.push();

    // If the current draw style is not FILLED, we don't want to turn
    // on backface culling. (It may already be on, but that's up to
    // the application to decide.)
    if (SoDrawStyleElement.get(action.getState()) !=
        SoDrawStyleElement.Style.FILLED) {
        return;
    }

    // Turn on backface culling, using shape hints element, unless it
    // is already set up ok. Save state first if changing things
    final SoShapeHintsElement.VertexOrdering[] oldOrder = new SoShapeHintsElement.VertexOrdering[1];
    final SoShapeHintsElement.ShapeType[]              oldShape = new SoShapeHintsElement.ShapeType[1];
    final SoShapeHintsElement.FaceType[]               oldFace = new SoShapeHintsElement.FaceType[1];

    SoShapeHintsElement.get(state, oldOrder, oldShape, oldFace);

    if (oldOrder[0] != SoShapeHintsElement.VertexOrdering.COUNTERCLOCKWISE ||
        oldShape[0] != SoShapeHintsElement.ShapeType.SOLID) {
        SoShapeHintsElement.set(state,
                                 SoShapeHintsElement.VertexOrdering.COUNTERCLOCKWISE,
                                 SoShapeHintsElement.ShapeType.SOLID,
                                 SoShapeHintsElement.FaceType.FACE_TYPE_AS_IS);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    This is called by a subclass after rendering, if
//    beginSolidShape() was called beforehand.
//
// Use: protected

protected void
endSolidShape(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    action.getState().pop();
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Computes a picking ray in the object space of the shape
//    instance. The picking ray is stored in the SoRayPickAction for
//    later access by the subclass.
//
// Use: protected

protected void
computeObjectSpaceRay(SoRayPickAction action)
//
////////////////////////////////////////////////////////////////////////
{
    action.setObjectSpace();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Same as above, but allows subclass to specify a matrix to
//    concatenate with the current transformation matrix.
//
// Use: protected

protected void
computeObjectSpaceRay(SoRayPickAction action, final SbMatrix matrix)
//
////////////////////////////////////////////////////////////////////////
{
    action.setObjectSpace(matrix);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invokes correct callbacks for triangle primitive generation.
//
// Use: protected

protected void
invokeTriangleCallbacks(SoAction action,
                                 final SoPrimitiveVertex v1,
                                 final SoPrimitiveVertex v2,
                                 final SoPrimitiveVertex v3)
//
////////////////////////////////////////////////////////////////////////
{
    final SoType actionType = action.getTypeId();

    // Treat rendering and picking cases specially
    if (actionType.isDerivedFrom(SoRayPickAction.getClassTypeId()))
        rayPickTriangle((SoRayPickAction ) action, v1, v2, v3);

    else if (actionType.isDerivedFrom(SoGLRenderAction.getClassTypeId()))
        GLRenderTriangle((SoGLRenderAction ) action, v1, v2, v3);

    // Otherwise, this is invoked through the callback action, so
    // invoke the triangle callbacks.
    else {
        SoCallbackAction cbAct = (SoCallbackAction ) action;
        cbAct.invokeTriangleCallbacks(this, v1, v2, v3);
    }
}



//! These methods can be used by subclasses to generate triangles
//! more easily when those triangles are part of a larger
//! structure, such as a triangle strip, triangle fan, or
//! triangulated polygon, according to the TriangleShape enumerated
//! type. The sequence of calls is similar to GL's: begin a shape,
//! send vertices of that shape, then end the shape.
//!
//! If the face detail passed to beginShape() is NULL (the
//! default), the details in the vertices will be used as is.
//! Note that some vertices may be copied into local storage; the
//! detail pointers are copied as well, so the details themselves
//! should be consistent for the duration of the shape generation.
//!
//! If the face detail passed to beginShape() is non-NULL, the
//! details in the vertices are assumed to be SoPointDetails. Each
//! vertex of each triangle generated will contain a pointer to the
//! face detail, which will be filled with three copies of the
//! point details from the relevant vertices. Since copies of the
//! point details are made, the storage for each point detail
//! passed to shapeVertex() can be re-used by the caller.

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Begins a shape composed of triangles during primitive generation.
//
// Use: protected

protected void beginShape(SoAction action, TriangleShape shapeType) {
	beginShape(action, shapeType, null);
}

protected void
beginShape(SoAction action, TriangleShape shapeType,
                    SoFaceDetail _faceDetail)
//
////////////////////////////////////////////////////////////////////////
{
    if (primVerts == null) {
        primVerts   = new SoPrimitiveVertex[2];
        primVerts[0] = new SoPrimitiveVertex();primVerts[1] = new SoPrimitiveVertex();
        vertDetails = new SoPointDetail[2];
        vertDetails[0] = new SoPointDetail();vertDetails[1] = new SoPointDetail();
    }

    primShapeType = shapeType;
    primVertNum   = 0;
    primShape     = this;
    primAction    = action;

    // Save face detail unless we are called recursively
    if (nestLevel++ == 0)
        faceDetail = _faceDetail;

    switch (shapeType) {

      case TRIANGLE_STRIP:
      case TRIANGLE_FAN:
      case TRIANGLES:
        // If the face detail is not NULL, get it ready to store the 3
        // point details for each triangle
        if (faceDetail != null)
            faceDetail.setNumPoints(3);
        break;

      case POLYGON:
        {
            final SoShapeHintsElement.VertexOrdering[] vo = new SoShapeHintsElement.VertexOrdering[1];
            final SoShapeHintsElement.ShapeType[] st = new SoShapeHintsElement.ShapeType[1];
            final SoShapeHintsElement.FaceType[] ft = new SoShapeHintsElement.FaceType[1];
            SoShapeHintsElement.get(action.getState(), vo, st, ft);

            if (ft[0] == SoShapeHintsElement.FaceType.CONVEX) {
                // Convex polygons can be drawn as triangle fans
                primShapeType = TriangleShape.TRIANGLE_FAN;
                // Do the same stuff needed for TRIANGLE_FAN:
                if (faceDetail != null)
                    faceDetail.setNumPoints(3);
            }
            else polyVertNum = 0;
        }
        break;
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Adds a vertex to a shape composed of triangles during primitive
//    generation.
//
// Use: protected

protected void
shapeVertex(final SoPrimitiveVertex v)
//
////////////////////////////////////////////////////////////////////////
{
    switch (primShapeType) {

      case TRIANGLE_STRIP:
        triangleVertex(v, primVertNum & 1);
        break;

      case TRIANGLE_FAN:
        triangleVertex(v, primVertNum == 0 ? 0 : 1);
        break;

      case TRIANGLES:
        triangleVertex(v, primVertNum == 2 ? -1 : primVertNum);
        // Reset for next triangle if processed 3 vertices
        if (primVertNum == 3)
            primVertNum = 0;
        break;

      case POLYGON:
        // Make sure there is enough room in polyVerts array
        allocateVerts();
        polyVerts[polyVertNum].copyFrom(v);

        if (faceDetail != null) {

            // Save point detail for given vertex in array
            polyDetails[polyVertNum].copyFrom(
                 ( SoPointDetail ) v.getDetail());

            // Store pointer to point detail in saved polygon vertex
            polyVerts[polyVertNum].setDetail(polyDetails[polyVertNum]);
        }

        ++polyVertNum;
        break;
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Implements shapeVertex() for one of the three triangle-based types.
//
// Use: private

private void
triangleVertex( SoPrimitiveVertex v, int vertToReplace)
//
////////////////////////////////////////////////////////////////////////
{
    if (faceDetail == null) {
        // Generate a triangle if we have 3 vertices
        if (primVertNum >= 2)
            invokeTriangleCallbacks(primAction,
                                    primVerts[0], primVerts[1], v);
        
        // Save vertex in one of the two slots
        if (vertToReplace >= 0)
            primVerts[vertToReplace].copyFrom(v);
    }
    
    // If face detail was supplied, set it to contain the 3 point
    // details. Make sure the primitive vertices all point to the
    // face detail.
    else {
        final SoPointDetail     pd = ( SoPointDetail ) v.getDetail();
        final SoPrimitiveVertex       pv = new SoPrimitiveVertex(v);
        
        pv.setDetail(faceDetail);
        
        // Generate a triangle if we have 3 vertices
        if (primVertNum >= 2) {
            faceDetail.setPoint(0, vertDetails[0]);
            faceDetail.setPoint(1, vertDetails[1]);
            faceDetail.setPoint(2, pd);
            invokeTriangleCallbacks(primAction,
                                    primVerts[0], primVerts[1], pv);
        }
        
        // Save vertex and details in one of the two slots
        if (vertToReplace >= 0) {
            primVerts[vertToReplace] = pv;
            vertDetails[vertToReplace].copyFrom(pd);
        }
    }

    primVertNum++;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    For polygons, re-allocates the polyVerts array if more space is
//    needed to hold all vertices.  We have to hold all of the
//    vertices in memory because the tesellator can't decompose
//    concave polygons until it has seen all of the vertices.
//
// Use: private

private void
allocateVerts()
//
////////////////////////////////////////////////////////////////////////
{
    // 8 vertices are allocated to begin with
    if (polyVerts == null) {
        polyVerts   = new SoPrimitiveVertex[8];
        polyDetails = new SoPointDetail[8];
        numPolyVertsAllocated = 8;
    }

    else {
        if (polyVertNum >= numPolyVertsAllocated) {
            final SoPrimitiveVertex[] oldVerts   = polyVerts;
            final SoPointDetail[]     oldDetails = polyDetails;

            // Double storage
            numPolyVertsAllocated = polyVertNum*2;
            polyVerts   = new SoPrimitiveVertex[numPolyVertsAllocated];
            polyDetails = new SoPointDetail[numPolyVertsAllocated];

            // Copy over old vertices and details
            for (int i = 0; i < polyVertNum; i++) {
                polyVerts[i]   = oldVerts[i];
                polyDetails[i] = oldDetails[i];
                polyVerts[i].setDetail(polyDetails[i]);
            }

            // Delete old storage
            //delete [] oldVerts; java port
            //delete [] oldDetails; java port
        }
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Ends a shape composed of triangles during primitive generation.
//
// Use: protected

protected void
endShape()
//
////////////////////////////////////////////////////////////////////////
{
    int i;
    
    // java port
    GL2 gl2 = SoGLCacheContextElement.get(primAction.getState());
    final GLU glu = new GLUgl2();

    switch (primShapeType) {
      case TRIANGLE_STRIP:
      case TRIANGLE_FAN:
      case TRIANGLES:
        primVertNum = 0;
        break;

      case POLYGON:
        // Don't bother with degenerate polygons
        if (polyVertNum < 3) {
            polyVertNum = 0;
            break;
        }

        // Concave polygons need to be tesselated; we'll use the
        // GLU routines to do this:
        if (tobj == null) {
            tobj = glu.gluNewTess();
            
            GLUtessellatorCallback beginGLUCB = new GLUtessellatorCallbackAdapter() {
            	public void begin(int type) {
            		SoShape.this.beginCB(type);
            	}
            };
            
            glu.gluTessCallback(tobj, (int)GLU.GLU_BEGIN,
                            beginGLUCB);
            
            GLUtessellatorCallback endGLUCB = new GLUtessellatorCallbackAdapter() {
            	public void end() {
            		SoShape.this.endCB();
            	}
            };
                                    
            glu.gluTessCallback(tobj, (int)GLU.GLU_END, 
                            endGLUCB);
            
            GLUtessellatorCallback vtxGLUCB = new GLUtessellatorCallbackAdapter() {
            	public void vertex(Object vertexData) {
            		SoShape.this.vtxCB(vertexData);
            	}
            };            
            
            glu.gluTessCallback(tobj, (int)GLU.GLU_VERTEX, 
                            vtxGLUCB);
            
            GLUtessellatorCallback errorGLUCB = new GLUtessellatorCallbackAdapter() {
            	public void error(int errnum) {
            		SoShape.this.errorCB(errnum,glu);
            	}
            };                        
            
            glu.gluTessCallback(tobj, (int)GLU.GLU_ERROR,
                            errorGLUCB);
        }
//#ifdef GLU_VERSION_1_2
        glu.gluTessBeginPolygon(tobj, null);
        glu.gluTessBeginContour(tobj);
//#else
//        glu.gluBeginPolygon(tobj);
//#endif

        for (i = 0; i < polyVertNum; i++) {
            final SbVec3f t = polyVerts[i].getPoint();

            double[] dv = new double[3];  // glu requires double...
            dv[0] = t.getValue()[0]; dv[1] = t.getValue()[1]; dv[2] = t.getValue()[2];
            glu.gluTessVertex(tobj, dv, 0,(Object)polyVerts[i]);
        }
//#ifdef GLU_VERSION_1_2
        glu.gluTessEndContour(tobj);
        glu.gluTessEndPolygon(tobj);
//#else
//        glu.gluEndPolygon(tobj);
//#endif

        polyVertNum = 0;
        break;
    }

    nestLevel--;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    This can be used by subclasses when the complexity type is
//    SCREEN_SPACE to determine how many window pixels are covered by
//    the shape. It returns in rectSize the number of pixels in the
//    window rectangle that covers the given 3D bounding box.
//
// Use: extender, static

public static void
getScreenSize(SoState state, final SbBox3f boundingBox,
                       final SbVec2s rectSize)
//
////////////////////////////////////////////////////////////////////////
{
    final SbMatrix    objToScreen = new SbMatrix();
    final SbVec2s     winSize = new SbVec2s();
    final SbVec3f     min = new SbVec3f(), max = new SbVec3f();
    final SbVec3f[] screenPoint = new SbVec3f[8];
    for(int i=0; i<8; i++) screenPoint[i] = new SbVec3f();
    final SbBox2f     screenBox = new SbBox2f();
    int         i;

    // Get the matrices from the state to convert from object to screen space
    objToScreen.copyFrom((SoModelMatrixElement.get(state).operator_mul(
                   SoViewingMatrixElement.get(state).operator_mul(
                   SoProjectionMatrixElement.get(state)))));

    // Get the size of the window from the state
    winSize.copyFrom(SoViewportRegionElement.get(state).getWindowSize());

    // Transform the 8 vertices of the bounding box into screen space

    boundingBox.getBounds(min, max);

    objToScreen.multVecMatrix(new SbVec3f(min.getValue()[0], min.getValue()[1], min.getValue()[2]), screenPoint[0]);
    objToScreen.multVecMatrix(new SbVec3f(min.getValue()[0], min.getValue()[1], max.getValue()[2]), screenPoint[1]);
    objToScreen.multVecMatrix(new SbVec3f(min.getValue()[0], max.getValue()[1], min.getValue()[2]), screenPoint[2]);
    objToScreen.multVecMatrix(new SbVec3f(min.getValue()[0], max.getValue()[1], max.getValue()[2]), screenPoint[3]);
    objToScreen.multVecMatrix(new SbVec3f(max.getValue()[0], min.getValue()[1], min.getValue()[2]), screenPoint[4]);
    objToScreen.multVecMatrix(new SbVec3f(max.getValue()[0], min.getValue()[1], max.getValue()[2]), screenPoint[5]);
    objToScreen.multVecMatrix(new SbVec3f(max.getValue()[0], max.getValue()[1], min.getValue()[2]), screenPoint[6]);
    objToScreen.multVecMatrix(new SbVec3f(max.getValue()[0], max.getValue()[1], max.getValue()[2]), screenPoint[7]);

    for (i = 0; i < 8; i++)
        screenBox.extendBy(new SbVec2f((screenPoint[i].getValue()[0] * winSize.getValue()[0]),
                                   (screenPoint[i].getValue()[1] * winSize.getValue()[1])));

    // Get the size of the resulting box
    final SbVec2f boxSize = new SbVec2f();
    screenBox.getSize(boxSize.getValue());

    // Screen space size is actually half of this size. Test for
    // overflow and use the maximum size if necessary.
    boxSize.operator_div_equal( 2.0f);

    if (boxSize.getValue()[0] > Short.MAX_VALUE)
        rectSize.getValue()[0] = Short.MAX_VALUE;
    else if (boxSize.getValue()[0] < Short.MIN_VALUE)
        rectSize.getValue()[0] = Short.MIN_VALUE;
    else
        rectSize.getValue()[0] = (short) boxSize.getValue()[0];

    if (boxSize.getValue()[1] > Short.MAX_VALUE)
        rectSize.getValue()[1] = Short.MAX_VALUE;
    else if (boxSize.getValue()[1] < Short.MIN_VALUE)
        rectSize.getValue()[1] = Short.MIN_VALUE;
    else
        rectSize.getValue()[1] = (short) boxSize.getValue()[1];
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invokes correct callbacks for line segment primitive generation.
//
// Use: protected

protected void
invokeLineSegmentCallbacks(SoAction action,
                                    final SoPrimitiveVertex v1,
                                    final SoPrimitiveVertex v2)
//
////////////////////////////////////////////////////////////////////////
{
    final SoType actionType = action.getTypeId();

    // Treat rendering and picking cases specially
    if (actionType.isDerivedFrom(SoRayPickAction.getClassTypeId()))
        rayPickLineSegment((SoRayPickAction ) action, v1, v2);

    else if (actionType.isDerivedFrom(SoGLRenderAction.getClassTypeId()))
        GLRenderLineSegment((SoGLRenderAction ) action, v1, v2);

    // Otherwise, this is invoked through the callback action, so
    // invoke the triangle callbacks.
    else {
        SoCallbackAction cbAct = (SoCallbackAction ) action;
        cbAct.invokeLineSegmentCallbacks(this, v1, v2);
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Picks a triangle primitive generated by a subclass.
//
// Use: private

private void
rayPickTriangle(SoRayPickAction action,
                         final SoPrimitiveVertex v1,
                         final SoPrimitiveVertex v2,
                         final SoPrimitiveVertex v3)
//
////////////////////////////////////////////////////////////////////////
{
    final SbVec3f             point = new SbVec3f();
    final SbVec3f             barycentric = new SbVec3f();
    final boolean[]              onFrontSide = new boolean[1];
    SoPickedPoint       pp;

    if (action.intersect(v1.getPoint(), v2.getPoint(), v3.getPoint(),
                          point, barycentric, onFrontSide) &&
        (pp = action.addIntersection(point)) != null) {

        final SbVec3f         norm = new SbVec3f();
        final SbVec4f         texCoord = new SbVec4f();
        SoDetail        detail;

        // Compute normal by interpolating vertex normals using
        // barycentric coordinates
        norm.copyFrom((v1.getNormal().operator_mul( barycentric.getValue()[0]).operator_add(
                v2.getNormal().operator_mul( barycentric.getValue()[1]).operator_add(
                v3.getNormal().operator_mul( barycentric.getValue()[2])))));
        norm.normalize();
        pp.setObjectNormal(norm);

        // Compute texture coordinates the same way
        texCoord.copyFrom((v1.getTextureCoords().operator_mul( barycentric.getValue()[0]).operator_add(
                    v2.getTextureCoords().operator_mul( barycentric.getValue()[1]).operator_add(
                    v3.getTextureCoords().operator_mul( barycentric.getValue()[2])))));
        pp.setObjectTextureCoords(texCoord);

        // Copy material index from closest detail, since it can't
        // be interpolated
        if (barycentric.getValue()[0] < barycentric.getValue()[1] && barycentric.getValue()[0] < barycentric.getValue()[2])
            pp.setMaterialIndex(v1.getMaterialIndex());
        else if (barycentric.getValue()[1] < barycentric.getValue()[2])
            pp.setMaterialIndex(v2.getMaterialIndex());
        else
            pp.setMaterialIndex(v3.getMaterialIndex());

        // Create a detail for the specific shape
        detail = createTriangleDetail(action, v1, v2, v3, pp);
        if (detail != null)
            pp.setDetail(detail, this);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    This is called during picking to create a detail containing
//    extra info about a pick intersection on a triangle. The default
//    method returns NULL.
//
// Use: protected, virtual

protected SoDetail 
createTriangleDetail(final SoRayPickAction action,
                              final SoPrimitiveVertex v1,
                              final SoPrimitiveVertex v2,
                              final SoPrimitiveVertex v3,
                              final SoPickedPoint pp)
//
////////////////////////////////////////////////////////////////////////
{
    return null;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    This is called during picking to create a detail containing
//    extra info about a pick intersection on a line segment. The
//    default method returns NULL.
//
// Use: protected, virtual

protected SoDetail 
createLineSegmentDetail(SoRayPickAction action,
                                 SoPrimitiveVertex v1,
                                 SoPrimitiveVertex v2,
                                 SoPickedPoint pp)
//
////////////////////////////////////////////////////////////////////////
{
    return null;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    This is called during picking to create a detail containing
//    extra info about a pick intersection on a point. The default
//    method returns NULL.
//
// Use: protected, virtual

protected SoDetail 
createPointDetail(SoRayPickAction action, SoPrimitiveVertex v,
                           SoPickedPoint pp)
//
////////////////////////////////////////////////////////////////////////
{
    return null;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Picks a line segment primitive generated by a subclass.
//
// Use: private

private void
rayPickLineSegment(SoRayPickAction action,
                            final SoPrimitiveVertex v1,
                            final SoPrimitiveVertex v2)
//
////////////////////////////////////////////////////////////////////////
{
    final SbVec3f             point = new SbVec3f();
    SoPickedPoint       pp;

    if (action.intersect(v1.getPoint(), v2.getPoint(), point) &&
        (pp = action.addIntersection(point)) != null) {

        float           ratioFromV1;
        final SbVec3f         norm = new SbVec3f();
        final SbVec4f         texCoord = new SbVec4f();
        SoDetail        detail;

        // Compute normal by interpolating vertex normals
        ratioFromV1 = ((point.operator_minus(v1.getPoint())).length() /
                       (v2.getPoint().operator_minus( v1.getPoint())).length());
        norm.copyFrom( (v1.getNormal().operator_mul((1.0f - ratioFromV1)).operator_add(
                v2.getNormal().operator_mul(ratioFromV1))));
        norm.normalize();
        pp.setObjectNormal(norm);

        // Compute texture coordinates the same way
        texCoord.copyFrom( (v1.getTextureCoords().operator_mul (1.0f - ratioFromV1)).operator_add(
                    v2.getTextureCoords().operator_mul( ratioFromV1)));
        pp.setObjectTextureCoords(texCoord);

        // Copy material index from closer detail, since it can't be
        // interpolated
        if (ratioFromV1 < 0.5)
            pp.setMaterialIndex(v1.getMaterialIndex());
        else
            pp.setMaterialIndex(v2.getMaterialIndex());

        // Create a detail for the specific shape
        detail = createLineSegmentDetail(action, v1, v2, pp);
        if (detail != null)
            pp.setDetail(detail, this);
    }
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invokes correct callbacks for point primitive generation.
//
// Use: protected

protected void
invokePointCallbacks(SoAction action, final SoPrimitiveVertex v)
//
////////////////////////////////////////////////////////////////////////
{
    SoType actionType = action.getTypeId();

    // Treat rendering and picking cases specially
    if (actionType.isDerivedFrom(SoRayPickAction.getClassTypeId()))
        rayPickPoint((SoRayPickAction ) action, v);

    else if (actionType.isDerivedFrom(SoGLRenderAction.getClassTypeId()))
        GLRenderPoint((SoGLRenderAction ) action, v);

    // Otherwise, this is invoked through the callback action, so
    // invoke the triangle callbacks.
    else {
        SoCallbackAction cbAct = (SoCallbackAction ) action;
        cbAct.invokePointCallbacks(this, v);
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Picks a point primitive generated by a subclass.
//
// Use: private

private void
rayPickPoint(SoRayPickAction action, final SoPrimitiveVertex v)
//
////////////////////////////////////////////////////////////////////////
{
    SoPickedPoint       pp;

    if (action.intersect(v.getPoint()) &&
        (pp = action.addIntersection(v.getPoint())) != null) {

        SoDetail        detail;

        pp.setObjectNormal(v.getNormal());
        pp.setObjectTextureCoords(v.getTextureCoords());
        pp.setMaterialIndex(v.getMaterialIndex());

        // Create a detail for the specific shape
        detail = createPointDetail(action, v, pp);
        if (detail != null)
            pp.setDetail(detail, this);
    }
}

	  
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Applies rayPick action to the bounding box surrounding the
//    shape. This is used to pick shapes when BOUNDING_BOX complexity
//    is on.
//
// Use: private

private void
rayPickBoundingBox(SoRayPickAction action)
//
////////////////////////////////////////////////////////////////////////
{
    // Create a surrogate cube to pick if not already done
    if (bboxCube == null) {
        bboxCube = new SoCube();
        bboxCube.ref();
    }

    // Compute the bounding box of the shape, using the virtual
    // computeBBox() method
    final SbBox3f     box = new SbBox3f();
    final SbVec3f     center = new SbVec3f();
    computeBBox(action, box, center);

    // Pick the cube using a special method that is designed for
    // this task
    bboxCube.rayPickBoundingBox(action, box);
}
	  

//
// This macro is used by the rendering methods to follow:
//

private void RENDER_VERTEX(SoPrimitiveVertex pv, GL2 gl2)    {                                                 
    if (sendTexCoords)                                                        
        gl2.glTexCoord4fv(pv.getTextureCoords().getValue(),0);                     
    matlBundle.send(pv.getMaterialIndex(), true);                           
    if (! matlBundle.isColorOnly())                                          
        gl2.glNormal3fv(pv.getNormal().getValue(),0);                              
    gl2.glVertex3fv(pv.getPoint().getValue(),0);
}
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Renders a triangle primitive generated by a subclass.
//
// Use: private

private void
GLRenderTriangle(SoGLRenderAction action,
                          final SoPrimitiveVertex v1,
                          final SoPrimitiveVertex v2,
                          final SoPrimitiveVertex v3)
//
////////////////////////////////////////////////////////////////////////
{
	GL2 gl2 = SoGLCacheContextElement.get(action.getState());
	
    gl2.glBegin(GL2.GL_TRIANGLES);

    RENDER_VERTEX(v1,gl2);
    RENDER_VERTEX(v2,gl2);
    RENDER_VERTEX(v3,gl2);

    gl2.glEnd();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Renders a line segment primitive generated by a subclass.
//
// Use: private

private void
GLRenderLineSegment(SoGLRenderAction action,
                             final SoPrimitiveVertex v1,
                             final SoPrimitiveVertex v2)
//
////////////////////////////////////////////////////////////////////////
{
	GL2 gl2 = SoGLCacheContextElement.get(action.getState());
	
    gl2.glBegin(GL2.GL_LINES);

    RENDER_VERTEX(v1,gl2);
    RENDER_VERTEX(v2,gl2);

    gl2.glEnd();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Renders a point primitive generated by a subclass.
//
// Use: private

private void
GLRenderPoint(SoGLRenderAction action, final SoPrimitiveVertex v)
//
////////////////////////////////////////////////////////////////////////
{
	GL2 gl2 = SoGLCacheContextElement.get(action.getState());
	
    gl2.glBegin(GL2.GL_POINTS);

    RENDER_VERTEX(v,gl2);

    gl2.glEnd();
}

//#undef RENDER_VERTEX


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Called by the GLU tesselator when we are beginning a triangle
//    strip, fan, or set of independent triangles.
//
// Use: static, private

private void
beginCB(int primType)
//
////////////////////////////////////////////////////////////////////////
{
    switch(primType) {
      case GL2.GL_TRIANGLE_STRIP:
        primShape.beginShape(primShape.primAction, TriangleShape.TRIANGLE_STRIP);
        break;
      case GL2.GL_TRIANGLE_FAN:
        primShape.beginShape(primShape.primAction, TriangleShape.TRIANGLE_FAN);
        break;
      case GL2.GL_TRIANGLES:
        primShape.beginShape(primShape.primAction, TriangleShape.TRIANGLES);
        break;
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Called by the GLU tesselator when we are generating primitives.
//
// Use: static, private

private void
vtxCB(Object data)
//
////////////////////////////////////////////////////////////////////////
{
    SoPrimitiveVertex v = ( SoPrimitiveVertex )data;

    primShape.shapeVertex(v);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Called by the GLU tesselator when we are done with the
//    strip/fan/etc.
//
// Use: static, private

private void
endCB()
//
////////////////////////////////////////////////////////////////////////
{
    primShape.endShape();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Called by the GLU tesselator if there is an error (typically
//    because the polygons self-intersects).
//
// Use: static, private

private static void
errorCB(int err, GLU glu)
//
////////////////////////////////////////////////////////////////////////
{
    SoDebugError.post("SoShape.errorCB",
                       "GLU error: "+ glu.gluErrorString(err));
}

	  
	 }
