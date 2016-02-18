/**
 * 
 */
package jscenegraph.nodekits.inventor.nodekits;

import jscenegraph.database.inventor.SbDict;
import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.SoTypeList;


/**
 * @author Yves Boyadjian
 *
 */
public class SoNodekitCatalog {
	
	public static final Integer SO_CATALOG_NAME_NOT_FOUND = -1;
	public static final Integer SO_CATALOG_THIS_PART_NUM = 0;

	private static SbName emptyName;
	private static SoTypeList emptyList;
	private static SoType badType;
	
	private int numEntries;
	private SoNodekitCatalogEntry[] entries; 
    private final SbDict                partNameDict = new SbDict(); 
  	
    // Returns number of entries in the catalog. 
    public int getNumEntries() {
    	 return numEntries; 
    }
    
	//
	   // Description:
	   //    For finding the partNumber of an entry given its reference name.
	   //
	   // Use: internal
	   
	// Given the name of a part, returns its part number in the catalog.
	public int getPartNumber(SbName theName) {
	    Object[] castPNum = new Object[1];
	      
	          if ( partNameDict.find( theName.getString(), castPNum ) )
//	      #if (_MIPS_SZPTR == 64 || __ia64 || __LP64__)
	              return  (Integer)castPNum[0];  // System long
//	      #else
//	              return ( (int) castPNum );
//	      #endif
	          else 
	              return SO_CATALOG_NAME_NOT_FOUND;
	     		
	}
	
	// Given the part number of a part, returns its name in the catalog. 
	public SbName getName(int thePartNumber) {
	     // return the name of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].getName();
		       else
		           return emptyName;
		  		
	}
	
	public SoType getType(int thePartNumber) {
	     // return the type of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].getType();
		       else
		           return badType;
		  		
	}
	
	public SoType getDefaultType(int thePartNumber) {
		
	     // return the defaultType of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].getDefaultType();
		       else
		           return badType;
		  	}
	
	public boolean isLeaf(int thePartNumber) {
	     // return the type of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].isLeaf();
		       else
		           return true;
		  		
	}
	
	//
	   // Description:
	   //    For finding the name of the parent of an entry.
	   //
	   // Use: internal
	   
	  public SbName 
	   getParentName( int thePartNumber )
	   //
	   {
	       // return the entry, if you can find it.
	       if ( thePartNumber >= 0 && thePartNumber < numEntries )
	           return entries[thePartNumber].getParentName();
	       else
	           return emptyName;
	   }
	   	
	//
	   // Description:
	   //    For finding the name of the parent of an entry.
	   //
	   // Use: internal
	   
	public int getParentPartNumber(int thePartNumber) {
	     SbName pName = getParentName( thePartNumber );
	          return( getPartNumber( pName ) );
	     		
	}
	

	   //
	   // Description:
	   //    For finding the name of the right sibling of an entry.
	   //
	   // Use: internal
	   
	  public SbName 
	   getRightSiblingName( int thePartNumber )
	   //
	   {
	       // return the entry, if you can find it.
	       if ( thePartNumber >= 0 && thePartNumber < numEntries )
	           return entries[thePartNumber].getRightSiblingName();
	       else
	           return emptyName;
	   }
	   	
	//
	   // Description:
	   //    For finding the name of the rightSibling of an entry.
	   //
	   // Use: internal
	  
	public int getRightSiblingPartNumber(int thePartNumber) {
	     SbName sName = getRightSiblingName( thePartNumber );
	          return( getPartNumber( sName ) );
	     		
	}
	
	//
	   // Description:
	   //    For finding 'listPart' of an entry.
	   //
	   // Use: internal
	   	
	public boolean isList(int thePartNumber) {
		
	     // return the type of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].isList();
		       else
		           return false;
		  	}
	
	//
	   // Description:
	   //    For finding the type of the container of a list entry.
	   //
	   // Use: internal
	   	
	public SoType getListContainerType( int thePartNumber) {
	     // return the defaultType of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].getListContainerType();
		       else
		           return badType;
		  		
	}
	
	//
	   // Description:
	   //    For finding the list item type of an entry.
	   //
	   // Use: internal
	   
	public SoTypeList getListItemTypes(int thePartNumber) {
	     // return the type of the entry, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].getListItemTypes();
		       else
		           return ( emptyList );
		  
	}
	
	public boolean isPublic(int thePartNumber) {
	     // return whether the part is public, if you can find it.
		       if ( thePartNumber >= 0 && thePartNumber < numEntries )
		           return entries[thePartNumber].isPublic();
		       else
		           return true;
		  		
	}
	
	/**
	 * used by SoNodekitParts to search through catalogs. 
	 * recursively search a given part for 'name to find' in the templates of 
	 * that entry and its descendants 
	 * 
	 * 
	 * @param partNumber
	 * @param nameTofind
	 * @param typesChecked
	 * @return
	 */
	public boolean recursiveSearch(int partNumber, SbName nameToFind, final SoTypeList typesChecked) {
		
	     // just call the recursive search method on the given entry...
		      return( entries[partNumber].recursiveSearch( nameToFind, typesChecked ));
		 	}
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    Initializes static variables.
	   //
	   // Use: public
	   
	  public static void
	   initClass()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       emptyName = new SbName("");
	       emptyList = new SoTypeList();
	       badType   = new SoType();
	       badType.copyFrom(SoType.badType());
	   }
	   	
}
