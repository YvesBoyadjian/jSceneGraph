/**
 * 
 */
package jscenegraph.interaction.inventor.nodekits;

import java.util.StringTokenizer;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SbPList;
import jscenegraph.database.inventor.SoFullPath;
import jscenegraph.database.inventor.SoPath;
import jscenegraph.database.inventor.SoPathList;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.nodekits.inventor.SoNodeKitPath;
import jscenegraph.nodekits.inventor.nodekits.SoBaseKit;
import jscenegraph.nodekits.inventor.nodekits.SoNodekitCatalog;
import jscenegraph.nodekits.inventor.nodekits.SoSubKit;

/**
 * @author Yves Boyadjian
 *
 */
public class SoInteractionKit extends SoBaseKit {
	
	private final SoSubKit kitHeader = SoSubKit.SO_KIT_HEADER(SoInteractionKit.class,this);
	
	   private SoPathList    surrogatePartPathList;
	   private SbPList       surrogatePartNameList;
		    	

	// Reimplemented from SoBaseKit.
	protected boolean setAnyPart(SbName partName, SoNode from, boolean anyPart) {
		
	     // Try to create set the part:
		       // You might not be able to, for example:
		       //    [a] part does not exist
		       //    [b] anyPart is FALSE and the part is a leaf or non-public.
		       if ( !super.setAnyPart( partName, from, anyPart ))
		           return false;
		   
		       // Temporary ref
		       if (from!= null)
		           from.ref();
		   
		       // If you were successful, then try to set the surrogate path to NULL
		       boolean success = true;
		       if ( !setAnySurrogatePath( partName, null, !anyPart, !anyPart )) {
//		   #ifdef DEBUG
//		           SoDebugError::post("SoInteractionKit::setAnyPart",
//		                   "can not set surrogate path to NULL for part %s",
//		                   partName.getString() );
//		   #endif
		           success = false;
		       }
		   
		       // Undo temporary ref
		       if (from != null)
		           from.unref();
		       return success;
		  	}
	

////////////////////////////////////////////////////////////////////////
//
// Use: protected
//
 protected boolean
setAnySurrogatePath( final SbName partName, 
                        SoPath from, boolean leafCheck, boolean publicCheck )
//
////////////////////////////////////////////////////////////////////////
{
    // Strategy:
    //   [-2] If 'partName' is in this catalog, just call 
    //        setMySurrogatePath and return.
    //   [-1] If 'from' is NULL, and partName is not directly in this catalog,
    //        determine which of our leaf nodes is on the way to the part.
    //        This will be either a nodekit or a list part.
    //        If this 'intermediary' has not been created yet, then we
    //        can just return.  This is because if the intermediary is NULL,
    //        then the part below it can have no value as of yet. So we don't
    //        need to bother removing it's surrogate path (which is what 
    //        we do when 'from' is NULL.
    //   [0] Temporarily ref 'from' and 'this'
    //       We need to ref 'this' because creating a path refs this,
    //       and this can get called from within constructors since
    //       it's called from within setPart(), getPart(), etc.
    //   [1] get partPath, which leads down to the part.
    //       First time, use 'makeIfNeeded' of FALSE.
    //       That's how we'll find out if there was something there to start.
    //
    //   [2] If (partPath == NULL), call it again with 'makeIfNeeded' of TRUE,
    //       but remember that we must NULL out the part when we are finished.
    //
    //   [3] Now we've got a path from 'this' down to the part.
    //       Find 'owner', the first InteractionKit above the part in the 
    //       partPath. 
    //       Note:   'owner' might not == this.
    //   [4] Find the 'nameInOwner' the name of the part within 'owner'
    //   [5] Tell 'owner' to use the given path 'from' as its surrogate 
    //       path for 'nameInOwner'
    //   [6] If you need to, remember to set the node-part back to NULL
    //   [8] Undo temporary ref of 'from' and 'this'

    //   [-2] If 'partName' is in this catalog, just call 
    //        setMySurrogatePath and return.
        final SoNodekitCatalog cat = getNodekitCatalog();
        int partNum = cat.getPartNumber( partName );
        if ( partNum != SoNodekitCatalog.SO_CATALOG_NAME_NOT_FOUND ) {
            if ( leafCheck && (cat.isLeaf(partNum) == false) )
                return false;
            if ( publicCheck && (cat.isPublic(partNum) == false) )
                return false;
            setMySurrogatePath(partName, from);
            return true;
        }

    //   [-1] If 'from' is NULL, and partName is not directly in this catalog,
    //        determine which of our leaf nodes is on the way to the part.
    //        This will be either a nodekit or a list part.
    //        If this 'intermediary' has not been created yet, then we
    //        can just return.  This is because if the intermediary is NULL,
    //        then the part below it can have no value as of yet. So we don't
    //        need to bother removing its surrogate path (which is what 
    //        we do when 'from' is NULL.
        if (from == null) {

            // See if there's a '.' and/or a '[' in the partName.
            // (as in "childList[0].appearance")
            // If so, get the string up to whichever came first.
            // This will be the 'intermediary' we look for.
            Integer dotPtr   =  strchr( partName.getString(), '.' );
            Integer brackPtr =  strchr( partName.getString(), '[' );

            if ( dotPtr != null || brackPtr != null ) {
                String nameCopy = strdup( partName.getString() );
                String firstName;
                if (dotPtr == null)
                    firstName = strtok( nameCopy, "[");
                else if (brackPtr == null || dotPtr < brackPtr)
                    firstName = strtok( nameCopy, ".");
                else 
                    firstName = strtok( nameCopy, "[");

                // Okay, look for the part, then free the string copy.
                int firstPartNum = cat.getPartNumber( new SbName(firstName) );

                SoNode firstPartNode = null; 
                if ( firstPartNum != SoNodekitCatalog.SO_CATALOG_NAME_NOT_FOUND ) {
                    // Check if the part is there.
                    // 2nd arg is FALSE, 'cause we don't want to create part.
                    // 3rd arg is TRUE 'cause this better be a leaf.
                    firstPartNode = super.getAnyPart( new SbName(firstName), 
                                                false, true, publicCheck );
                }
                //free (nameCopy); java port

                // If the intermediary doesn't exist, return TRUE
                if (firstPartNode == null)
                    return true;
            }
        }


    //   [0] Temporarily ref 'from' and 'this'
        if (from != null) from.ref();
        ref();

    //   [1] get partPath, which leads down to the part.
    //       First time, use 'makeIfNeeded' of FALSE.
    //       That's how we'll find out if there was something there to start.
        boolean        makeIfNeeded = false;
        SoNodeKitPath partPath    = null;

        partPath = super.createPathToAnyPart( partName, makeIfNeeded, 
                                                   leafCheck, publicCheck );

    //   [2] If (partPath == NULL), call it again with 'makeIfNeeded' of TRUE,
    //       but remember that we must NULL out the part when we are finished.
        if (partPath == null) {
            // Try again, this time with 'makeIfNeeded' TRUE
            makeIfNeeded = true;
            partPath = super.createPathToAnyPart( partName, makeIfNeeded, 
                                                   leafCheck, publicCheck );
        }
        if (partPath == null) {
            // This would happen if leafCheck or publicCheck were TRUE
            // and the check failed.
//#ifdef DEBUG
//            SoDebugError::post("SoInteractionKit::setAnySurrogatePath",
//                "can not get a part path for part %s", partName.getString());
//#endif
            // Undo temporary ref of 'from' and 'this'
            if (from != null) from.unref();
            unrefNoDelete();
            return false;
        }
        else
            partPath.ref();

    //   [3] Now we've got a path from 'this' down to the part.
    //       Find 'owner', the first InteractionKit above the part in the 
    //       partPath. 
    //       Note:   'owner' might not == this.
        SoInteractionKit owner = null;
        for (int i = partPath.getLength() - 1;  i >= 0; i-- ) {
            SoNode n = partPath.getNode(i);
            if ( n != (new SoFullPath (partPath)).getTail() &&
                 n.isOfType( SoInteractionKit.getClassTypeId() ) ) {
                    owner = (SoInteractionKit ) n;
                    owner.ref();
                    break;
            }
        }
        if ( owner == null ) {
            partPath.unref();
            // Undo temporary ref of 'from' and this.
            if (from != null) from.unref();
            unrefNoDelete();
            return false;
        }

    //   [4] Find the 'nameInOwner' the name of the part within 'owner'
        SbName nameInOwner = new SbName(owner.getPartString( partPath ));
        
    //   [5] Tell 'owner' to use the given path 'from' as its surrogate 
    //       path for 'nameInOwner'
    //       Use setMySurrogatePath for this...
        owner.setMySurrogatePath(nameInOwner,from);

    //   [6] If you need to, remember to set the node-part back to NULL
        boolean success = true;
        if (makeIfNeeded == true) {
            boolean anyPart = ( !leafCheck && !publicCheck );
            if ( !super.setAnyPart( partName, null, anyPart ) )
                success = false;
        }

        owner.unref();
        partPath.unref();

    //   [8] Undo temporary ref of 'from' and 'this'
        if (from != null) from.unref();
        unrefNoDelete();

        return success;
}
 
 //
  // Assumes that this node is the 'owner' of the surrogate part.
  // That is, it is the first InteractionKit above the part.
  //
  // This means that 'name' is registered in this node's catalog,
  // or if the name is 'listName[#]', then listName is in the catalog.
  //
  // Passing a value of NULL for newPath has the effect of removing 
  // this part from the surrogate path lists.
  //
  // Use: private
  //
  private void setMySurrogatePath( SbName name, SoPath newPath )
  //
  {
      int index = surrogatePartNameList.find( (Object) name.getString() );
      if ( index != -1 ) {
          // an entry already exists for this part name. So we need to remove
          // the old entry before adding the new one.
          surrogatePartNameList.remove( index );
          surrogatePartPathList.remove( index );
      }
  
     // Now append the new entry.
     if ( newPath != null ) {
         surrogatePartNameList.append( (Object) name.getString() );
         surrogatePartPathList.append( newPath );
     }
 }
 
  ////////////////////////////////////////////////////////////////////////
   //
   // Description:
   //    overrides method in SoNode to return FALSE.
   //    Since there is a separator as the top child of the dragger.
   //
   // Use: public
   //
  public boolean
   affectsState()
   {
       return false;
   }
   

 // java port
 private Integer strchr(String str, int character) {
	 int index = str.indexOf(character);
	 if(index >= 0) {
		 return index;
	 }
	 else {
		 return null;
	 }
 }
 
 private String strdup(String str) {
	 return str;
 }
 
 private String strtok(String str, String tok) {
	 StringTokenizer stringTokenizer = new StringTokenizer(str, tok);
	 if(stringTokenizer.hasMoreTokens()) {
		 return stringTokenizer.nextToken();
	 }
	 return null;
 }
	
 ////////////////////////////////////////////////////////////////////////
  //
  // Description:
  //    Initialize the class
  //
  // Use: static, internal
  //
  
 public static void
  initClass()
  //
  ////////////////////////////////////////////////////////////////////////
  {
      SO__KIT_INIT_CLASS(SoInteractionKit.class, "InteractionKit", SoBaseKit.class);
  }
  }
