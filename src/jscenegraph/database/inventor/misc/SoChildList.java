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
 |      This file contains the definition of the extender SoChildList
 |      class.
 |        
 |   Author(s)          : Paul S. Strauss
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.misc;

import jscenegraph.database.inventor.SbPList;
import jscenegraph.database.inventor.SoNodeList;
import jscenegraph.database.inventor.SoPath;
import jscenegraph.database.inventor.actions.SoAction;
import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.port.Destroyable;


///////////////////////////////////////////////////////////////////////////////
///
////\class SoChildList
///
///  This class maintains a list of child nodes for any node. It allows
///  children to be added, removed, replaced, and accessed. The SoGroup
///  class has an instance of this class and provides public methods
///  for dealing with children. Other classes can also have an instance
///  of this class and may choose to publicize whatever methods they
///  wish for dealing with them. The SoNode::getChildren() method
///  returns the child list for any node class.
///
///  SoChildList automatically maintains the auditor list for parent
///  nodes. That is, when a child is added to the list, the parent node
///  is automatically added as a parent auditor of the child. To make
///  this possible, the parent node must be passed in to the
///  constructors of the SoChildList class.
///
//////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoChildList extends SoNodeList implements Destroyable {
	
   private  SoNode              parent;
	private final SbPList auditors = new SbPList();
	
	public SoChildList(SoNode parentNode) {
		super();
		parent = parentNode;
	}
	
	 ////////////////////////////////////////////////////////////////////////
	    //
	    // Description:
	    //    Constructor.
	    //
	    // Use: public
	    
	   public SoChildList(SoNode parentNode, int size)
	    //
	    ////////////////////////////////////////////////////////////////////////
	    {
	    	super(size);
	    
	        parent = parentNode;
	    }
	    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Constructor.
//
// Use: public

public SoChildList(SoNode parentNode, final SoChildList l) {
        super(l);
//
////////////////////////////////////////////////////////////////////////

    parent = parentNode;
}

public void destructor() {
	truncate(0);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Adds a child to the end of the list.
//
// Use: public

public void
append(SoNode child)
//
////////////////////////////////////////////////////////////////////////
{
    super.append(child);

    // Express interest by parent in child's modification
    child.addAuditor(parent, SoNotRec.Type.PARENT);

    // the parent has changed; notify its auditors
    parent.startNotify();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Insert given child into list before child with given index
//
// Use: public

public void
insert(SoNode child, int addBefore)
//
////////////////////////////////////////////////////////////////////////
{
    int i;

    super.insert(child, addBefore);

    // If any paths go through the parent, make sure they get updated
    for (i = 0; i < auditors.getLength(); i++)
        ((SoPath )auditors.operator_square_bracket(i)).insertIndex(parent, addBefore);

    // Express interest by parent in child's modification
    child.addAuditor(parent, SoNotRec.Type.PARENT);

    // the parent has changed; notify its auditors
    parent.startNotify();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Removes child with given index.
//
// Use: public

public void
remove(int which)
//
////////////////////////////////////////////////////////////////////////
{
    int i;

    // Remove interest of parent in child
    (this).operator_square_bracket(which).removeAuditor(parent, SoNotRec.Type.PARENT);

    // If any paths go through the parent, make sure they get updated
    for (i = 0; i < auditors.getLength(); i++)
        ((SoPath )auditors.operator_square_bracket(i)).removeIndex(parent, which);

    super.remove(which);

    // the parent has changed; notify its auditors
    parent.startNotify();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Removes all children from one with given index on, inclusive.
//
// Use: public

public void
truncate(int start)
//
////////////////////////////////////////////////////////////////////////
{
    int which;

    // Remove children from right to left. This allows the indices to
    // remain correct during the removal and also keeps the paths that
    // audit the group from doing too much work. (A path has to adjust
    // indices if we remove a child before the one in the path.) It
    // also minimizes shuffling of entries in the children array.

    for (which = getLength() - 1; which >= start; --which) {
        int i;

        // Remove interest of parent in child
        (this).operator_square_bracket(which).removeAuditor(parent, SoNotRec.Type.PARENT);

        // If any paths go through the parent, make sure they get updated
        for (i = 0; i < auditors.getLength(); i++)
            ((SoPath )auditors.operator_square_bracket(i)).removeIndex(parent, which);

        super.remove(which);
    }

    // the parent has changed; notify its auditors
    parent.startNotify();
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Copies a list of children.
//
// Use: public

public void
copy(final SoChildList cList)
//
////////////////////////////////////////////////////////////////////////
{
    int i;

    // Truncate to get rid of old children
    truncate(0);

    // Copy children
    super.copy(cList);

    // Express parent's interest in all children
    for (i = 0; i < getLength(); i++)
        (this).operator_square_bracket(i).addAuditor(parent, SoNotRec.Type.PARENT);

    // the parent has changed; notify its auditors
    parent.startNotify();
}


	public void set(int which, SoNode child) {
	     int i;
	      
	          // Remove interest of parent in old child
	          (this).operator_square_bracket(which).removeAuditor(parent, SoNotRec.Type.PARENT);
	      
	          // If any paths go through the parent, make sure they get updated
	          for (i = 0; i < auditors.getLength(); i++)
	              ((SoPath )auditors.operator_square_bracket(i)).replaceIndex(parent, which, child);
	      
	          // Replace child
	          super.set(which, child);
	      
	          // Express parent's interest in new child
	          child.addAuditor(parent, SoNotRec.Type.PARENT);
	      
	          // the parent has changed; notify its auditors
	          parent.startNotify();
	     		
	}

	// SoPath calls these to be notified of changes in scene graph topology: 
	public void addPathAuditor(SoPath p) {
		auditors.append(p);
	}
	
	public void removePathAuditor(SoPath p) {
		auditors.remove(auditors.find(p));
	}

    //! Traverses all children to apply action. Stops if action's
      //! termination condition is reached
 public     void                traverse(SoAction action)
          { traverse(action, 0, getLength() - 1); }
  
      //! Traverses just one child
public      void                traverse(SoAction action, int childIndex)
          { traverse(action, childIndex, childIndex); }
  
      //! Traverses all children between two indices, inclusive. Stops if
      //! action's termination condition is reached.
public      void                traverse(SoAction action,
                                   int firstChild, int lastChild)
//
 ////////////////////////////////////////////////////////////////////////
 {
     int i;
 
     // Optimization:  If the node this childList is part of is not in
     // the path, then the path code can't change as we traverse its
     // children, and we can use a more efficient traversal:
     SoAction.PathCode  pc = action.getCurPathCode();
 
     if (pc == SoAction.PathCode.NO_PATH || pc == SoAction.PathCode.BELOW_PATH) {
         //begin by pushing nonexistent child:
         action.pushCurPath();
         for (i = firstChild; i <= lastChild; i++) {
             SoNode child = (this).operator_square_bracket(i);
             // Now pop and push in one move:
             action.popPushCurPath(i);
             action.traverse(child);
 
             // Stop if action has reached termination condition. (For
             // example, search has found what it was looking for, or event
             // was handled.)
             if (action.hasTerminated())
                 break;
         }
         action.popCurPath();
     }
     else {
         for (i = firstChild; i <= lastChild; i++) {
             SoNode      child = (this).operator_square_bracket(i);
 
             // Never traverse a child that does not affect the state
             // if we are off the path
             if (pc == SoAction.PathCode.OFF_PATH && ! child.affectsState())
                 continue;
 
             // If we are in the path, don't traverse a child that does
             // not affect the state if it is not on a path
             action.pushCurPath(i);
 
             // If action code is now OFF_PATH, we know that the child
             // was not on a path. Don't traverse it if it does not
             // affect the state
             if (action.getCurPathCode() != SoAction.PathCode.OFF_PATH ||
                 child.affectsState())
                 action.traverse(child);
 
             action.popCurPath(pc);
 
             // Stop if action has reached termination condition. (For
             // example, search has found what it was looking for, or event
             // was handled.)
             if (action.hasTerminated())
                 break;
         }
     }
 }
 }
