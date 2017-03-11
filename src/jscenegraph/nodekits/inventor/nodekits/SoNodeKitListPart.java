/**
 * 
 */
package jscenegraph.nodekits.inventor.nodekits;

import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.SoTypeList;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.fields.SoMFName;
import jscenegraph.database.inventor.fields.SoSFName;
import jscenegraph.database.inventor.fields.SoSFNode;
import jscenegraph.database.inventor.misc.SoChildList;
import jscenegraph.database.inventor.nodes.SoGroup;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.database.inventor.nodes.SoSubNode;
import jscenegraph.database.inventor.nodes.SoSwitch;


/**
 * @author Yves Boyadjian
 *
 */
public class SoNodeKitListPart extends SoNode {
	
	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_HEADER(SoNodeKitListPart.class, this);
	
	public                                                                     
    static SoType       getClassTypeId()        /* Returns class type id */   
                                    { return SoSubNode.getClassTypeId(SoNodeKitListPart.class); }                   
    public SoType      getTypeId()       /* Returns type id      */
    {
		return nodeHeader.getClassTypeId();	
    }
  public                                                                  
    SoFieldData   getFieldData() {
	  return nodeHeader.getFieldData(); 
  }
  public  static SoFieldData[] getFieldDataPtr()                              
        { return SoSubNode.getFieldDataPtr(SoNodeKitListPart.class); }              
   
	protected SoChildList children;
	
	private final SoSFName containerTypeName = new SoSFName();
	private final SoMFName childTypeNames = new SoMFName();
	private final SoSFNode containerNode = new SoSFNode();
	
	private final SoTypeList childTypes = new SoTypeList();
	
	private boolean areTypesLocked;

	// Gets and sets the type of node used as the container. 
	//
	   // Returns type of container.
	   //
	   	public SoType getContainerType() {
		return (SoType.fromName( containerTypeName.getValue() ) );
	}
	
	// Gets and sets the type of node used as the container. 
	public void setContainerType(SoType newContainerType) {
		
	     if ( isTypeLocked() ) {
//	    	   #ifdef DEBUG
//	    	           SoDebugError::post("SoNodeKitListPart::setContainerType",
//	    	           "You can\'t change the type because the type lock has been turned on");
//	    	   #endif
	    	           return;
	    	       }
	    	       
	    	       if ( newContainerType == getContainerType() )
	    	           return;
	    	   
	    	       if ( !newContainerType.isDerivedFrom( SoGroup.getClassTypeId() ) )
	    	           return;
	    	   
	    	       if ( newContainerType.canCreateInstance() == false )
	    	           return;
	    	   
	    	       // If necessary, create a new container node of the correct type:
	    	       SoGroup oldContainer = (SoGroup ) containerNode.getValue();
	    	       SoGroup newContainer = null;
	    	   
	    	       if (  oldContainer == null ||
	    	            !oldContainer.isOfType( newContainerType ) ) {
	    	   
	    	           newContainer = (SoGroup ) newContainerType.createInstance(); 
	    	           newContainer.ref();
	    	   
	    	               // copy children from oldContainer to new one.
	    	               if ( oldContainer != null ) {
	    	                   for (int i = 0; i < oldContainer.getNumChildren(); i++ ) 
	    	                       newContainer.addChild( oldContainer.getChild(i) );
	    	               }
	    	           
	    	               // replace the container in this nodes children list
	    	               int oldChildNum = children.find( oldContainer );
	    	               if ( oldChildNum == -1 )
	    	                   children.insert( newContainer, 0 );
	    	               else
	    	                   children.set( oldChildNum, newContainer );
	    	   
	    	               containerNode.setValue( newContainer);
	    	   
	    	           newContainer.unref();
	    	       }
	    	   
	    	       containerTypeName.setValue( newContainerType.getName() );
	    	  	}
	
	//
	   // Description:
	   //    Returns a list of the allowable child types.
	   //    If nothing has been specified, any type of node is allowed.
	   //
	   // Use: public
	public SoTypeList getChildTypes() {
		return childTypes;
	}
	
	/**
	 * Permits the node type typeToAdd as a child. 
	 * The first time the addChildType() method is called, 
	 * the default of SoNode is overridden and only the new typeToAdd is permitted.
	 * In subsequent calls to addChildType(), the typeToAdd is added to the 
	 * existing types. 
	 * 
	 * @param typeToAdd
	 */
	public void addChildType(SoType typeToAdd) {
	     if ( isTypeLocked() ) {
//	    	   #ifdef DEBUG
//	    	           SoDebugError::post("SoNodeKitListPart::addChildType",
//	    	           "You can\'t change the type because the type lock has been turned on");
//	    	   #endif
	    	           return;
	    	       }
	    	       
	    	       // If this is our first one, then truncate the childTypes to 0.
	    	       // By default, (i.e., until we set the first one), the initial
	    	       // entry is SoNode::getClassTypeId(), which allows any node to be
	    	       // permitted.
	    	       if ( childTypeNames.isDefault() )
	    	           childTypes.truncate(0);
	    	   
	    	       // Add the type to the childTypes list if it's not there yet.
	    	       if ( childTypes.find( typeToAdd ) == -1 ) {
	    	           childTypes.append( typeToAdd );
	    	   
	    	           // Set the value of the corresponding entry in the
	    	           // childTypeNames field.
	    	           childTypeNames.set1Value(childTypes.getLength()-1,typeToAdd.getName());
	    	       }
	    	  
	}
	
	/**
	 * These are the functions used to edit the children. 
	 * They parallel those of SoGroup, except that they always check 
	 * the child types against those which are permissible. 
	 * See SoGroup for details. 
	 * 
	 * @param child
	 * @param childIndex
	 */
	//
	   // Description:
	   //    This inserts a child into the container so that it will have the given
	   //    index.
	   //
	   // Use: public
	   	public void insertChild(SoNode child, int newChildIndex) {
	     if ( isChildPermitted( child ) ) {
	    	           getContainerNode().insertChild(child, newChildIndex);
	    	       }
//	    	   #ifdef DEBUG
//	    	       else {
//	    	           SoDebugError::post("SoNodeKitListPart::insertChild",
//	    	                              "--> Can\'t insert child of type \"%s\" ",
//	    	                              child->getTypeId().getName().getString() );
//	    	       }
//	    	   #endif
	    	  		
	}
	
	// Returns whether a node of type typeToCheck may be added as a child. 
	 //
	   // Description:
	   //    Returns whether a type is legal as a child of the container.
	   //
	   // Use: public
	  public boolean isTypePermitted(SoType typeToCheck) {
		     for ( int i = 0; i < childTypes.getLength(); i++ ) {
		    	           if ( typeToCheck.isDerivedFrom( childTypes.operator_square_bracket(i) ) )
		    	               return true;
		    	       }
		    	       return false;
		    	   		
	}
	
	 //
	   // Description:
	   //    Returns whether a type is legal as a child of the container.
	   //
	   // Use: public
	   
	   boolean
	   isChildPermitted( final SoNode child )
	   //
	   {
	       for ( int i = 0; i < childTypes.getLength(); i++ ) {
	           if ( child.isOfType( childTypes.operator_square_bracket(i) ) )
	               return true;
	       }
	       return false;
	   }
	   
	   /**
	    * This function permanently locks the permitted child types and 
	    * the container type permanently. 
	    * Calls to setContainerType() and addChildType() will have no effect 
	    * after this function is called. 
	    */
	   //
	    // Description:
	    //    Locks the types of the container and child nodes.
	    //    Once called, the methods setContainerType and addChildType
	    //    no longer have any effect.
	    //
	    // Use: public
	    	   public void lockTypes() {
		   areTypesLocked = true;
	   }
	   
	   /**
	    * Returns whether the permitted child types and the 
	    * container type are locked (i.e. cannot be changed). 
	    * See lockTypes()
	    * 
	    * @return
	    */
	  	public boolean isTypeLocked() {
	  		 return areTypesLocked; 
	  	}
	
	/**
	 * These are the functions used to edit the children. 
	 * They parallel those of SoGroup, except that they always check 
	 * the child types against those which are permissible. 
	 * See SoGroup for details. 
	 * 
	 * @param child
	 */
	//
	   // Description:
	   //    This adds a child as the last one in the container.
	   //
	   // Use: public
	   	public void addChild(SoNode child) {
	        if ( isChildPermitted( child ) ) {
	        	           // Turn off notification while getting the container.
	        	           // We'll be notifying when the child gets added, so there's
	        	           // no reason to notify if the container is created as well.
	        	           boolean wasEn = enableNotify(false);
	        	           SoGroup cont = getContainerNode();
	        	           enableNotify(wasEn);
	        	   
	        	           cont.addChild(child);
	        	       }
//	        	   #ifdef DEBUG
//	        	       else {
//	        	           SoDebugError::post("SoNodeKitListPart::addChild",
//	        	                              "--> Can\'t add child of type \"%s\" ",
//	        	                               child->getTypeId().getName().getString() );
//	        	       }
//	        	   #endif
	        	  		
	}
	   	
	   	/**
	   	 * These are the functions used to edit the children. 
	   	 * They parallel those of SoGroup, except that they always check 
	   	 * the child types against those which are permissible. 
	   	 * See SoGroup for details. 
	   	 * 
	   	 * @param index
	   	 * @return
	   	 */
	   	//
	     // Description:
	     //    returns the node of the given index from the container
	     //
	     // Use: public
	    public SoNode getChild(int index) {
	        if ( containerNode.getValue() == null )
	        	           return null;
	        	   
	        	       return ((SoGroup )containerNode.getValue()).getChild(index);
	        	  	   		
	   	}

	/**
	 * These are the functions used to edit the children. 
	 * They parallel those of SoGroup, except that they always check the child types 
	 * against those which are permissible. See SoGroup for details. 
	 * 
	 * @return
	 */
	public int getNumChildren() {
		
	     if ( containerNode.getValue() == null )
	    	           return 0;
	    	   
	    	       return ((SoGroup )containerNode.getValue()).getNumChildren();
	    	  	}
	
	/**
	 * These are the functions used to edit the children. 
	 * They parallel those of SoGroup, except that they always check 
	 * the child types against those which are permissible. 
	 * See SoGroup for details. 
	 * 
	 * @param index
	 */
	public void removeChild(int index) {
	     if ( containerNode.getValue() == null )
	    	           return;
	    	   
	    	       SoGroup grp = (SoGroup ) containerNode.getValue();
	    	       grp.removeChild(index);
	    	   
	    	       // If the parent is a switch, make sure this doesn't 
	    	       // screw it up...
	    	       if ( grp.isOfType( SoSwitch.getClassTypeId() ) ){
	    	           SoSwitch sw = (SoSwitch ) grp;
	    	           int swNum = sw.getNumChildren();
	    	           if (sw.whichChild.getValue() >= swNum)
	    	               sw.whichChild.setValue(  swNum - 1 );
	    	       }
	    	  		
	}
	
	/**
	 * These are the functions used to edit the children. 
	 * They parallel those of SoGroup, except that they always check the 
	 * child types against those which are permissible. 
	 * See SoGroup for details. 
	 * 
	 * @param index
	 * @param newChild
	 */
	public void replaceChild(int index, SoNode newChild) {
	     if ( containerNode.getValue() == null )
	    	           return;
	    	   
	    	       if ( isChildPermitted( newChild ) ) {
	    	           ((SoGroup )containerNode.getValue()).replaceChild(index, newChild);
	    	       }
//	    	   #ifdef DEBUG
//	    	       else {
//	    	           SoDebugError::post("SoNodeKitListPart::replaceChild",
//	    	                              "--> Can\'t replace with child of type \"%s\"",
//	    	                              newChild->getTypeId().getName().getString() );
//	    	       }
//	    	   #endif
	    	   		
	}
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    Does the node affect the state? Well, it all depends on the container
	   //    node.
	   //
	   // Use: public
	   
	  public boolean
	   affectsState()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       if ( containerNode.getValue() == null )
	           return false;
	       else
	           return ( containerNode.getValue().affectsState() );
	   }
	   
	  	
	protected SoGroup getContainerNode() {
		
	     if ( containerNode.getValue() != null )
	    	           return ((SoGroup ) containerNode.getValue()); 
	    	       else {
	    	           SoType   contType = SoType.fromName( containerTypeName.getValue() );
	    	           SoGroup contNode = (SoGroup ) contType.createInstance(); 
	    	           contNode.ref();
	    	   
	    	           // put contNode into this node's children list
	    	           if (children.getLength() == 0)
	    	               children.append( contNode );
	    	           else
	    	               children.insert( contNode, 0 );
	    	   
	    	           containerNode.setValue( contNode);
	    	           contNode.unref();
	    	   
	    	           return contNode;
	    	       }
	    	  	}

	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    This initializes the SoNodeKitListPart class.
	   //
	   // Use: internal
	   
	public static void
	   initClass()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       SoSubNode.SO__NODE_INIT_CLASS(SoNodeKitListPart.class, "NodeKitListPart", SoNode.class);
	   }
}
