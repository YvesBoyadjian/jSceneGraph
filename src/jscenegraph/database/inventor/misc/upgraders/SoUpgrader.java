/**
 * 
 */
package jscenegraph.database.inventor.misc.upgraders;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SoInput;
import jscenegraph.database.inventor.misc.SoBase;
import jscenegraph.database.inventor.nodes.SoGroup;

/**
 * @author Yves Boyadjian
 *
 */
public class SoUpgrader extends SoGroup {

	  public
		    //! Find out if an upgrader exists for a specific class and a
		    //! specific version of the file format.
		    static SoUpgrader   getUpgrader(final SbName className,
		                                     float fileFormatVersion) {
		  return null; //TODO
	  }

	    //! This is the key method that reads in fields, calls the
	    //! createNewNode() method (which is responsible for looking at the
	    //! fields read and setting the appropriate fields in the new
	    //! node), and then reads in and adds children to the new node if
	    //! it is derived from SoGroup.
	    public boolean        upgrade(SoInput in, final SbName refName,
	                                final SoBase[] result) {
	    	return false ; //TODO
	    }

}
