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
 * Copyright (C) 1990,91,92   Silicon Graphics, Inc.
 *
 _______________________________________________________________________
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 |
 |   $Revision: 1.1.1.1 $
 |
 |   Description:
 |      Definition of SoBase, the base class for several other Inventor
 |      classes. This class handles reference counting and
 |      notification and is the main entry point for reading and
 |      writing derived classes.
 |
 |   Author(s)          : Paul S. Strauss, Nick Thompson, Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.misc;

import jscenegraph.database.inventor.SbDict;
import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SbPList;
import jscenegraph.database.inventor.SoBaseList;
import jscenegraph.database.inventor.SoDB;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.sensors.SoDataSensor;
import jscenegraph.port.Destroyable;


////////////////////////////////////////////////////////////////////////////////
//! Base class for all nodes, paths, and engines.
/*!
\class SoBase
\ingroup General
Abstract base class for Inventor node, path, and engine classes. This
class handles reference counting, notification, and naming.

\par See Also
\par
SoFieldContainer, SoNode, SoPath, SoEngine, SoDB
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public abstract class SoBase implements Destroyable {
	
	private static SoType classTypeId;
	
	private int refCount;
	
	private final SoAuditorList auditors = new SoAuditorList();
	
	private class WriteStuff {
		int writeCounter;
		boolean hasName;
		boolean multWriteRef;
		boolean writeRefFromField;
	}
	private final WriteStuff writeStuff = new WriteStuff();
	
	private static SbDict nameObjDict;
	
	private static SbDict objNameDict;
	
	public static SoType getClassTypeId() { return new SoType(classTypeId); }
	
	// This speed up reading a little:
	private static SbName globalFieldName;

	// Setup type information. 
	 ////////////////////////////////////////////////////////////////////////
	    //
	   // Description:
	   //    Setup SoBase's type info, and the global name dictionary.
	   //
	   // Use: public
	   
	  	public static void initClass() {
	  		
	  		if(classTypeId != null) {
	  			throw new IllegalStateException("SoBase already initialized");
	  		}
	  		
	     classTypeId = SoType.createType(SoType.badType(), new SbName("Base"));
	      
	          // Set up global name dictionaries
	          nameObjDict = new SbDict();
	          objNameDict = new SbDict();
	      
	          globalFieldName = new SbName("GlobalField");
	     		
	}
	  	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Constructor for SoBase.
//
// Use: protected

protected SoBase()
//
////////////////////////////////////////////////////////////////////////
{
    refCount = 0;

    writeStuff.hasName = false;
    writeStuff.writeCounter = 0;
    writeStuff.writeRefFromField = false;

//#ifdef DEBUG
//    if (traceRefs)
//        SoDebugError::postInfo("SoBase::SoBase",
//                               "for %#x", (const void *) this);
//#endif /* DEBUG */
}

	  	

	// Returns the type identifier for a specific instance.
	public abstract SoType getTypeId();
	
	/**
	 * Returns TRUE if this object is of the type specified in type 
	 * or is derived from that type. 
	 * Otherwise, it returns FALSE.
	 *  
	 * For example,
	 *  
	 * nodePtr->isOfType(SoGroup::getClassTypeId()) returns TRUE 
	 * if nodePtr is an instance of SoGroup or one of its subclasses. 
	 * 
	 * @param type
	 * @return
	 */
	public boolean isOfType(SoType type) {
		return getTypeId().isDerivedFrom(type);
	}
	
	/**
	 * Returns the name of an instance. 
	 * If the instance has not been named, an empty SbName is returned. 
	 * Objects that are named can be looked up using 
	 * the getByName() methods of SoNode, SoEngine, or SoPath. 
	 * 
	 * @return
	 */
	public SbName getName() {
		 Object[] n = new Object[1];
		  
		   if (!writeStuff.hasName) return new SbName("");
		   if (!objNameDict.find(this, n)) {
//		  #ifdef DEBUG
//		   SoDebugError::post("SoBase::getName",
//		   "hasName is TRUE, but couldn't find name!\n");
//		  #endif
		   return new SbName("");
		   }
		   return new SbName((String)n[0]); // java port
		  
	}
	
	/**
	 * Sets the name of an instance. 
	 * Object names are preserved when objects are written to 
	 * or read from files. 
	 * Object names must not begin with a digit, 
	 * and must not contain spaces or control characters, 
	 * single or double quote characters, backslashes, 
	 * curly braces or the plus character. 
	 * The isBaseNameChar() and isBaseNameStartChar() methods 
	 * of SbName can be used to validate names input by users. 
	 * This method will replace any bad charaters in the name 
	 * with underscore characters, and will print out an 
	 * error message if the application is using the Inventor 
	 * debugging library. 
	 * 
	 * @param name
	 */
	// java port
	public void setName(String newName) {
		setName(new SbName(newName));
	}
	public void setName(SbName newName) {
	    //Following 4 lines just do what getName() would do, repeating that code
	    //here fixes a thread-safe bug
	    Object[] n = new Object[1];
	    SbName oldName = new SbName("");
	    if ((writeStuff.hasName) && (objNameDict.find(this, n)))
	        oldName = new SbName((String)n[0]); 
	    
	    if (oldName.getLength() != 0)
	        removeName(this, oldName.getString());

	    // Empty name: leave unnamed.
	    if (newName.getLength() == 0) return;

	    // Make sure name is legal
	    String str = newName.getString();
	    boolean isBad = false;

	    // Check for beginning-with-a-number:
	    if (!SbName.isBaseNameStartChar(str.charAt(0))) isBad = true;

	    int i;
	    for (i = 1; i < newName.getLength() && !isBad; i++) {
	        isBad = !SbName.isBaseNameChar(str.charAt(i));
	    }

	    if (!isBad) {
	        addName(this, str);
	    }
	    else {
	        // Replace bad characters with underscores
	        String goodString = "";

	        // Prepend underscore if name begins with number:
	        if (!SbName.isBaseNameStartChar(str.charAt(0))) {
	            goodString += "_";
	        }
	        for (i = 0; i < newName.getLength(); i++) {
	            // Ugly little hack so we can use SbString's += operator,
	            // which doesn't do char's (only char *'s):
	            char[] temp = new char[2];
	            temp[0] = str.charAt(i); temp[1] = '\0';
	            if (!SbName.isBaseNameChar(str.charAt(i)))
	                goodString += "_";
	            else
	                goodString += temp;
	        }
//	#ifdef DEBUG
//	        SoDebugError::post("SoBase::setName", "Bad characters in"
//	                           " name '%s'.  Replacing with name '%s'",
//	                           str, goodString.getString());
//	#endif       
	        // MUST create an SbName here to create persistent storage for
	        // the name.
	        SbName goodName = new SbName(goodString);
	        addName(this, goodName.getString());
	    }
		
	}
	
	
	
	/**
	 * Initiates notification from an instance. 
	 * The default method does nothing, because some classes (path, sensor) 
	 * never initiate notification. 
	 * This is used by touch(). 
	 */
	public void startNotify() {
		  final SoNotRec rec = new SoNotRec(this);
		    final SoNotList list = new SoNotList();
		   
		    // Indicate to the database that a notification is in progress
		    SoDB.startNotify();
		   
		    // Assume the notification type is CONTAINER. This is a safe bet
		    // for now. If it is another type, it will be changed
		    // appropriately later on. We need to set it to something so that
		    // SoFieldContainer will have some deterministic way to act.
		    rec.setType(SoNotRec.Type.CONTAINER);
		   
		    list.append(rec);
		    notify(list);
		   
		    // Indicate to the database that the notification has completed
		    SoDB.endNotify();
	}

	/**
	 * Propagates modification notification through an instance. The default
	 * method here does not create and add a new record. It merely propagates
	 * the current record list to all auditors. This method may be used by
	 * subclasses to do the propagation after modifying the list appropriately.
	 * 
	 * @param list
	 */
	public void notify(SoNotList list) {
		auditors.notify(list);
	}
	
	// Adds/removes an auditor to/from list. 
	public void addAuditor(Object auditor, SoNotRec.Type type) {
	     auditors.append(auditor, type);
	}
	
	// Adds/removes an auditor to/from list. 
	public void removeAuditor(Object auditor, SoNotRec.Type type) {
	     int audIndex = auditors.find(auditor, type);
	      
	      //#ifdef DEBUG
	          if (audIndex < 0) {
	              SoDebugError.post("SoBase::removeAuditor",
	                                 "can't find auditor "+auditor+"\n");
	              return;
	          }
	      //#endif /* DEBUG */
	      
	          auditors.remove(audIndex);
	     		
	}
	
    //! Returns auditor list-- used by SoField and SoEngineOutput to
    //! trace forward connections
    SoAuditorList getAuditors() { return auditors; }

	
	/**
	 * Adds and removes a reference to an instance. 
	 * Instances should be referenced when they will be used outside of 
	 * the routine in which they were initialized. 
	 * (A typical example of this is maintaining a pointer to the root of 
	 * a graph.) Whenever the reference count for an instance is decremented 
	 * to 0, the instance is automatically destroyed by the database 
	 * (unless unrefNoDelete() is used to unref it). 
	 * The reference count of a node is automatically incremented when the 
	 * node is added as a child of another node or when a path points to the 
	 * node. Likewise, the reference count is automatically decremented when 
	 * the node is removed as a child or when a path that points to the node 
	 * is changed or destroyed. 
	 * 
	 * unrefNoDelete() should be called when it is desired to decrement the 
	 * reference count, but not delete the instance if this brings the 
	 * reference count to zero. This is most useful in returning an object 
	 * to a zero-reference-count state, like it was when it was created by 
	 * new. 
	 * 
	 */
	public void ref() {
		  // This generates a C++ warning.
		  ((SoBase ) this).refCount++;
		 
//		 #ifdef DEBUG
//		  if (traceRefs)
//		  SoDebugError::postInfo("SoBase::ref",
//		  "refCount for %#x + => %2d",
//		  (const void *) this, refCount);
//		 #endif /* DEBUG */
				
	}
	
	public void unref() {
		refCount--;
		if(refCount == 0) {
			destroy();
		}
	}
	
	public void unrefNoDelete() {
	     // This generates a C++ warning.
		       SoBase      base = (SoBase ) this;
		   
		   //#ifdef DEBUG
		       if (base.refCount <= 0)
		           SoDebugError.postWarning("SoBase::unrefNoDelete",
		                                     "instance has reference count <= 0 already");
		   
//		       if (traceRefs)
//		           SoDebugError.postInfo("SoBase::unrefNoDelete",
//		                                  "refCount for "+this+" - => %2d"+ (refCount - 1));
		   //#endif /* DEBUG */
		       base.refCount--;
		  		
	}
	
	/**
	 * Marks an instance as modified, simulating a change to it. 
	 * This will notify auditors (parent nodes, connected engines, 
	 * and so on) of a change to this object and cause attached 
	 * sensors to be triggered. 
	 * 
	 */
	public void touch() {
		startNotify();
	}
	
	// Returns current reference count. 
	public int getRefCount() {
		 return refCount; 
	}
	
	// Actually deletes an instance. 
	// Allows subclasses to do other stuff before the deletion if necessary. 
	protected void destroy() {
		  // If there are any auditors left, give them a chance to clean up.
		  // The only type of auditors that can be attached to a base with a
		  // zero ref count (which is why we are here) are sensors, which
		  // are given a chance to detach themselves.
		  //
		  // NOTE: The implementation of dyingReference() for sensors may
		  // require this base instance to be valid. Therefore, we cannot do
		  // this stuff in the destructor, or it would be too late.
		 
		  for (int i = auditors.getLength() - 1; i >= 0; i--) {
		 
		  switch (auditors.getType(i)) {
		  case SENSOR:
		  // Tell sensor that we are going away
		  ((SoDataSensor ) auditors.getObject(i)).dyingReference();
		 
		  // The call to dyingReference() might remove auditors,
		  // shortening the auditors list; make sure we're not
		  // trying to access past the end.
		  if (i > auditors.getLength())
		  i = auditors.getLength();
		  break;
		 
		  default:
		  SoDebugError.post("(internal) SoBase::destroy",
		  "Got an auditor of type "+ auditors.getType(i));
		  break;
		  }
		  }
		 
		 //#if 1
		 
		  //delete this; java port
			destructor();
		 
		 //#else
			}
	
	// Virtual destructor so that subclasses are deleted properly. 
	public void destructor() {		
		  SbName myName = getName();
		    if (myName.operator_not_equal(""))
		    	removeName(this, myName.getString());		   
	}
	
	// Internal methods used to maintain the global name dictionary. 
	public static void addName(SoBase b, String name) {
		
	     SbPList list;
	          final Object[] t = new Object[1];
	      
	          b.writeStuff.hasName = true;
	      
	          // Look for name:
	          if (!nameObjDict.find(name, t)) {
	              // If not found, create a BaseList and enter it in the
	              // dictionary
	              list = new SbPList();
	              nameObjDict.enter(name, list);
	          } else {
	              list = (SbPList )t[0];
	          }
	      
//	      #ifdef DEBUG
	          // Make sure it isn't already on the list
	          if (list.find(b) != -1)
	              SoDebugError.post("SoBase::addName",
	                                 "Base "+b+"%x with name \""+name+"\" is already in dictionary");
//	      #endif
	      
	          // Add name to the list:
	          list.append(b);
	      
	          // And append to the objName dictionary:
	          objNameDict.enter(b, name);
	     	}
	
	//
	 // Description:
	 // Removes a name/instance pair from the global dictionary.
	 //
	 // Use: internal
	 
	public static void removeName(SoBase b, String name) {
		
		  SbPList list;
		    boolean found;
		    Object[] t = new Object[1];
		    int i;
		   
		    b.writeStuff.hasName = false;
		   
		    // Look for name list
		    found = nameObjDict.find( name, t);
		   
		    // Look for name within list
		    if (found) {
		    list = (SbPList ) t[0];
		    i = list.find(b);
		   
		    if (i < 0)
		    found = false;
		   
		    else
		    list.remove(i);
		    }
		   
		    // And remove from objName dict:
		    found |= objNameDict.remove(b);
		   
//		   #ifdef DEBUG
		    if (! found)
		    SoDebugError.post("SoBase::removeName",
		    "Name \""+name+"\" (base "+b+") is not in dictionary");
//		   #endif
		   
		    return;
		  
		  }
	

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Internal routines used by node/path/function getByName routines.
//
// Use: internal, static

public SoBase 
getNamedBase(final SbName name, final SoType type)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    if (nameObjDict == null) {
        SoDebugError.post("SoBase::getName",
                           "SoDBinit() has not yet been called");
        return null;
    }
//#endif

    SbPList list;
    final Object[] t = new Object[1];
    // Lookup the name in the dictionary
    if (! nameObjDict.find(name.getString(), t))
        return null;
    list = (SbPList )t[0];

    // Search backwards through the list.  Return the last item of the
    // appropriate type.
    for (int i = list.getLength()-1; i >= 0; i--) {
        SoBase b = (SoBase )(list).operator_square_bracket(i);
        if (b.isOfType(type)) return b;
    }
    return null;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Internal routine used by the get() routines above.  Returns the
//    number of items added to the result list.
//
// Use: internal, static

public int
getNamedBases(final SbName name, final SoBaseList result, final SoType type)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    if (nameObjDict == null) {
        SoDebugError.post("SoBase::getName",
                           "SoDBinit() has not yet been called");
        return 0;
    }
//#endif

    int numAdded = 0;
    SbPList list;

    final Object[] t = new Object[1];
    // Lookup the name in the dictionary
    if (! nameObjDict.find(name.getString(), t))
        return 0;

    list = (SbPList )t[0];
    // Search backwards through the list.  Add all items of the
    // appropriate type to the result list.
    for (int i = list.getLength()-1; i >= 0; i--) {
        SoBase b = (SoBase )(list).operator_square_bracket(i);
        if (b.isOfType(type)) {
            result.append(b);
            numAdded++;
        }
    }
    return numAdded;
}

	
}
