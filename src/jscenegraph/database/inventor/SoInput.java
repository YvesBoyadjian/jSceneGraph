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
 |      This file contains the definition of the SoInput class.
 |
 |   Classes:
 |      SoInput, SoInputFile (internal)
 |
 |   Author(s)          : Paul S. Strauss
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor;

import java.nio.ByteBuffer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import jscenegraph.database.inventor.errors.SoReadError;
import jscenegraph.port.FILE;

/**
 * @author Yves Boyadjian
 *
 */

////////////////////////////////////////////////////////////////////////////////
//! Used to read Inventor data files.
/*!
\class SoInput
\ingroup General
This class is used by the SoDB reading routines when reading
Inventor data files.  It supports both ASCII (default) and binary
Inventor formats.  Users can also register additional
valid file headers. 
When reading, SoInput skips over Inventor comments 
(from '#' to end of
line) and can stack input files. When EOF is reached, the stack is
popped.  This class can also be used to read from a buffer in memory.

\par See Also
\par
SoDB, SoOutput, SoTranReceiver
*/
////////////////////////////////////////////////////////////////////////////////

public class SoInput {
	
    private static SbStringList directories;   //!< Directory search path.
    private final SbPList             files = new SbPList();          //!< Stack of SoInputFiles (depth >=1)
    private SoInputFile  curFile;       //!< Top of stack
    private String            backBuf;        //!< For strings that are put back
    private int                 backBufIndex;   //!< Index into backBuf (-1 if no buf)

    private ByteBuffer                tmpBuffer;     //!< Buffer for binary read from file
    private int                curTmpBuf;     //!< Current location in temporary buffer
    private long              tmpBufSize;     //!< Size of temporary buffer

    private char[]                backupBuf = new char[8];   //!< Buffer for storing data that
                                        //! has been read but can't be put back.
    private boolean                backupBufUsed;  //!< True if backupBuf contains data
    
		   

////////////////////////////////////////////////////////////////////////
//
//Description:
//Sets up buffer to read from and its size.
//
//Use: public

	// Sets an in-memory buffer to read from, along with its size. 
	public void setBuffer(String bufPointer, int bufSize) {
    // Close open files, if any
    closeFile();

    // Initialize reading from buffer
    curFile.name       = "<user-defined buffer in memory>";
    curFile.fp         = null;
    curFile.buffer     = bufPointer;
    curFile.curBuf     = /*(char *) bufPointer*/0; // java port
    curFile.bufSize    = bufSize;
    curFile.lineNum    = 1;
    curFile.openedHere = false;

    // Start with a fresh dictionary
    if (curFile.refDict != null && ! curFile.borrowedDict)
        curFile.refDict.clear();
    else
        curFile.refDict = new SbDict();

    // Assume file is ASCII until header is checked
    curFile.binary     = false;
    curFile.readHeader = false;
    curFile.headerOk   = true;
    curFile.ivVersion  = 0.f;
    curFile.headerString = "";

    // Delete the temporary buffer if it has been allocated
    if (tmpBuffer != null) {
        //free (tmpBuffer); java port
        tmpBuffer = null;
        tmpBufSize = 0;
    }
	}
	
	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Closes current files that were opened by SoInput. It does
//    nothing to files that were not opened here, so it's always safe
//    to call. This also removes all of the files from the stack and
//    resets things to read from stdin.
//
// Use: public

public void closeFile()
//
////////////////////////////////////////////////////////////////////////
{
    int                 i;
    SoInputFile  f;

    // Close all files opened here
    for (i = 0; i < files.getLength(); i++) {
        f = ( SoInputFile ) files.operator_square_bracket(i);

        if (f.openedHere)
            FILE.fclose(f.fp);

        // Free up storage used for all but topmost file
        if (i > 0) {
            f.refDict.destructor();
            //delete f; java port
        }
    }

    // Remove all but the first file from the stack
    if (files.getLength() > 1)
        files.truncate(1);

    // Reset to read from stdin again
    initFile(new FILE(System.in), "<stdin>", null, false);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Constructor - sets input to stdin by default.
//
// Use: public

public SoInput()
//
////////////////////////////////////////////////////////////////////////
{
    // Create new file and push on stack
    curFile = new SoInputFile();
    files.append((Object) curFile);

    // Make it read from stdin
    initFile(new FILE(System.in), "<stdin>", null, false);

    backBufIndex = -1;  // No buffer

    tmpBuffer = null;
    tmpBufSize = 0;

    backupBufUsed = false;
}


////////////////////////////////////////////////////////////////////////
//
//Description:
//Destructor - closes all input files opened by SoInput.
//
//Use: public

public void destructor() {
    closeFile();

    // closeFile() leaves the topmost file on the stack, so delete it
    if (curFile.refDict != null && ! curFile.borrowedDict)
        curFile.refDict.destructor();
    curFile.destructor();

    if (tmpBuffer != null) {
        //free (tmpBuffer); java port
        tmpBuffer = null;
        tmpBufSize = 0;
    }
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initializes reading from file pointer. Just sets up some variables.
//
// Use: private

//! Initializes reading from file
// java port
private void                initFile(FILE newFP, String fileName,
                             final String[] fullName, boolean openedHere) {
	initFile(newFP, fileName, fullName, openedHere, null);
}

private void initFile(FILE newFP,          // New file pointer
                  String fileName, // Name of new file to read
                  final String[] fullName,   // Full name of new file
                  boolean openedHere,    // true if SoInput opened file
                  SbDict refDict)      // Dictionary of base references
                                        // (default is null: create new dict)
//
////////////////////////////////////////////////////////////////////////
{
    curFile.name       = fileName;
    if (fullName == null)
        curFile.fullName = fileName;
    else
        curFile.fullName = fullName[0];
    curFile.fp         = newFP;
    curFile.buffer     = null;
    curFile.lineNum    = 1;
    curFile.openedHere = openedHere;

    if (refDict == null) {
        // Start with a fresh dictionary
        if (curFile.refDict != null && ! curFile.borrowedDict)
            curFile.refDict.clear();
        else
            curFile.refDict = new SbDict();
        curFile.borrowedDict = false;
    }
    else {
        if (curFile.refDict != null && ! curFile.borrowedDict)
            curFile.refDict.destructor();
        curFile.refDict = refDict;
        curFile.borrowedDict = true;
    }

    // Assume file is ASCII until header is checked
    curFile.binary     = false;
    curFile.readHeader = false;
    curFile.headerOk   = true;
}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Init routine-- called by SoDB::init, it sets up the list of
//    directories to search in.
//
// Use: internal, static

	// Init function sets up global directory list. 
	public static void init() {
	    directories = new SbStringList();

	    // Default directory search path is current directory
	    directories.append(new String("."));		
	      	}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Opens named file, sets file pointer to result. If it can't open
//    the file, it returns false. If okIfNotFound is false (the
//    default), it prints an error message if the file could not be
//    found.
//
// Use: public

	public boolean	openFile(String fileName, boolean okIfNotFound)
	//
	////////////////////////////////////////////////////////////////////////
	{
    FILE newFP = null;
    final String[]    fullName = new String[1];

    if (fileName != null && !fileName.isEmpty()) {
        newFP = findFile(fileName, fullName);
    }

    if (newFP == null) {
        if (! okIfNotFound)
            SoReadError.post(this,
                              "Can't open file \""+fileName+"\" for reading");
        return false;
    }

    // Close open files, if any
    closeFile();

    // Initialize reading from file
    initFile(newFP, fileName, fullName, true);

    if (tmpBuffer == null) {
        tmpBuffer = ByteBuffer.allocateDirect(64);
        tmpBufSize = 64;
        curTmpBuf = 0;//(char *)tmpBuffer; java port
    }

    return true;
	}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Fills in passed string to contain description of current
//    location in all open input files.
//
// Use: internal

public void getLocationString(final String[] string)
//
////////////////////////////////////////////////////////////////////////
{
    SoInputFile    f;
    int                         i = files.getLength() - 1;
    String buf;

    string[0] = "";

    f = (SoInputFile ) files.operator_square_bracket(i);
    buf = "\tOccurred at line "+f.lineNum+" in "+ f.fullName;
    string[0] = buf;

    for (--i ; i >= 0; --i) {
        f = ( SoInputFile ) files.operator_square_bracket(i);
        buf = "\n\tIncluded at line "+f.lineNum+" in "+ f.fullName;
        string[0] += buf;
    }
}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Opens named file, Returns a file pointer to it. If the file name
//    is not absolute, it checks all of the current directories in the
//    search path. It returns the full name of the file it found in
//    the fullName parameter. Returns null on error.
//
// Use: public

public FILE findFile(String fileName, final String[] fullName)
//
////////////////////////////////////////////////////////////////////////
{
    FILE        fp;
    int         i;
    
    FileSystem fileSystem = FileSystems.getDefault();
    Path fileNamePath = fileSystem.getPath(fileName); 

    // If filename is absolute
    if(fileNamePath.isAbsolute()) {
    	fullName[0] = fileName;
    	fp = FILE.fopen(fileName, "r");
    }

    // For relative file names, try each of the directories in the search path
    else {
        fp = null;

        for (i = 0; i < directories.getLength(); i++) {
            fullName[0] = (String)directories.operator_square_bracket(i);
            fullName[0] = fileSystem.getPath(fullName[0], fileName).toString();
            fp = FILE.fopen(fullName[0], "r");
            if (fp != null)
                break;
        }
    }   

    return fp;
}

	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns pointer to current file, or null if reading from buffer.
//
// Use: public

public FILE getCurFile() 
//
////////////////////////////////////////////////////////////////////////
{
	return fromBuffer() ? null : curFile.fp;
}

    //! Returns TRUE if reading from memory buffer rather than file
    public boolean                fromBuffer() 
        { return (curFile.buffer != null); }

}
