/**
 * 
 */
package jscenegraph.database.inventor;

import jscenegraph.port.FILE;

/**
 * @author Yves Boyadjian
 *
 */
public class SoInput {
	
		   
	// Sets an in-memory buffer to read from, along with its size. 
	public void setBuffer(Object buffPointer, int bufSize) {
		//TODO
	}
	
	// Init function sets up global directory list. 
	public static void init() {
		
	      	}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Opens named file, sets file pointer to result. If it can't open
//    the file, it returns FALSE. If okIfNotFound is FALSE (the
//    default), it prints an error message if the file could not be
//    found.
//
// Use: public

	public boolean	openFile(String fileName, boolean okIfNotFound)
	//
	////////////////////////////////////////////////////////////////////////
	{
		//TODO
		
		return false;
	}
	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns pointer to current file, or NULL if reading from buffer.
//
// Use: public

public FILE getCurFile() 
//
////////////////////////////////////////////////////////////////////////
{
	//TODO
	
    return null;//fromBuffer() ? null : curFile.fp;
}

	
}
