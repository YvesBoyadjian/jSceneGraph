/**
 * 
 */
package jscenegraph.interaction.inventor.nodes;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.misc.SoCallbackList;
import jscenegraph.database.inventor.misc.SoCallbackListCB;
import jscenegraph.database.inventor.nodes.SoSeparator;
import jscenegraph.database.inventor.nodes.SoSubNode;

/**
 * @author Yves Boyadjian
 *
 */
public class SoSelection extends SoSeparator {
	
	   private final SoSubNode nodeHeader = SoSubNode.SO_NODE_HEADER(SoSelection.class,this);	   	
	
		public                                                                     
	    static SoType       getClassTypeId()        /* Returns class type id */   
	                                    { return SoSubNode.getClassTypeId(SoSelection.class); }                   
	    public SoType      getTypeId()       /* Returns type id      */
	    {
			return nodeHeader.getClassTypeId();	
	    }
	  public                                                                  
	    SoFieldData   getFieldData() {
		  return nodeHeader.getFieldData(); 
	  }
	  public  static SoFieldData[] getFieldDataPtr()                              
	        { return SoSubNode.getFieldDataPtr(SoSelection.class); }              
	   
	   
    //! Change callbacks
    protected  SoCallbackList      changeCBList;
 	
    public void
     addChangeCallback(SoSelectionClassCB f, Object userData)
     {
         if (changeCBList == null)
             changeCBList = new SoCallbackList();
         changeCBList.addCallback((SoCallbackListCB ) f, userData);
     }
    
	 public void
	   removeChangeCallback(SoSelectionClassCB f, Object userData)
	   {
	       if (changeCBList != null)
	           changeCBList.removeCallback((SoCallbackListCB ) f, userData);
	   }
	 
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    This initializes the SoSelection class.
	   //
	   // Use: private
	   
	  public static void
	   initClass()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
		  SoSubNode.SO__NODE_INIT_CLASS(SoSelection.class, "Selection", SoSeparator.class);
	   }	 
}
