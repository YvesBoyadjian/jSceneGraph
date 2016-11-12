/**
 * 
 */
package jscenegraph.nodekits.inventor.nodekits;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SoFullPath;
import jscenegraph.database.inventor.SoPath;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoAction;
import jscenegraph.database.inventor.actions.SoSearchAction;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.misc.SoBase;
import jscenegraph.database.inventor.misc.SoChildList;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.nodekits.inventor.SoNodeKitPath;
import jscenegraph.port.Offset;

/**
 * @author Yves Boyadjian
 *
 */
public class SoBaseKit extends SoNode {
	
	public                                                                     
    static SoType       getClassTypeId()        /* Returns class type id */   
                                    { return classTypeId; }                   
    public SoType      getTypeId()       /* Returns type id      */
    {
		return classTypeId;		    	
    }
  public                                                                  
    SoFieldData   getFieldData() {
	  return fieldData[0]; 
  }
  public  static SoFieldData[] getFieldDataPtr()                              
        { return fieldData; }              
  private                                                                    
    static SoType       classTypeId;            /* Type id              */    
  private  static boolean       firstInstance; /* true until 2nd c'tor call */        
  private  static final SoFieldData[] fieldData = new SoFieldData[1];                                   
  private  static final SoFieldData[][]    parentFieldData = new SoFieldData[1][];	
	
	
	protected SoChildList children;
	
	protected SoNodekitParts nodekitPartsList;

	private static final SoNodekitCatalog[] nodekitCatalog = new SoNodekitCatalog[1];                          
	private static SoNodekitCatalog[]  parentNodekitCatalogPtr = null;	
	
	private  static boolean searchingChildren;
	  
	  
			public SoNodekitCatalog                                                       
			getNodekitCatalog()                                          
			{                                                                             
			    return nodekitCatalog[0];                                                    
			}
	
	/* (non-Javadoc)
	 * @see com.openinventor.inventor.fields.SoFieldContainer#plus(int)
	 */
	@Override
	public Object plus(Offset offset) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the SoNodekitCatalog for this instance of SoBaseKit. 
	 * While each instance of a given class creates its own distinct 
	 * set of parts (which are actual nodes), 
	 * all instances share the same catalog 
	 * (which describes the parts but contains no actual node pointers). 
	 * 
	 * @return
	 */
	public SoNodekitCatalog getNodeKitCatalog() {
		return nodekitCatalog[0];
	}
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    Return the part with the given name
	   //
	   // Use: public
	   
	  public SoNode 
	   getPart( SbName partName, boolean makeIfNeeded )
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       // the fourth argument, leafCheck and publicCheck are TRUE, because we 
	       // don't ordinarily return parts unless they are public leaves.
	       return ( getAnyPart( partName, makeIfNeeded, true, true ) );
	   }
	   
	  	
		
	/**
	 * Given a node or a path to a node, checks if the part exists in the nodekit, 
	 * in a nested nodekit, or an element of a list part. 
	 * If so, returns a string describing the part name; 
	 * otherwise, returns an empty string (""). 
	 * 
	 * @param part
	 * @return
	 */
	public String getPartString(SoBase part) {
		return ""; // TODO
	}
	
	/**
	 * Inserts the given node (not a copy) as the new part specified by partName. 
	 * See getPart() for the syntax of partName. 
	 * This method adds any extra nodes needed to fit the part into the nodekit's catalog. 
	 * For example, if you call:
	 * 
	 * mySepKit->setPart("childList[0]", myNewChild);
	 * 
	 * the kit may need to create the part childList before it can install myNewChild. 
	 * Run-time type checking verifies that the node type of newPart matches the type called for by partName. 
	 * For example, if partName was a material for an SoSeparatorKit, but newPart was an SoTransform node, 
	 * then the node would not be installed, and FALSE would be returned.
	 * If newPart is NULL, then the node specified by partName is removed. 
	 * If this renders any private parts useless (as occurs when you remove the last child of an SoGroup node), 
	 * they will also be removed. 
	 * Hence nodekits do not retain unnecessary nodes.
	 * 
	 * TRUE is returned on success, and FALSE upon error. 
	 * 
	 * @param partName
	 * @param from
	 * @return
	 */
	
	// java port
	public boolean setPart(String partName, SoNode from) {
		return setPart(new SbName(partName), from);
	}
	
	public boolean setPart(SbName partName, SoNode from) {
	     // the third argument, anyPart, is FALSE, because we don't ordinarily
		 // return parts unless they are public leaves.
		 return ( setAnyPart( partName, from, false ) );
	}
	
	/**
	 * like their public versions, but are allowed access to non-leaf and private parts. 
	 * These are virtual so subclasses may do extra things when certain parts are requested. 
	 * 
	 * @param partName
	 * @param makeIfNeeded
	 * @param leafCheck
	 * @param publicCheck
	 * @return
	 */
	protected SoNode getAnyPart(SbName partName, boolean makeIfNeeded, boolean leafCheck, boolean publicCheck) {
	     return (nodekitPartsList.getAnyPart( partName, makeIfNeeded, leafCheck, publicCheck ));		
	}
	
	// java port
	protected boolean setAnyPart(SbName partName, SoNode from) {
		return setAnyPart(partName, from, true);
	}
	
	//
	   // Description:
	   //    Return the part with the given name
	   //
	   // Use: protected
	   	
	protected boolean setAnyPart(SbName partName, SoNode from, boolean anyPart) {
	     if ( from != null)
	    	           from.ref();
	    	       boolean answer = nodekitPartsList.setAnyPart( partName, from, anyPart );
	    	       if ( from != null)
	    	           from.unref();
	    	       return answer;
	    	  
	}

	//
	  // Description:
	  //    Redefines this to add this node and all part nodes to the dictionary.
	  //
	  // Use: protected, virtual
	  
	public SoNode addToCopyDict() {
		
	     // If this node is already in the dictionary, nothing else to do
		      SoNode copy = (SoNode ) checkCopy(this);
		      if (copy == null) {
		  
		          // Create and add a new instance to the dictionary
		          copy = (SoNode ) getTypeId().createInstance();
		          copy.ref();
		          addCopy(this, copy);            // Adds a ref()
		          copy.unrefNoDelete();
		  
		          // Recurse on all non-NULL parts. Skip part 0, which is "this".
		          for (int i = 1; i < nodekitPartsList.numEntries; i++) {
		              SoNode partNode = nodekitPartsList.fieldList[i].getValue();
		              if (partNode != null)
		                  partNode.addToCopyDict();
		          }
		      }
		  
		      return copy;
		 	}
	
////////////////////////////////////////////////////////////////////////
//
// Description:
//     Returns a path that begins with 'this', and ends at 
//     the part named.  
//
//     If 'pathToExtend' is not NULL:
//         the returned path will be a copy of 'pathToExtend' with the new 
//         path appended to it. In order to append, however, the following 
//         condition must be met:
//             'this' must lie on the pathToExtend, otherwise NULL is returned.
//         If 'this' lies on pathToExtend, then a copy is made, and the
//             copy is truncated to end at 'this.' Finally, the path from 'this'
//             down to the part will be appended.
//     
//
// Use: public

	// java port
	protected SoNodeKitPath 
	createPathToAnyPart(final SbName partName, 
	                            boolean makeIfNeeded, boolean leafCheck,
	                            boolean publicCheck) {
		return createPathToAnyPart(partName,makeIfNeeded,leafCheck,publicCheck,null);
	}
protected SoNodeKitPath 
createPathToAnyPart(final SbName partName, 
                            boolean makeIfNeeded, boolean leafCheck,
                            boolean publicCheck, final SoPath pathToExtend )
//
////////////////////////////////////////////////////////////////////////
{
    // Return if pathToExtend is non-NULL but doesn't contain 'this'
    if (   pathToExtend != null &&
         (new SoFullPath(pathToExtend)).containsNode(this) == false ) {
//#ifdef DEBUG
            SoDebugError.post("SoBaseKit::createPathToAnyPart",
            "The given pathToExtend does not contain this node.Returning NULL");
//#endif
        return null;
    }

    SoFullPath thePath = nodekitPartsList.createPathToAnyPart( partName, 
                                        makeIfNeeded, leafCheck, publicCheck );
    if ( thePath == null )
        return null;

    if ( pathToExtend == null )
        return (SoNodeKitPath) thePath;

    final SoFullPath fullPathToExtend = new SoFullPath( pathToExtend);

    thePath.ref();
    fullPathToExtend.ref();

    // Create a copy of 'fullPathToExtend' with 'thePath' tacked onto it

    // First, copy fullPathToExtend into longPath
    SoFullPath longPath = new SoFullPath( fullPathToExtend.copy());
    longPath.ref();

    // Now, truncate longPath to end at 'this'
    while ( longPath.getTail() != this )
        longPath.pop();

    // Finally, append 'thePath' after 'longPath'.  Leave out thePath->head(), 
    // since it's already at the tail of longPath...
    for( int i = 1; i < thePath.getLength(); i++ )
        longPath.append( thePath.getIndex( i ) );

    thePath.unref();
    fullPathToExtend.unref();
    longPath.unrefNoDelete();

    return (SoNodeKitPath)longPath;
}

	private int  getNumChildren() { return (children.getLength()); }
	
	void removeChild(int index) {
	     // copied from SoGroup
//		  #ifdef DEBUG
//		      if (index < 0 || index >= getNumChildren()) {
//		          SoDebugError::post( "SoBaseKit::removeChild",
//		                             "Index %d is out of range %d - %d", index, 0, getNumChildren() - 1);
//		          return;
//		      }
//		  #endif                          /* DEBUG */
		      // Play it safe anyway...
		      if (index >= 0) {
		          children.remove(index);
		      }
		  
		 		
	}
	
	void removeChild( SoNode child  ) { removeChild(findChild(child)); }
	
	void addChild(SoNode child) {
		
//		 #ifdef DEBUG
//		      if (child == NULL) {
//		          SoDebugError::post( "SoBaseKit::addChild", "NULL child node");
//		          return;
//		      }
//		  #endif                          /* DEBUG */
		  
		      children.append(child);
		 	}
	
	int findChild(final SoNode child) {
		      int i, num;
		  
		      num = getNumChildren();
		  
		      for (i = 0; i < num; i++)
		          if (getChild(i) == child) return(i);
		  
		      return(-1);
		  }		  		


	  void
	  insertChild(SoNode child, int newChildIndex)
	  {
//	  #ifdef DEBUG
//	      if (child == NULL) {
//	          SoDebugError::post( "SoBaseKit::insertChild", "NULL child node");
//	          return;
//	      }
//	  
//	      // Make sure index is reasonable
//	      if (newChildIndex < 0 || newChildIndex > getNumChildren()) {
//	          SoDebugError::post( "SoBaseKit::insertChild",
//	                             "Index %d is out of range %d - %d",newChildIndex, 0, getNumChildren());
//	          return;
//	      }
//	  #endif                          /* DEBUG */
	  
	      // See if adding at end
	      if (newChildIndex >= getNumChildren())
	          children.append(child);
	      else
	          children.insert(child, newChildIndex);
	  }
	  	
	private SoNode getChild( int index) { return children.operator_square_bracket(index); }

	private void replaceChild( int index, SoNode newChild) {
	     // copied from SoGroup...
//		  #ifdef DEBUG
//		      if (index < 0 || index >= getNumChildren()) {
//		          SoDebugError::post( "SoBaseKit::replaceChild",
//		                             "Index %d is out of range %d - %d", index, 0, getNumChildren() - 1);
//		          return;
//		      }
//		  #endif                          /* DEBUG */
		  
		      // Play it safe anyway...
		      if (index >= 0)
		          children.set(index, newChild);
		 		
	}
	
	void replaceChild( SoNode oldChild, SoNode newChild)
	           { replaceChild(findChild(oldChild),newChild); }

    //! Sets and queries if nodekit children are searched during SoSearchAction
      //! traversal.  By default, they are not.
    public  static boolean isSearchingChildren() { return searchingChildren; }
 	
	 public void
	  search( SoSearchAction action )
	  {
	      super.search(action);
	      if (isSearchingChildren())
	          /*SoBaseKit.*/doAction( action );
	  }
	 
	 /**
	  * This method performs the "typical" operation of a node for any action. 
	  * The default implementation does nothing. 
	  */
	 public void
	  doAction( SoAction action )
	  {
	      final int[]         numIndices = new int[1];
	      final int[][]  indices = new int[1][];
	  
	      if (action.getPathCode(numIndices, indices) == SoAction.PathCode.IN_PATH)
	          children.traverse(action, 0, indices[0][numIndices[0] - 1]);
	      else
	          children.traverse(action);
	  }
	  	 
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    This initializes the SoBaseKit class.
	   //
	   // Use: internal
	   
	  public static void
	   initClass()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       // We can not attempt to inherit a parentNodekitCatalogPtr
	       // from SoNode (it is undefined there).
	       // Therefore, we don't call SO_KIT_INIT_CLASS here.
	       // We just call it for SO_NODE and set parentNodekitCatalog to NULL
	   
	       classTypeId = SO__NODE_INIT_CLASS(SoBaseKit.class, "BaseKit", SoNode.class,parentFieldData);
	       parentNodekitCatalogPtr = null;
	   }
	   	 
	              
		public static void  SO__KIT_INIT_CLASS(Class className,String classPrintName,Class< ? extends SoBaseKit> parentClass) {
			SO__NODE_INIT_CLASS(className, classPrintName, parentClass,parentFieldData);
		    int _value_false= 0;                                                      
		    do {                                                                      
		        parentNodekitCatalogPtr = SoSubKit.getClassNodekitCatalogPtr(parentClass);   
		    } while (_value_false != 0);                                                   
	  }	  
	 }
