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
 |      Defines the SoCallbackAction class
 |
 |   Author(s)          : Dave Immel, Thad Beier
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.actions;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SbPList;
import jscenegraph.database.inventor.SoFullPath;
import jscenegraph.database.inventor.SoPath;
import jscenegraph.database.inventor.SoPrimitiveVertex;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoActionMethodList.SoActionMethod;
import jscenegraph.database.inventor.elements.SoElement;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.database.inventor.nodes.SoShape;
import jscenegraph.port.Destroyable;


////////////////////////////////////////////////////////////////////////////////
//! Performs a generic traversal of the scene graph.
/*!
\class SoCallbackAction
\ingroup Actions
This action defines a generic traversal of the scene graph. The user
can specify callback functions for node types or paths; when those
node types or paths are encountered during traversal, the user's
callback function is called.


In addition, callback functions can be registered for primitives
generated by shapes in the scene graph. Most shape types can generate
primitives that represent or approximate their geometries.
<em>Triangle</em> primitives are used for all surfaces (such as cubes, face
sets, or 3D text), <em>line segment</em> primitives are used for line
shapes, and <em>point</em> primitives are used for point shapes. Note that
the type of primitives generated for a shape is the same, regardless
of drawing style or other properties.


Most of the methods on this class access information
from the traversal state. They should be called only by callback
functions that are invoked during traversal, so there is a valid state
to work with.

\par See Also
\par
SoCallback, SoEventCallback, SoShape
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoCallbackAction extends SoAction implements Destroyable {
	
	//!
//! Typedefs for callback routines used with the callbacks for
//! generating primitives.
//!

public interface SoTriangleCB  { void run(Object userData,
                          SoCallbackAction action,
                          final SoPrimitiveVertex v1,
                          final SoPrimitiveVertex v2,
                          final SoPrimitiveVertex v3);
}
public interface SoLineSegmentCB { void run(Object userData, SoCallbackAction action,
                             final SoPrimitiveVertex v1,
                             final SoPrimitiveVertex v2);
}
public interface SoPointCB { void run(Object userData, SoCallbackAction action,
                       final SoPrimitiveVertex v);
}

	
	
	 class nodeTypeCallback implements Destroyable {
		        final SoType                                      type = new SoType();
		        SoCallbackAction.SoCallbackActionCB        cb;
		        Object                                        data;
				@Override
				public void destructor() {
					cb = null;
					data = null;
				}
		    };
		    
		    class tailCallback {
		        SoCallbackAction.SoCallbackActionCB        cb;
		        Object                                        data;
		   };
		    	

class triangleCallback implements Destroyable {
    final SoType                                      type = new SoType();
    SoTriangleCB                                cb;
    Object                                        data;
	@Override
	public void destructor() {
		cb = null;
		data = null;
	}
};

class lineSegmentCallback implements Destroyable {
    final SoType                                      type = new SoType();
    SoLineSegmentCB                             cb;
    Object                                        data;
	@Override
	public void destructor() {
		cb = null;
		data = null;
	}
};

class pointCallback implements Destroyable {
    final SoType                                      type = new SoType();
    SoPointCB                                   cb;
    Object                                        data;
	@Override
	public void destructor() {
		cb = null;
		data = null;
	}
};

	
    //! Possible responses from a pre or post callback
      public enum Response {
          CONTINUE,               //!< Continue as usual
          ABORT,                  //!< Stop traversing the rest of the graph
          PRUNE                   //!< Do not traverse children of this node
      };
  
      //! The SoCallbackActionCB typedef is defined within the class, since
           //! it needs to refer to the Response enumerated type.
           //! The first argument is the data pointer that the user supplied
           //! when the callback was registered. The second argument is the
           //! action, from which the state can be extracted. The third
           //! argument is the node that the callback is called from.  
      interface SoCallbackActionCB {
    	  
    	  Response run(Object userData,
                                               SoCallbackAction action,
                                               final SoNode node);
      };
            
      private
    	       //! Callback lists
    	     final  SbPList             preCallbackList = new SbPList();
      private	  final     SbPList             postCallbackList = new SbPList();
    	   
      private 	  final     SbPList             preTailCallbackList = new SbPList();
      private 	  final     SbPList             postTailCallbackList = new SbPList();
    	   
      private  	  final     SbPList             triangleCallbackList = new SbPList();
      private  	  final     SbPList             lineSegmentCallbackList = new SbPList();
      private  	  final     SbPList             pointCallbackList = new SbPList();
    	   
    	       //! Response from last callback
	private Response            response;
  
      //! Node where the callback occurs:
	private SoNode             currentNode;
 	
	

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Default constructor.
//
// Use: public

public SoCallbackAction()
//
////////////////////////////////////////////////////////////////////////
{
    //SO_ACTION_CONSTRUCTOR(SoCallbackAction.class);
    traversalMethods = methods;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Destructor.
//
// Use: public

public void destructor()
//
////////////////////////////////////////////////////////////////////////
{
    int i;

    // Free up the structures in the callback lists:

    for (i = 0; i < preCallbackList.getLength(); i++)
        ((nodeTypeCallback ) preCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < postCallbackList.getLength(); i++)
        ((nodeTypeCallback ) postCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < preTailCallbackList.getLength(); i++)
        ((nodeTypeCallback ) preTailCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < postTailCallbackList.getLength(); i++)
        ((nodeTypeCallback ) postTailCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < triangleCallbackList.getLength(); i++)
        ((triangleCallback ) triangleCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < lineSegmentCallbackList.getLength(); i++)
        ((lineSegmentCallback ) lineSegmentCallbackList.operator_square_bracket(i)).destructor();

    for (i = 0; i < pointCallbackList.getLength(); i++)
        ((pointCallback ) pointCallbackList.operator_square_bracket(i)).destructor();
}

	
	
	public                                                                     
     SoType              getTypeId() {
		return classTypeId; 
	}
    public static SoType               getClassTypeId()                              
                                    { return classTypeId; }                   
  public                                                          
    static void                 addMethod(SoType t, SoActionMethod method)    
                                    { methods.addMethod(t, method); }
  // java port
  public  static void                 enableElement(Class<?> klass)         
  { enabledElements.enable(SoElement.getClassTypeId(klass), SoElement.getClassStackIndex(klass));}
  
   public  static void                 enableElement(SoType t, int stkIndex)         
                                    { enabledElements.enable(t, stkIndex);}  
  protected                                                                  
    SoEnabledElementsList  getEnabledElements()  {
	  return enabledElements;
  }
    protected static SoEnabledElementsList enabledElements;                            
    protected static SoActionMethodList   methods;                                     
  private                                                                
    static SoType               classTypeId;

	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    Initializes the SoCallbackAction class.
	   //
	   // Use: internal
	   
	   public static void
	   initClass()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       //SO_ACTION_INIT_CLASS(SoCallbackAction, SoAction);
	       enabledElements = new SoEnabledElementsList(SoAction.enabledElements);
	       methods = new SoActionMethodList(SoAction.methods);          
	       classTypeId    = SoType.createType(SoAction.getClassTypeId(),        
	                                           new SbName("SoCallbackAction"), null);
	   }
	   

	     //! Set the current node during traversal:
	   public     void                setCurrentNode(SoNode node)
	            { currentNode = node;}
	   
	    ////////////////////////////////////////////////////////////////////////
	    //
	    // Description:
	    //    Invoke pre callbacks before a node is traversed.
	    //
	    // Use: internal
	    
	    public void
	    invokePreCallbacks(final SoNode node)
	    //
	    ////////////////////////////////////////////////////////////////////////
	    {
	        Response            newResponse;
	        nodeTypeCallback    typeCb;
	        tailCallback        tailCb;
	        int                 i;
	    
	        // If we had been pruning, stop. (We know that if this node is
	        // traversed, it wasn't pruned.)
	        if (response == Response.PRUNE)
	            response = Response.CONTINUE;
	    
	        for (i = 0; i < preCallbackList.getLength(); i++) {
	            typeCb = (nodeTypeCallback )preCallbackList.operator_square_bracket(i);
	            if (node.isOfType(typeCb.type)) {
	                newResponse = typeCb.cb.run(typeCb.data, this, node);
	                if (newResponse != Response.CONTINUE) {
	                    response = newResponse;
	                    if (newResponse == Response.ABORT) {
	                        setTerminated(true);
	                        return;
	                    }
	                }
	            }
	        }
	    
	        final SoPath pathAppliedTo = getPathAppliedTo();
	    
	        if (preTailCallbackList.getLength() > 0 &&
	            pathAppliedTo != null &&
	            getCurPath().operator_equals(pathAppliedTo)) {
	    
	            for (i = 0; i < preTailCallbackList.getLength(); i++) {
	                tailCb = (tailCallback )preTailCallbackList.operator_square_bracket(i);
	                newResponse = tailCb.cb.run(tailCb.data, this, node);
	                if (newResponse != Response.CONTINUE) {
	                    response = newResponse;
	                    if (newResponse == Response.ABORT) {
	                        setTerminated(true);
	                        return;
	                    }
	                }
	            }
	        }
	    }
	    
	    ////////////////////////////////////////////////////////////////////////
	    //
	    // Description:
	    //    Invoke post callbacks.
	    //
	    // Use: internal
	    
	    public void
	    invokePostCallbacks(final SoNode node)
	    //
	    ////////////////////////////////////////////////////////////////////////
	    {
	        Response            newResponse;
	        nodeTypeCallback    typeCb;
	        tailCallback        tailCb;
	        int                 i;
	    
	        if (response == Response.PRUNE)
	            response = Response.CONTINUE;
	    
	        for (i = 0; i < postCallbackList.getLength(); i++) {
	            typeCb = (nodeTypeCallback )postCallbackList.operator_square_bracket(i);
	            if (node.isOfType(typeCb.type)) {
	                newResponse = typeCb.cb.run(typeCb.data, this, node);
	                if (newResponse != Response.CONTINUE)
	                    response = newResponse;
	                if (newResponse == Response.ABORT) {
	                    setTerminated(true);
	                    return;
	                }
	            }
	        }
	    
	        final SoPath pathAppliedTo = getPathAppliedTo();
	    
	        if (postTailCallbackList.getLength() > 0 &&
	            pathAppliedTo != null &&
	            getCurPath().operator_equals(pathAppliedTo)) {
	    
	            for (i = 0; i < postTailCallbackList.getLength(); i++) {
	                tailCb = (tailCallback )postTailCallbackList.operator_square_bracket(i);
	                newResponse = tailCb.cb.run(tailCb.data, this, node);
	                if (newResponse != Response.CONTINUE)
	                    response = newResponse;
	                if (newResponse == Response.ABORT) {
	                    setTerminated(true);
	                    return;
	                }
	            }
	        }
	    }
	     //! Returns the current response
	    public     Response            getCurrentResponse() { return response; }
	    	    
	    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns TRUE if the given node should have primitives generated
//    for it.  SoShape takes care of checking this flag and calling
//    (or not calling) the appropriate callbacks.
//
// Use: internal

public boolean
shouldGeneratePrimitives(final SoShape shape)
//
////////////////////////////////////////////////////////////////////////
{
    triangleCallback    triCb;
    lineSegmentCallback lsCb;
    pointCallback       pntCb;
    int                 i;

    // Look at each callback list to see if there is an occurrence of
    // this shape type.  Return TRUE if the type is found.
    for (i = 0; i < triangleCallbackList.getLength(); i++) {
        triCb = (triangleCallback )triangleCallbackList.operator_square_bracket(i);
        if (shape.isOfType(triCb.type))
            return true; 
    }
    for (i = 0; i < lineSegmentCallbackList.getLength(); i++) {
        lsCb = (lineSegmentCallback )lineSegmentCallbackList.operator_square_bracket(i);
        if (shape.isOfType(lsCb.type))
            return true;
    }
    for (i = 0; i < pointCallbackList.getLength(); i++) {
        pntCb = (pointCallback )pointCallbackList.operator_square_bracket(i);
        if (shape.isOfType(pntCb.type))
            return true;
    }

    return false;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invoke triangle callbacks.
//
// Use: internal

public void
invokeTriangleCallbacks(final SoShape shape,
                                          final SoPrimitiveVertex v1,
                                          final SoPrimitiveVertex v2,
                                          final SoPrimitiveVertex v3)
//
////////////////////////////////////////////////////////////////////////
{
    triangleCallback    triCb;
    int                 i;

    for (i = 0; i < triangleCallbackList.getLength(); i++) {
        triCb = (triangleCallback )triangleCallbackList.operator_square_bracket(i);
        if (shape.isOfType(triCb.type))
            (triCb.cb).run(triCb.data, this, v1, v2, v3);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invoke line segment callbacks.
//
// Use: internal

public void
invokeLineSegmentCallbacks(final SoShape shape,
                                             final SoPrimitiveVertex v1,
                                             final SoPrimitiveVertex v2)
//
////////////////////////////////////////////////////////////////////////
{
    lineSegmentCallback lsCb;
    int                 i;

    for (i = 0; i < lineSegmentCallbackList.getLength(); i++) {
        lsCb = (lineSegmentCallback )lineSegmentCallbackList.operator_square_bracket(i);
        if (shape.isOfType(lsCb.type))
            (lsCb.cb).run(lsCb.data, this, v1, v2);
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Invoke point callbacks.
//
// Use: internal

public void
invokePointCallbacks(final SoShape shape,
                                       final SoPrimitiveVertex v)
//
////////////////////////////////////////////////////////////////////////
{
    pointCallback       pntCb;
    int                 i;

    for (i = 0; i < pointCallbackList.getLength(); i++) {
        pntCb = (pointCallback )pointCallbackList.operator_square_bracket(i);
        if (shape.isOfType(pntCb.type))
            (pntCb.cb).run(pntCb.data, this, v);
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initiates action on a graph.
//
// Use: protected

protected void
beginTraversal(SoNode node)
//
////////////////////////////////////////////////////////////////////////
{
    response = Response.CONTINUE;

    traverse(node);
}

/////////////////////////////////////////////////////////////////////////
//
// Description:
//     Provides current node being traversed
//     Relies on SoNode::callbackS setting the node, rather than
//     the path being accumulated during traversal.
//
// Use: public, virtual

public SoNode getCurPathTail()
{
//#ifdef DEBUG
    if ( currentNode != SoFullPath.cast(getCurPath()).getTail()){
        SoDebugError.post("SoCallbackAction::getCurPathTail\n", 
        "Path tail inconsistent.  Did you change the scene graph\n"+
        "during a callback action?\n");
    }
//#endif /*DEBUG*/
    return(currentNode);
}

	    
}
