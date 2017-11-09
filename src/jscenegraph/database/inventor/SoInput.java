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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import jscenegraph.database.inventor.SoDB.SoDBHeaderCB;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.errors.SoReadError;
import jscenegraph.database.inventor.misc.SoBase;
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
	
	private static final char COMMENT_CHAR ='#';
	private static final int READ_STRINGBUFFER_SIZE = 64;

	private int SB_MAX(int a, int b) { return ((a) > (b)) ? (a) : (b);}

	
	
    private static SbStringList directories;   //!< Directory search path.
    private final SbPList             files = new SbPList();          //!< Stack of SoInputFiles (depth >=1)
    public SoInputFile  curFile;       //!< Top of stack
    private String            backBuf;        //!< For strings that are put back
    private int                 backBufIndex;   //!< Index into backBuf (-1 if no buf)

    private byte[]                tmpBuffer;     //!< Buffer for binary read from file
    private int                curTmpBuf;     //!< Current location in temporary buffer
    private int              tmpBufSize;     //!< Size of temporary buffer

    private byte[]                backupBuf = new byte[8];   //!< Buffer for storing data that
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
// Description:
//    Constructor that gets reference dictionary from another SoInput.
//
// Use: internal

public SoInput(SoInput dictIn)
//
////////////////////////////////////////////////////////////////////////
{
    // Create new file and push on stack
    curFile = new SoInputFile();
    files.append( curFile);

    // Make it read from stdin and use the passed dictionary
    initFile(new FILE(System.in), "<stdin>", null, false,
             (dictIn == null ? null : dictIn.curFile.refDict));

    backBufIndex = -1;  // No buffer

    tmpBuffer = null;
    tmpBufSize = 0;
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
//    Adds a directory to beginning of list of directories to search to
//    find named files to open.
//
// Use: public, static

public static void addDirectoryFirst(String dirName)
//
////////////////////////////////////////////////////////////////////////
{
    directories.insert(new String(dirName), 0);
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
                             final String fullName, boolean openedHere) {
	initFile(newFP, fileName, fullName, openedHere, null);
}

private void initFile(FILE newFP,          // New file pointer
                  String fileName, // Name of new file to read
                  final String fullName,   // Full name of new file
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
        curFile.fullName = fullName;
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
// java port
	public boolean	openFile(String fileName) {
		return openFile(fileName,false);
	}
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
    initFile(newFP, fileName, fullName[0], true);

    if (tmpBuffer == null) {
        tmpBuffer = new byte[64];
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


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns full name of current file, or NULL if reading from buffer.
//
// Use: public

public String getCurFileName()
//
////////////////////////////////////////////////////////////////////////
{
    return fromBuffer() ? null : curFile.fullName;
}


    //! Returns true if reading from memory buffer rather than file
    public boolean                fromBuffer() 
        { return (curFile.buffer != null); }


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a character string SbName from current file/buffer. If the
//    character string begins with a double quote, this reads until a
//    matching close quote is found. Otherwise, it reads until the
//    next white space is found. This returns false on EOF.
//
//    If "validIdent" is true, this reads only strings that are valid
//    identifiers as defined in the SbName class. The default for this
//    is false. Identifiers may not be within quotes.
//
// Use: public

    //java port
public     boolean                read(final SbName         n) {
	return read(n, false);
}

public boolean read(SbName n,                // Name to read into
              boolean validIdent)        // true => name must be
                                        // identifier (default is false)
//
////////////////////////////////////////////////////////////////////////
{
    boolean        gotChar;

    if (! skipWhiteSpace())
        return false;

    // If binary input or not an identifer, read as just a regular string
    if (curFile.binary || ! validIdent) {
        final String[]        s = new String[1];

        if (! read(s)) {

            // We may have just discovered EOF when trying to read the
            // string. If so, and there's another file open on the
            // stack, call this method again to try reading from the
            // next file.

            if (curFile.binary && eof() && files.getLength() > 1)
                return read(n, validIdent);

            return false;
        }

        n.copyFrom(new SbName(s[0]));
    }

    else {
        // Read identifier, watching for validity
        final char[]    buf = new char[256];
        int b = 0; // java port
        final char[]    c = new char[1];

        if (fromBuffer()) {
            if ((gotChar = getASCIIBuffer(c)) && SbName.isIdentStartChar(c[0])) {
                buf[b] = c[0]; b++;
    
                while ((gotChar= getASCIIBuffer(c)) && SbName.isIdentChar(c[0])) {
                    // If the identifier is too long, it will be silently
                    // truncated.
                    if (b < 255) {
                        buf[b] = c[0]; b++;
                    }
                }
            }
        }
        else {
            if ((gotChar = getASCIIFile(c)) && SbName.isIdentStartChar(c[0])) {
                buf[b] = c[0]; b++;
    
                while ((gotChar = getASCIIFile(c)) && SbName.isIdentChar(c[0])) {
                    // If the identifier is too long, it will be silently
                    // truncated.
                    if (b < 255) {
                        buf[b] = c[0]; b++;
                    }
                }
            }
        }
        buf[b] = '\0';
        String bufStr = new String(buf,0,b); //java port

        // Put the terminating character (if any) back in the stream.
        if (gotChar)
            putBack(c[0]);

        n.copyFrom(new SbName(bufStr));
    }

    return true;
}

    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns true if current file/buffer is at EOF.
//
// Use: public

public boolean eof()
//
////////////////////////////////////////////////////////////////////////
{
    if (! fromBuffer())
        return FILE.feof(curFile.fp)!=0;
    else
        return (freeBytesInBuf() == 0);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads next character from current file/buffer. Returns false on EOF.
//
// Use: public

// java port
private boolean get(char[] bufStore, int buf) {
	char[] c= new char[1];
	boolean retCode = get(c);
	if(retCode) {
		bufStore[buf] = c[0];
	}
	return retCode;
}

public boolean get(final char[] c)
//
////////////////////////////////////////////////////////////////////////
{
    boolean ret;

    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        c[0] = backBuf.charAt(backBufIndex);backBufIndex++;

        if (c[0] != '\0')
            return true;

        // Back buffer ran out of characters
        backBuf = "";//.makeEmpty(); java port
        backBufIndex = -1;
    }

    if (! curFile.readHeader && ! checkHeader())
        return false;

    if (eof()) {
        c[0] = (char)FILE.EOF;
        ret = false;
    }

    else if (curFile.binary) {
        if (fromBuffer()) {
            c[0] = curFile.buffer.charAt(curFile.curBuf); curFile.curBuf++;
            curFile.curBuf++;
            curFile.curBuf++;
            curFile.curBuf++;
            ret = true;
        }
        else {
            final char[] pad = new char[3];
            long i = FILE.fread(c, /*sizeof(char)*/1, 1, curFile.fp);
            FILE.fread(pad, /*sizeof(char)*/1, 3, curFile.fp);
            ret = (i == 1) ? true : false;
        }
    }

    else {

        if (! fromBuffer()) {

            int i = FILE.getc(curFile.fp);

            if (i == FILE.EOF) {
                c[0] = (char)FILE.EOF;  // Set c to EOF so putpack(c) will fail 
                ret = false;
            }

            else {
                c[0] = (char) i;
                ret = true;
            }
        }

        else {
            c[0] = curFile.buffer.charAt(curFile.curBuf); curFile.curBuf++;
            ret = true;
        }
    }

    return ret;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads next ASCII character from current buffer. Returns false on EOF.
//
// Use: public

public boolean getASCIIBuffer(final char[] c)
//
////////////////////////////////////////////////////////////////////////
{
    boolean ret;

    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        c[0] = backBuf.charAt(backBufIndex); backBufIndex++;

        if (c[0] != '\0')
            return true;

        // Back buffer ran out of characters
        backBuf = ""; // java port
        backBufIndex = -1;
    }

    if (freeBytesInBuf() == 0) {
        c[0] = (char)FILE.EOF;
        ret = false;
    }
    else {
        c[0] = curFile.buffer.charAt(curFile.curBuf); curFile.curBuf++;
        ret = true;
    }

    return ret;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads next ASCII character from current file. Returns false on EOF.
//
// Use: public

public boolean getASCIIFile(final char[] c)
//
////////////////////////////////////////////////////////////////////////
{
    int         i;
    boolean        ret;

    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        c[0] = backBufIndex == backBuf.length()? 0 : backBuf.charAt(backBufIndex); backBufIndex++;

        if (c[0] != '\0')
            return true;

        // Back buffer ran out of characters
        backBuf ="";
        backBufIndex = -1;
    }

    i = FILE.getc(curFile.fp);
    c[0] = (char)i;

    ret =  (i == FILE.EOF) ? false : true;

    return ret;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Checks current file/buffer for header comment that determines whether
//    it is in ASCII or binary format. The header must be the very
//    next thing in the input for this to recognize it. This prints an
//    error message and returns false if there is no header and we are
//    reading from a file.
//
// Use: private

private boolean checkHeader()
//
////////////////////////////////////////////////////////////////////////
{
    final SoDBHeaderCB[]    preCB = new SoDBHeaderCB[1];
    final SoDBHeaderCB[]    postCB = new SoDBHeaderCB[1];
    final Object[] userData = new Object[1];
    final boolean[]            isBinary = new boolean[1];
    final float[]           versionNum = new float[1];
    final char[]            c = new char[1];
    
    // Don't need to do this again. This has to be set first here so
    // the subsequent reads don't try to do this again.
    curFile.readHeader = true;

    // See if first character in file is a comment character. If so,
    // see if there is a Inventor file header at the beginning. If so,
    // determine whether it is ASCII or binary. If there is no valid
    // header, assume it is ASCII.

    if (get(c)) {
        if (c[0] == COMMENT_CHAR) {
            final char[]        buf = new char[256];
            int         i = 0;

            // Read comment into buffer
            buf[i++] = c[0];
            while (get(c) && c[0] != '\n')
                buf[i++] = c[0];
            buf[i] = '\0';
            if (c[0] == '\n')
                curFile.lineNum++;

            // Read the file if the header is a registered header, or
            // if the file header is a superset of a registered header.
            String bufStr = new String(buf,0,i); // java port
            
            if (SoDB.getHeaderData(bufStr, isBinary, versionNum,
                                        preCB, postCB, userData, true)) {
        
                if (isBinary[0]) {
                    curFile.binary = true;
                    if (tmpBuffer == null) {
                        tmpBuffer = new byte[64];
                        tmpBufSize = 64;
                        curTmpBuf = 0;//(char *)tmpBuffer; java port
                    }
                } else {                    
                    curFile.binary = false;                
                }

                // Set the Inventor file version associated with the header
                setIVVersion(versionNum[0]);

                // Invoke the pre-callback associated with the header
                if (preCB[0] != null)
                    (preCB[0]).run(userData, this);
                
                // Store a pointer to the post-callback for later use
                curFile.postReadCB = postCB[0];
                curFile.CBData = userData;
                    
                curFile.headerOk = true;
                curFile.headerString = bufStr;
                return true;
            }
        }

        // Put non-comment char back in case we are reading from a buffer.
        else
            putBack(c[0]);
    }

    // If it gets here, no valid header was found. If we are reading
    // from a buffer, we can just assume we are reading ASCII, in the
    // latest format:
    if (fromBuffer()) { 
        curFile.binary = false;
        return true;
    }

    // If we are reading from a file, it MUST have a header
    SoReadError.post(this, "File does not have a valid header string");
    curFile.headerOk = false;
    return false;
}

    //! Set the Inventor version number of the current file
	public void                setIVVersion(float version) 
        { curFile.ivVersion = version; }


 
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Removes given directory from list.
//
// Use: public, static

public static void removeDirectory(String dirName)
//
////////////////////////////////////////////////////////////////////////
{
    int         i;
    String    dir;

    for (i = 0; i < directories.getLength(); i++) {

        dir = (String)directories.operator_square_bracket(i);

        if (dir.equals(dirName)) {
            directories.remove(i);
            //delete dir; java port
            break;
        }
    }
}

    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns whether current file being read is binary. This may have
//    to check the header to determine this info.
//
// Use: public

public boolean isBinary()
//
////////////////////////////////////////////////////////////////////////
{
    // Check header if not already done
    if (! curFile.readHeader)
         checkHeader();

    return curFile.binary;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Puts a just-read character back in input stream
//
// Use: internal

public void putBack(char c)
//
////////////////////////////////////////////////////////////////////////
{
    // Never put an EOF back in the stream
    if (c == (char) FILE.EOF)
        return;

    if (backBufIndex >= 0)
        --backBufIndex;
    else if (! fromBuffer())
        FILE.ungetc(c, curFile.fp);
    else if (isBinary())
        ;                               // Can't do anything???
    else
        curFile.curBuf--;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Puts a just-read character string back in input stream. It uses
//    backBuf to implement this.
//
// Use: internal

public void putBack(String string)
//
////////////////////////////////////////////////////////////////////////
{
    backBuf = string;
    backBufIndex = 0;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Finds a reference in dictionary, returning the base pointer or NULL.
//
// Use: private


public SoBase                                       // Returns pointer to base
findReference(final SbName name)  // Reference name
//
////////////////////////////////////////////////////////////////////////
{
    final Object[] base = new Object[1];

    // Generates a CC warning. Ho hum.
    if (curFile.refDict.find(name.getString(), base))
        return (SoBase ) base[0];

    return null;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a character from current file/buffer after skipping white space.
//    Returns false on EOF.
//
// Use: public

public boolean read(final char[] c)
//
////////////////////////////////////////////////////////////////////////
{
    return (skipWhiteSpace() && get(c));
}

//! Returns the Inventor file version of the file being read (e.g. 2.1).
//! If the file has a header registered through SoDB::registerHeader(),
//! the returned version is the Inventor version registered with the 
//! header.
public float               getIVVersion()  
    { return curFile.ivVersion; }

public boolean read(final int[] i)
{
    // READ_INTEGER(i, convertInt32, int, int32_t);
    boolean ok = false;                                                                
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (curFile.binary) {                                               
        int n = Integer.BYTES;//M_SIZEOF(dglType);                                            
        int pad = ((n+3) & ~0003) - n;                                        
        int tnum = 0;                                                         
        if (fromBuffer()) {                                                   
            if (eof())                                                        
                ok = false;                                                   
            else {                                                            
                ok = true;                                                    
                tnum = SoMachine.DGL_NTOH_INT32(curFile.curBufAsInt());//dglFunc(curFile->curBuf, (dglType *)&tnum);                   
                curFile.curBuf += Integer.BYTES + pad;                   
            }                                                                 
        }                                                                     
        else {                                                
            if (backupBufUsed == true) {                                      
                tnum = (backupBuf[0] << 24)+(backupBuf[1] << 16)+(backupBuf[2] << 8)+(backupBuf[3]);//(type)(*(type *)backupBuf);                             
                backupBufUsed = false;                                        
                return true;                                                  
            }                                                                 
            byte[] padbuf = new byte[4];                                                   
            makeRoomInBuf(/*M_SIZEOF(int)*/Integer.BYTES);                                 
            ok = FILE.fread(tmpBuffer, /*M_SIZEOF(int)*/Integer.BYTES, 1, curFile.fp)!=0;      
            tnum = convertInt32(tmpBuffer);                     
            if (pad != 0)                                                     
                ok = FILE.fread(padbuf, /*M_SIZEOF(char)*/1, pad, curFile.fp)!=0; 
        }                                                                     
        i[0] = (int)tnum;                                                     
    }                                                                         
    else {                                                                    
        final int[] _tmp = new int[1];                                                        
        ok = readInteger(_tmp);                                                    
        if (ok)                                                               
            i[0] = _tmp[0];                                                
    }                                                                         
    return ok;
}

public boolean read(final long[] i)
{
    // READ_INTEGER(i, convertInt32, int, int32_t);
    boolean ok = false;                                                                
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (curFile.binary) {                                               
        int n = Long.BYTES;//M_SIZEOF(dglType);                                            
        int pad = ((n+3) & ~0003) - n;                                        
        long tnum = 0;                                                         
        if (fromBuffer()) {                                                   
            if (eof())                                                        
                ok = false;                                                   
            else {                                                            
                ok = true;                                                    
                tnum = SoMachine.DGL_NTOH_INT64(curFile.curBufAsLong());//dglFunc(curFile->curBuf, (dglType *)&tnum);                   
                curFile.curBuf += Long.BYTES + pad;                   
            }                                                                 
        }                                                                     
        else {                                                
            if (backupBufUsed == true) {                                      
                tnum = (backupBuf[0] << 24)+(backupBuf[1] << 16)+(backupBuf[2] << 8)+(backupBuf[3]);//(type)(*(type *)backupBuf);                             
                backupBufUsed = false;                                        
                return true;                                                  
            }                                                                 
            byte[] padbuf = new byte[8];                                                   
            makeRoomInBuf(/*M_SIZEOF(int)*/Long.BYTES);                                 
            ok = FILE.fread(tmpBuffer, /*M_SIZEOF(int)*/Long.BYTES, 1, curFile.fp)!=0;      
            tnum = convertInt32(tmpBuffer);                     
            if (pad != 0)                                                     
                ok = FILE.fread(padbuf, /*M_SIZEOF(char)*/1, pad, curFile.fp)!=0; 
        }                                                                     
        i[0] = (long)tnum;                                                     
    }                                                                         
    else {                                                                    
        final long[] _tmp = new long[1];                                                        
        ok = readLong(_tmp);                                                    
        if (ok)                                                               
            i[0] = _tmp[0];                                                
    }                                                                         
    return ok;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts long from network format and puts in buffer.
//
// Use: private

private int
convertInt32(byte[] from)
//
////////////////////////////////////////////////////////////////////////
{
	
	int i = ByteBuffer.wrap(from).order(ByteOrder.LITTLE_ENDIAN).getInt();//(from[3]<<24)+(from[2]<<16)+(from[1]<<8)+(from[0]);
    return SoMachine.DGL_NTOH_INT32( /*INT32(from)*/i );
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts float from network format and puts in buffer.
//
// Use: private

private float
convertFloat(byte[] from)
//
////////////////////////////////////////////////////////////////////////
{
    return SoMachine.DGL_NTOH_FLOAT( from );
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts array of floats in read buffer from network format and
//    puts in array.
//
// Use: private

private void
convertFloatArray( byte[] from,
                            float[] to,
                            int len)
//
////////////////////////////////////////////////////////////////////////
{
//    float[] t = to;
//    byte[]  b = from;
	
	int l = Float.BYTES;
    
    for( int i=0; i< len; i++) {
    	byte[] buf = Arrays.copyOfRange(from, i*l, (i+1)*l);
    	to[i] = convertFloat(buf);
    }

//    while (len > 4) {           // unroll the loop a bit
//        DGL_NTOH_FLOAT( t[0], FLOAT(b));
//        DGL_NTOH_FLOAT( t[1], FLOAT(b + M_SIZEOF(float)));
//        DGL_NTOH_FLOAT( t[2], FLOAT(b + M_SIZEOF(float)*2));
//        DGL_NTOH_FLOAT( t[3], FLOAT(b + M_SIZEOF(float)*3));
//        t += 4;
//        b += M_SIZEOF(float)*4;
//        len -= 4;
//    }
//    while (len-- > 0) {
//        DGL_NTOH_FLOAT( *t, FLOAT(b));
//        t++;
//        b += M_SIZEOF(float);
//    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts array of int32_ts in read buffer from network format and
//    puts in array.
//
// Use: private

private void
convertInt32Array( byte[] from,
                           int[] to,
                           int len)
//
////////////////////////////////////////////////////////////////////////
{
	int l = Integer.BYTES;
    for( int i=0; i< len; i++) {
    	byte[] buf = Arrays.copyOfRange(from, i*l, (i+1)*l);
    	to[i] = convertInt32(buf);
    }

//    register int32_t  *t = to;
//    register char  *b = from;
//
//    while (len > 4) {           // unroll the loop a bit
//        DGL_NTOH_INT32( t[0], INT32(b));
//        DGL_NTOH_INT32( t[1], INT32(b + M_SIZEOF(int32_t)));
//        DGL_NTOH_INT32( t[2], INT32(b + M_SIZEOF(int32_t)*2));
//        DGL_NTOH_INT32( t[3], INT32(b + M_SIZEOF(int32_t)*3));
//        t += 4;
//        b += M_SIZEOF(int32_t)*4;
//        len -= 4;
//    }
//    while (len-- > 0) {
//        DGL_NTOH_INT32( *t, INT32(b));
//        t++;
//        b += M_SIZEOF(int32_t);
//    }
}



public boolean read(final short[] s)
{
    //READ_UNSIGNED_INTEGER(s, convertInt32, unsigned short, int32_t);
	boolean ok = false; //java port
	if(! skipWhiteSpace()) {
		ok = false;
	}
	else if(curFile.binary) {
        int n = Integer.BYTES;// M_SIZEOF(int);                                            
        int pad = ((n+3) & ~0003) - n;                                        
        int tnum = 0;                                                         
        if (fromBuffer()) {                                                   
            if (eof())                                                        
                ok = false;                                                   
            else {                                                            
                ok = true;                                                    
                tnum = SoMachine.DGL_NTOH_INT32(curFile.curBufAsInt());                   
                curFile.curBuf += Integer.BYTES + pad;                   
            }                                                                 
        }                                                                     
        else { //TODO                                                                
            if (backupBufUsed == true) {                                      
                tnum = (backupBuf[0] << 24)+(backupBuf[1] << 16)+(backupBuf[2] << 8)+(backupBuf[3]);//(type)(*(type *)backupBuf);                             
                backupBufUsed = false;                                        
                return true;                                                  
            }                                                                 
            byte[] padbuf = new byte[4];                                                   
            makeRoomInBuf(/*M_SIZEOF(int32_t)*/Integer.BYTES);                                 
            ok = FILE.fread(tmpBuffer, /*M_SIZEOF(int32_t)*/Integer.BYTES, 1, curFile.fp)!=0;      
            tnum = convertInt32(tmpBuffer);                     
            if (pad != 0)                                                     
                ok = FILE.fread(padbuf, /*M_SIZEOF(char)*/1, pad, curFile.fp)!=0; 
        }                                                                     
        s[0] = (short)tnum;                                                     
	}
	else {
		final int[] _tmp = new int[1];
		ok = readInteger(_tmp);
		if(ok) {
			s[0] = (short)_tmp[0];
		}
	}
	return ok;
}

public boolean read(DoubleConsumer dc) {
	final float[] f = new float[1];
	if(read(f)) {
		dc.accept(f[0]);
		return true;
	}
	return false;
}

public boolean read(final float[] f)
{
    //READ_REAL(f, convertFloat, float, float);
    boolean ok = false;                                                                
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (curFile.binary) {                                               
        int n = Float.BYTES;//M_SIZEOF(dglType);                                            
        int pad = ((n+3) & ~0003) - n;                                        
        float tnum = 0;                                                         
        if (fromBuffer()) {                                                   
            if (eof())                                                        
                ok = false;                                                   
            else {                                                            
                ok = true;                                                    
                tnum = convertFloat(curFile.buffer, curFile.curBuf);                   
                curFile.curBuf += Float.BYTES + pad;                   
            }                                                                 
        }                                                                     
        else {                                                                
            if (backupBufUsed == true) {
            	
                tnum = ByteBuffer.wrap(backupBuf).getFloat();//(float)(*(float *)backupBuf);                             
                backupBufUsed = false;                                        
                return true;                                                  
            }                                                                 
            byte[] padbuf = new byte[4];                                                   
            makeRoomInBuf(/*M_SIZEOF(float)*/Float.BYTES);                                 
            ok = FILE.fread(tmpBuffer, /*M_SIZEOF(float)*/Float.BYTES, 1, curFile.fp)!=0;      
            tnum = convertFloat(tmpBuffer);                     
            if (pad != 0)                                                     
                ok = FILE.fread(padbuf, /*M_SIZEOF(char)*/1, pad, curFile.fp)!=0; 
        }                                                                     
        f[0] = (float)tnum;       
    }                                                                         
    else {                                                                    
        final double[] _tmp = new double[1];                                                        
        ok = readReal(_tmp);                                                    
        if (ok)                                                               
            f[0] = (float) _tmp[0];                                                
    }                                                                         
    return ok;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts float from network format and puts in buffer.
//
// Use: private

private static float
convertFloat(String buffer, int from)
//
////////////////////////////////////////////////////////////////////////
{
	byte[] buf = new byte[Float.BYTES];
	for(int i=0;i<Float.BYTES;i++) {
		buf[i] = (byte)buffer.charAt(from+i);
	}
    return SoMachine.DGL_NTOH_FLOAT(buf);
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a character string from current file/buffer. If the character
//    string begins with a double quote, this reads until a matching
//    close quote is found. Otherwise, it reads until the next white
//    space is found. This returns false on EOF.
//
// Use: public

public boolean read(final Consumer<String> ref) {
	final String[] s = new String[1];
	if(read(s)) {
		ref.accept(s[0]);
		return true;
	}
	return false;
}
public boolean read(final String[] s)
//
////////////////////////////////////////////////////////////////////////
{
    if (! skipWhiteSpace())
        return false;

    if (curFile.binary) {
        // If there is a string in the putback buffer, use it.
        if (backBufIndex >= 0) {
            s[0] = backBuf;
            backBuf = "";
            backBufIndex = -1;
            return true;
        }

        //
        // Reading from the binary format consists of reading a word
        // representing the string length, followed by the string,
        // followed by the padding to the word boundary.  Put a NULL
        // character at the end of the string.
        //
        if (fromBuffer()) {
            if (eof())
                return false;

            int n = curFile.curBufAsInt();// * (int *) curFile.curBuf;
            n = SoMachine.DGL_NTOH_INT32(n);

            // A marker was read.  Return without incrementing the buffer
            if (n < 0)
                return false;

            final byte[] buffer = new byte[n/*+1*/];
            byte[] buf;
//            if (n > 1023)
//                buf = new char [n + 1];
//            else
                buf = buffer;
            curFile.curBuf += 4;
            curFile.memcpy(buf, curFile.curBuf, n);
            //buf[n] = '\0';
            curFile.curBuf += (n+3) & ~0003;
            s[0] = new String(buf, StandardCharsets.UTF_8);;
//            if (n > 1023)
//                delete [] buf;
            return true;
        }
        else { 
            // Break out the case where an eof is hit.  This will only be
            // The case in SoFile nodes which don't know how many children
            // they have.  Reading keeps happening until eof is hit.

            final int[] n = new int[1];
            if (FILE.fread( n, Integer.BYTES/* sizeof(int)*/, 1, curFile.fp) == 1) {
                n[0] = SoMachine.DGL_NTOH_INT32(n[0]);

                if (n[0] < 0) {
                    // A marker was read.  Put it in the backup buffer
                    // so the next read will read it.
                    //int *tint = (int *) backupBuf;
                    //*tint = n;
                	backupBuf[0] = (byte) (n[0] >> 24); // java port
                	backupBuf[1] = (byte) (n[0] >> 16);
                	backupBuf[2] = (byte) (n[0] >> 8);
                	backupBuf[3] = (byte) (n[0]);
                    backupBufUsed = true;
                    return false;
                }

                //byte[] buffer = new byte[1024];
                byte[] buf;
                //if (n[0] > 1023)
                    buf = new byte [n[0] /*+ 1*/];
                //else
                //    buf = buffer;
                boolean ok =
                    (FILE.fread( buf, 1/*sizeof(char)*/, n[0], curFile.fp) == n[0]);
                if (ok) {
                    int pad = ((n[0]+3) & ~003) - n[0];
                    byte[] padbuf = new byte[4];
                    ok = (FILE.fread( padbuf, /*sizeof(char)*/1, pad,
                                curFile.fp) == pad);

                    if (ok) {
                        //buf[n[0]] = '\0';
                        try {
							s[0] = new String(buf, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                }
//                if (n > 1023) java port
//                    delete [] buf;

                if (! ok)
                    return false;
            }
            else
                s[0] = "";

            return true;
        }
    }

    else {
        boolean        quoted;
        final char[]        c = new char[1];
        final char[]        bufStore = new char[256];
        int       buf; // java port
        int         bytesLeft;

        s[0] = "";

        // Read first character - if none, EOF
        if (! get(c))
            return false;

        // If quoted string
        quoted = (c[0] == '\"');
        if (! quoted)
            putBack(c[0]);

        do {
            buf       = 0;//bufStore; //java port
            bytesLeft = (bufStore.length) - 1;

            // Read a bufferfull
            while (bytesLeft > 0) {

                // Terminate on EOF
                if (! get(bufStore, buf))
                    break;

                if (quoted) {
                    // Terminate on close quote
                    if (bufStore[buf] == '\"')
                        break;

                    // Check for escaped double quote
                    if (bufStore[buf] == '\\') {
                        if ((get(c)) && c[0] == '\"')
                            bufStore[buf] = '\"';
                        else
                            putBack(c[0]);
                    }

                    if (bufStore[buf] == '\n')
                        curFile.lineNum++;
                }

                // If unquoted string, terminate at whitespace
                else if (Character.isSpace(bufStore[buf])) {
                    // Put back the whitespace
                    putBack(bufStore[buf]);
                    break;
                }

                buf++;
                bytesLeft--;
            }
            bufStore[buf] = '\0';

            // Tack the buffer onto the string
            s[0] += String.valueOf(bufStore,0,buf);

        } while (bytesLeft == 0);
    }

    return true;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Skips over white space (blanks, tabs, newlines) and Inventor
//    comments (from COMMENT_CHAR to end of line). Keeps current line
//    number up-to-date. Returns false on error.
//
// Use: private

private boolean skipWhiteSpace()
//
////////////////////////////////////////////////////////////////////////
{
    final char[]        c = new char[1];
    boolean        gotChar;

    // Check for ASCII/binary header if not already done. Since most
    // of the read methods call this first, it's a convenient place to
    // do it.
    if (! curFile.readHeader && ! checkHeader())
        return false;

    // Don't skip space in binary input. In ASCII, keep going while
    // space and comments appear
    if (! curFile.binary) {
        if (fromBuffer()) {
            while (true) {
    
                // Skip over space characters
                while ((gotChar = getASCIIBuffer(c)) && Character.isSpace(c[0]))
                    if (c[0] == '\n')
                        curFile.lineNum++;
    
                if (! gotChar)
                    break;
    
                // If next character is comment character, flush til end of line
                if (c[0] == COMMENT_CHAR) {
                    while (getASCIIBuffer(c) && c[0] != '\n')
                        ;
    
                    if (eof())
                        SoReadError.post(this,
                                          "EOF reached before end of comment");
                    else
                        curFile.lineNum++;
                }
                else {
                    putBack(c[0]);
                    break;              // EXIT: hit a non-comment, non-space
                }
            }
        }
        else {
            while (true) {
    
                // Skip over space characters
                while ((gotChar = getASCIIFile(c)) && Character.isSpace(c[0]))
                    if (c[0] == '\n')
                        curFile.lineNum++;
    
                if (! gotChar)
                    break;
    
                // If next character is comment character, flush til end of line
                if (c[0] == COMMENT_CHAR) {
                    while (getASCIIFile(c) && c[0] != '\n')
                        ;
    
                    if (eof())
                        SoReadError.post(this,
                                          "EOF reached before end of comment");
                    else
                        curFile.lineNum++;
                }
                else {
                    putBack(c[0]);
                    break;              // EXIT: hit a non-comment, non-space
                }
            }
        }
    }

    // If EOF, pop to previous file and skip space again
    while (eof() && popFile())
        if (! skipWhiteSpace())
            return false;

    return true;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Pops current file from stack. This should be called when current
//    file is at EOF and more stuff is needed. This returns false if
//    there ain't no more files on the stack.
//
// Use: private

private boolean popFile()
//
////////////////////////////////////////////////////////////////////////
{
    // Call the post callback associated with this file type (as determined
    // by the file header)
    if (curFile.postReadCB != null)
        (curFile.postReadCB).run(curFile.CBData, this);
        
    int depth = files.getLength();

    // Nothing to pop if we're already in last file on stack
    if (depth == 1)
        return false;

    // Remove one file
    files.truncate(depth - 1);

    // Free up structure for current file and set to new one
    if (curFile.openedHere)
        FILE.fclose(curFile.fp);

    curFile.refDict.destructor();
    curFile.destructor();
    curFile = ( SoInputFile ) files.operator_square_bracket(depth - 2);

    return true;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a hexadecimal number into a int32_t. Returns false on EOF or
//    if no valid hexadecimal number was read.
//
// Use: public

public boolean readHex(final int[] l)
{
    int         i;
    final char[]        str = new char[READ_STRINGBUFFER_SIZE];    // Number can't be longer than this
    int        s = 0; // java port
    boolean        ret;

    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        ret = false;
        if (backBufIndex >= 3) {

            // 2005-04-19 Felix: Check for buffer overflow
            if (backBuf.length() >= READ_STRINGBUFFER_SIZE) {
                SoDebugError.post("SoInput::readHex",
                                   "Hexadecimal value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters");

                //strncpy(str, backBuf, READ_STRINGBUFFER_SIZE-1); //java port
            	int ii;
            	for (ii=0;ii<READ_STRINGBUFFER_SIZE-1;ii++ ) {
            		str[ii] = backBuf.charAt(ii);
            	}
                str[READ_STRINGBUFFER_SIZE-1] = '\0';
            }
            else {
            	int ii;
            	for (ii=0;ii<backBuf.length();ii++ ) {
            		str[ii] = backBuf.charAt(ii);
            	}
            	str[ii] = 0;
                //strcpy(str, backBuf); java port
            }

            ret = true;
        }

        // Clear the back buffer.
        backBuf ="";
        backBufIndex = -1;
    }

    // Read from a memory buffer
    else if (fromBuffer()) {
        skipWhiteSpace();
        s = curFile.curBuf;
        ret = true;
    }

    // Read from a file
    else {
        skipWhiteSpace();
        while ((i = FILE.getc(curFile.fp)) != FILE.EOF) {
            str[s] = (char) i;
            if (str[s] == ',' || str[s] == ']' || str[s] == '}' || Character.isSpace(str[s])) {
                putBack(str[s]);
                str[s] = '\0';
                break;
            }

            // 2005-04-19 Felix: Check for buffer overflow
            if (s /*- str*/ < READ_STRINGBUFFER_SIZE-1) {//java port
                s++;
            }
            else {
                SoDebugError.post("SoInput::readHex",
                                   "Hexadecimal value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters");
                
                s = /*str +*/ READ_STRINGBUFFER_SIZE-1; //java port
                str[s] = '\0';
                break;
            }
        }

        ret = (s /*- str*/ <= 0) ? false : true; // java port
        s = /*str*/0; // java port
    }

    // Convert the hex string we just got into an uint32_teger
    if (ret) {
        //int     i; java port
        int     minSize = 3;    // Must be at least this many bytes in str
        int    save = s; // java port

        char ss = str[s]; s++; // java port
        if (ss == '0') {
            if (str[s] == '\0' || str[s] == ',' || str[s] == ']' || str[s] == '}' ||
                Character.isSpace(str[s]))
            {
                l[0] = 0;
                curFile.curBuf++;
            }
            else if (str[s] == 'x' || str[s] == 'X') {
                s++;
                l[0] = 0;
                while (str[s] != '\0' && str[s] != ',' && str[s] != ']' && str[s] != '}' &&
                        ! Character.isSpace(str[s]))
                {
                    i = (int)str[s];
                    if (i >= '0' && i <= '9') {
                        i -= '0';
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'A' && i <= 'F') {
                        i -= ('A' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'a' && i <= 'f') {
                        i -= ('a' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    s++;
                }
    
                if (fromBuffer()) {
                    // Make sure we have at least 1 actual digit
                    if (s - curFile.curBuf < minSize) {
                        if (fromBuffer())
                            curFile.curBuf = save;
                        else
                            putBack(str[save]); // java port
                        ret = false;
                    }
                    else
                        curFile.curBuf = s;
                }
                else if (s /*- str*/ < minSize) { // java port
                    if (fromBuffer())
                        curFile.curBuf = save;
                    else
                        putBack(str[save]); // java port
                    ret = false;
                }
                  
            }
        }
    }

    return ret;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a int32_t signed integer. Returns FALSE on EOF or if no
//    valid integer was read.
//
// Use: private

private boolean readInteger(final int[] l)
//
////////////////////////////////////////////////////////////////////////
{
    final char[]        str = new char[READ_STRINGBUFFER_SIZE];    // Number can't be longer than this
    int        s = /*str*/0; // java port
    int         i;
    boolean        ret;


    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        
        // 2005-04-19 Felix: Check for buffer overflow
        if (backBuf.length() >= READ_STRINGBUFFER_SIZE) {
            SoDebugError.post("SoInput::readInteger",
                               "Integer value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters");
            
            //*(strncpy(str, backBuf.getString(), READ_STRINGBUFFER_SIZE-1) + READ_STRINGBUFFER_SIZE-1) = '\0';
        	int ii;
        	for (ii=0;ii<READ_STRINGBUFFER_SIZE-1;ii++ ) {
        		str[ii] = backBuf.charAt(ii);
        	}
            str[READ_STRINGBUFFER_SIZE-1] = '\0';
        }
        else {
            //strcpy(str, backBuf.getString());
        	int ii;
        	for (ii=0;ii<backBuf.length();ii++ ) {
        		str[ii] = backBuf.charAt(ii);
        	}
        	str[ii] = 0;
        }

        // Clear the back buffer.
        backBuf = "";
        backBufIndex = -1;

        s = /*str*/0;
        ret = true;
    }

    // Read from a memory buffer
    else if (fromBuffer()) {
        s = curFile.curBuf;
        ret = true;
    }

    // Read from a file
    else {
        //int i;

        while ((i = FILE.getc(curFile.fp)) != FILE.EOF) {
            str[s] = (char) i;
            if (str[s] == ',' || str[s] == ']' || str[s] == '}' || Character.isSpace(str[s])) {
                putBack(str[s]);
                str[s] = '\0';
                break;
            }
            
            // 2005-04-19 Felix: Check for buffer overflow
            if (s /*- str*/ < READ_STRINGBUFFER_SIZE-1) {
                s++;
            }
            else {
                SoDebugError.post("SoInput::readInteger",
                                   "Integer value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters" );
                
                s = /*str +*/ READ_STRINGBUFFER_SIZE-1;
                str[s] = '\0';
                break;
            }
        }

        ret = (s /*- str*/ <= 0) ? false : true;
        s = /*str*/0;
    }
    
    // Convert the string we just got into a int32_t integer
    if (ret) {
        int ptr;
        int save = s;

        if (str[s] == '0') {
            s++;

            // The string just contains a single zero
            if (str[s] == '\0' || str[s] == ',' || str[s] == ']' || str[s] == '}' ||
                Character.isSpace(str[s]))
            {
                l[0] = 0;
                ret = true;
            }

            // A hexadecimal format number
            else if (str[s] == 'x' || str[s] == 'X') {
                s++;
                l[0] = 0;
                ptr = s;
                while (str[s] != '\0') {
                    i = (int)str[s];
                    if (i >= '0' && i <= '9') {
                        i -= '0';
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'A' && i <= 'F') {
                        i -= ('A' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'a' && i <= 'f') {
                        i -= ('a' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    else {      // unrecognized character; stop processing
                        break;
                    }
                    s++;
                }
                if (s == ptr) {
                    if (fromBuffer())
                        s = curFile.curBuf = save;
                    else
                        putBack(str[save]);
                    ret = false;
                }
            }

            // An octal format number
            else {
                l[0] = 0;
                ptr = s;
                while ((int)str[s] >= '0' && (int)str[s] <= '7') {
                    i = (int)str[s] - '0';
                    l[0] = (l[0]<<3) + i;
                    s++;
                }
                if (s == ptr) {
                    if (fromBuffer())
                        s = curFile.curBuf = save;
                    else
                        putBack(str[save]);
                    ret = false;
                }
            }
        }

        // A decimal format number
        else {
            int sign = 1;

            l[0] = 0;
            if (str[s] == '-' || str[s] == '+') {
                s++;
                sign = -1;
            }
            ptr = s;
            while ((int)str[s] >= '0' && (int)str[s] <= '9') {
                i = (int)str[s] - '0';
                l[0] = l[0]*10 + i;
                s++;
            }
            l[0] *= sign;
            if (s == ptr) {
                if (fromBuffer())
                    s = curFile.curBuf = save;
                else
                    putBack(str[save]);
                ret = false;
            }
        }

        if (fromBuffer())
            curFile.curBuf = s;
    }

    return ret;

}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a int32_t signed integer. Returns FALSE on EOF or if no
//    valid integer was read.
//
// Use: private

private boolean readLong(final long[] l)
//
////////////////////////////////////////////////////////////////////////
{
    final char[]        str = new char[READ_STRINGBUFFER_SIZE];    // Number can't be longer than this
    int        s = /*str*/0; // java port
    int         i;
    boolean        ret;


    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        
        // 2005-04-19 Felix: Check for buffer overflow
        if (backBuf.length() >= READ_STRINGBUFFER_SIZE) {
            SoDebugError.post("SoInput::readInteger",
                               "Integer value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters");
            
            //*(strncpy(str, backBuf.getString(), READ_STRINGBUFFER_SIZE-1) + READ_STRINGBUFFER_SIZE-1) = '\0';
        	int ii;
        	for (ii=0;ii<READ_STRINGBUFFER_SIZE-1;ii++ ) {
        		str[ii] = backBuf.charAt(ii);
        	}
            str[READ_STRINGBUFFER_SIZE-1] = '\0';
        }
        else {
            //strcpy(str, backBuf.getString());
        	int ii;
        	for (ii=0;ii<backBuf.length();ii++ ) {
        		str[ii] = backBuf.charAt(ii);
        	}
        	str[ii] = 0;
        }

        // Clear the back buffer.
        backBuf = "";
        backBufIndex = -1;

        s = /*str*/0;
        ret = true;
    }

    // Read from a memory buffer
    else if (fromBuffer()) {
        s = curFile.curBuf;
        ret = true;
    }

    // Read from a file
    else {
        //int i;

        while ((i = FILE.getc(curFile.fp)) != FILE.EOF) {
            str[s] = (char) i;
            if (str[s] == ',' || str[s] == ']' || str[s] == '}' || Character.isSpace(str[s])) {
                putBack(str[s]);
                str[s] = '\0';
                break;
            }
            
            // 2005-04-19 Felix: Check for buffer overflow
            if (s /*- str*/ < READ_STRINGBUFFER_SIZE-1) {
                s++;
            }
            else {
                SoDebugError.post("SoInput::readInteger",
                                   "Integer value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters" );
                
                s = /*str +*/ READ_STRINGBUFFER_SIZE-1;
                str[s] = '\0';
                break;
            }
        }

        ret = (s /*- str*/ <= 0) ? false : true;
        s = /*str*/0;
    }
    
    // Convert the string we just got into a int32_t integer
    if (ret) {
        int ptr;
        int save = s;

        if (str[s] == '0') {
            s++;

            // The string just contains a single zero
            if (str[s] == '\0' || str[s] == ',' || str[s] == ']' || str[s] == '}' ||
                Character.isSpace(str[s]))
            {
                l[0] = 0;
                ret = true;
            }

            // A hexadecimal format number
            else if (str[s] == 'x' || str[s] == 'X') {
                s++;
                l[0] = 0;
                ptr = s;
                while (str[s] != '\0') {
                    i = (int)str[s];
                    if (i >= '0' && i <= '9') {
                        i -= '0';
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'A' && i <= 'F') {
                        i -= ('A' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    else if (i >= 'a' && i <= 'f') {
                        i -= ('a' - 10);
                        l[0] = (l[0]<<4) + i;
                    }
                    else {      // unrecognized character; stop processing
                        break;
                    }
                    s++;
                }
                if (s == ptr) {
                    if (fromBuffer())
                        s = curFile.curBuf = save;
                    else
                        putBack(str[save]);
                    ret = false;
                }
            }

            // An octal format number
            else {
                l[0] = 0;
                ptr = s;
                while ((int)str[s] >= '0' && (int)str[s] <= '7') {
                    i = (int)str[s] - '0';
                    l[0] = (l[0]<<3) + i;
                    s++;
                }
                if (s == ptr) {
                    if (fromBuffer())
                        s = curFile.curBuf = save;
                    else
                        putBack(str[save]);
                    ret = false;
                }
            }
        }

        // A decimal format number
        else {
            int sign = 1;

            l[0] = 0;
            if (str[s] == '-' || str[s] == '+') {
                s++;
                sign = -1;
            }
            ptr = s;
            while ((int)str[s] >= '0' && (int)str[s] <= '9') {
                i = (int)str[s] - '0';
                l[0] = l[0]*10 + i;
                s++;
            }
            l[0] *= sign;
            if (s == ptr) {
                if (fromBuffer())
                    s = curFile.curBuf = save;
                else
                    putBack(str[save]);
                ret = false;
            }
        }

        if (fromBuffer())
            curFile.curBuf = s;
    }

    return ret;

}



    //! Returns number of bytes left in current buffer
	public long              freeBytesInBuf()
        { return (curFile.bufSize -
                  (curFile.curBuf/* - curFile.buffer*/)); }



//! Adds a reference to dictionary in current file.  This may also
//! add a reference to the global dictionary if addToGlobalDict is
//! true (the default).
// java port
public void                addReference(final SbName name, final SoBase base) {
                                 addReference(name, base, true);
}
    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Adds a reference to dictionary in current file/buffer.
//    The name passed will be a full identifier-- the object's name
//    followed by a '+' and the object's reference ID.  This routine
//    takes care of stripping the name before the '+' and giving the
//    object that name.  It also notices if the name is an underscore
//    followed by nothing but digits (Inventor 1.0 format for
//    instances) and leaves the object unnamed in that case.
//    Called by SoBase.
//
// Use: private

public void addReference(final SbName name,       // Reference name
                      final SoBase base, boolean addToGlobalDict)
//
////////////////////////////////////////////////////////////////////////
{
    // Enter in dictionary : generates a CC warning...
    curFile.refDict.enter(name.getString(), (Object) base);

    int length = name.getLength();
    if (length == 0) return;

    String n = name.getString();

    // If we're reading a 1.0 file and the name is an '_' followed by
    // all digits, don't name the node.
    if (n.charAt(0) == '_' &&  curFile.ivVersion == 1.0f) {
        int i;
        for (i = 1; i < length; i++) {
            if (!Character.isDigit(n.charAt(i))) break;
        }
        if (i == length) return;
    }

    if (addToGlobalDict) {
        // Look for the first '+':
        int firstPlus = n.indexOf('+');

        if (firstPlus == -1) {
            base.setName(name);
        }
        else if (firstPlus != 0) {
            SbName instanceName = new SbName(n.substring(0, firstPlus)/*SbString(n, 0, firstPlus-n-1)*/);
            base.setName(instanceName);
        }
    }
}

    public boolean readReal_old(final double[] d) {
        int         n;
        boolean        ret;
        
        //TODO
        
        n = FILE.fscanf(curFile.fp, "%lf", d);
        ret =  (n == 0 || n == FILE.EOF) ? false : true;
        
        return ret;
    }
    

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a double floating-point real number. Returns FALSE on EOF
//    or if no valid real was read. This is used by all real-reading
//    methods.
//
// Use: private

public boolean
readReal(final double[] d)
//
////////////////////////////////////////////////////////////////////////
{
    int         n;
    final char[]        str = new char[READ_STRINGBUFFER_SIZE];    // Number can't be longer than this
    /*char        **/ int s = /*str*/0; //java port
    boolean        ret;

    // Read from backBuf if it is not empty
    if (backBufIndex >= 0) {
        n = FILE.sscanf(backBuf/*.getString()*/, "%lf", d);

        // Clear the back buffer.
        backBuf = "";//.makeEmpty(); java port
        backBufIndex = -1;

        ret = (n == 0 || n == FILE.EOF) ? false : true;
    }

    else if (fromBuffer()) {
        boolean    gotNum = false;
        boolean    isTruncated = false;

        ////////////////////////////////////////////
        //
        // Leading sign
    
        n = readChar(str, s, '-');
        if (n == 0)
            n = readChar(str, s, '+');
        s += n;
    
        ////////////////////////////////////////////
        //
        // Integer before decimal point
    
        // 2005-04-19 Felix: Check for buffer overflow
        if ((n = readDigits(str, s, SB_MAX(READ_STRINGBUFFER_SIZE-1 - (s/* - str*/), 0))) > 0) {
            gotNum = true;
            s += n;
        }
    
        ////////////////////////////////////////////
        //
        // Decimal point
    
        // 2005-04-19 Felix: Check for buffer overflow
        if (s/* - str*/ < READ_STRINGBUFFER_SIZE-1) {

            if (readChar(str, s, '.') > 0) {
                s++;

                ////////////////////////////////////////////
                //
                // Integer after decimal point (no sign)

                // 2005-04-19 Felix: Check for buffer overflow
                if ((n = readDigits(str, s, SB_MAX(READ_STRINGBUFFER_SIZE-1 - (s /*- str*/), 0))) > 0) {
                    gotNum = true;
                    s += n;
                }
            }
    
            // If no number before or after decimal point, there's a problem
            if (! gotNum)
                return false;

            ////////////////////////////////////////////
            //
            // 'e' or 'E' for exponent

            // 2005-04-19 Felix: Check for buffer overflow
            if (s /*- str*/ < READ_STRINGBUFFER_SIZE-1) {

                n = readChar(str, s, 'e');
                if (n == 0)
                    n = readChar(str, s, 'E');

                if (n > 0) {
                    s += n;

                    ////////////////////////////////////////////
                    //
                    // Sign for exponent

                    // 2005-04-19 Felix: Check for buffer overflow
                    if (s /*- str*/ < READ_STRINGBUFFER_SIZE-1) {

                        n = readChar(str,s, '-');
                        if (n == 0)
                            n = readChar(str,s, '+');
                        s += n;

                        ////////////////////////////////////////////
                        //
                        // Exponent integer

                        // 2005-04-19 Felix: Check for buffer overflow
                        if ((n = readDigits(str, s, SB_MAX(READ_STRINGBUFFER_SIZE-1 - (s /*- str*/), 0))) > 0)
                            s += n;

                        else
                            return false;       // Invalid exponent
                    }
                    else {
                        isTruncated = true;
                    }
                }
            }
            else {
                isTruncated = true;
            }
        }
        else {
            isTruncated = true;
        }

        if (isTruncated) {
            SoDebugError.post("SoInput::readReal",
                               "Double floating-point value to big for internal representation, value truncated to "+(READ_STRINGBUFFER_SIZE-1)+" characters");
        }

        // Terminator
        str[s] = '\0';

        d[0] = Float.parseFloat(String.valueOf(str));

        ret = true;
    }
    else {
        n = FILE.fscanf(curFile.fp, "%lf", d);
        ret =  (n == 0 || n == FILE.EOF) ? false : true;
    }

    return ret;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads a series of decimal digits into string. Returns number of
//    bytes read.
//
// Use: private

private int
readDigits(char[] string, int index, int maxDigits)
//
////////////////////////////////////////////////////////////////////////
{
    final char[] c = new char[1];
    int s = index; // java port

    if (fromBuffer()) {
        while (getASCIIBuffer(c)) {

            // 2005-04-19 Felix: Check for buffer overflow
            if (Character.isDigit(c[0]) && (s - index/*string*/ < maxDigits)) {
                string[s] = c[0]; s++;
            }

            else {
                putBack(c[0]);
                break;
            }
        }
    }
    else {
        while (getASCIIFile(c)) {

            if (Character.isDigit(c[0]) && (s - index/*string*/ < maxDigits)) {
                string[s] = c[0]; s++;
            }

            else {
                putBack(c[0]);
                break;
            }
        }
    }

    return s - index/*string*/;
}



////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads the given character. Returns the number of bytes read (0 or 1).
//
// Use: private

public int
readChar(char[] string, int index, char charToRead)
//
////////////////////////////////////////////////////////////////////////
{
    final char[]        c = new char[1];
    int         ret;

    if (fromBuffer()) {
        if (! getASCIIBuffer(c))
            ret = 0;

        else if (c[0] == charToRead) {
            string[index] = c[0];
            ret = 1;
        }

        else {
            putBack(c[0]);
            ret = 0;
        }
    }
    else {
        if (! getASCIIFile(c))
            ret = 0;

        else if (c[0] == charToRead) {
            string[index] = c[0];
            ret = 1;
        }

        else {
            putBack(c[0]);
            ret = 0;
        }
    }

    return ret;
}

    

/**
 * java port
 * @param intConsumer
 * @return
 */
	public boolean read(IntConsumer intConsumer) {
		final int[] ret = new int[1];
		if(read(ret)) {
			intConsumer.accept(ret[0]);
			return true;
		}
		return false;
	}


public boolean read(double[] f) {
    //READ_REAL(f, convertFloat, float, float);
    boolean ok = false;                                                                
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (curFile.binary) { //TODO                                               
//        int n = M_SIZEOF(dglType);                                            
//        int pad = ((n+3) & ~0003) - n;                                        
//        dglType tnum;                                                         
//        if (fromBuffer()) {                                                   
//            if (eof())                                                        
//                ok = false;                                                   
//            else {                                                            
//                ok = true;                                                    
//                dglFunc(curFile.curBuf, (dglType *)&tnum);                   
//                curFile.curBuf += M_SIZEOF(dglType) + pad;                   
//            }                                                                 
//        }                                                                     
//        else {                                                                
//            if (backupBufUsed == true) {                                      
//                num = (type)(*(type *)backupBuf);                             
//                backupBufUsed = false;                                        
//                return true;                                                  
//            }                                                                 
//            char padbuf[4];                                                   
//            makeRoomInBuf(M_SIZEOF(dglType));                                 
//            ok = fread(tmpBuffer, M_SIZEOF(dglType), 1, curFile.fp)!=0;      
//            dglFunc((char *)tmpBuffer, (dglType *)&tnum);                     
//            if (pad != 0)                                                     
//                ok = fread((void *)padbuf, M_SIZEOF(char), pad, curFile.fp)!=0; 
//        }                                                                     
//        num = (type)tnum;       
    }                                                                         
    else {                                                                    
        final double[] _tmp = new double[1];                                                        
        ok = readReal(_tmp);                                                    
        if (ok)                                                               
            f[0] = _tmp[0];                                                
    }                                                                         
    return ok;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads an array of unsigned chars from current file/buffer.
//
// Use: public

public boolean readBinaryArray(byte[] c, int length)
//
////////////////////////////////////////////////////////////////////////
{
    boolean ok = true;
    if (! skipWhiteSpace())
        ok = false;
    else if (fromBuffer()) {
        if (eof())
            ok = false;
        else {
        	for(int i=0; i<length;i++) {
        		c[i] = (byte)curFile.buffer.charAt(curFile.curBuf+1);
        	}
            //memcpy(c, curFile.curBuf, length); java port
            curFile.curBuf += length /** M_SIZEOF(unsigned char)*/;
        }
    }
    else {
        int i = (int)(FILE.fread(c, /*M_SIZEOF(unsigned char)*/1, length, curFile.fp));
        if (i != length)
            return false;
    }
    return ok;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads an array of floats from current file/buffer.
//
// Use: public

public boolean
readBinaryArray(float[] array, int length)
//
////////////////////////////////////////////////////////////////////////
{
    //READ_BIN_ARRAY(f, length, convertFloatArray, float);
    boolean ok = true;                                                         
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (fromBuffer()) {                                                  
        if (eof())                                                            
            ok = false;                                                       
        else {                                                                
        	convertFloatArray(curFile.buffer, curFile.curBuf, (float[])array, length);                  
            curFile.curBuf += length * Float.BYTES;//M_SIZEOF(type);                       
        }                                                                     
    }                                                                         
    else {                                                             
        makeRoomInBuf(length * /*M_SIZEOF(float)*/Float.BYTES);                               
        int i = FILE.fread(tmpBuffer, /*M_SIZEOF(float)*/Float.BYTES, length, curFile.fp); 
        if (i != length)                                                      
            return false;                                                     
        convertFloatArray(tmpBuffer, array, length);                    
    }                                                                         
    return ok;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Reads an array of int32_ts from current file/buffer.
//
// Use: public

public boolean
readBinaryArray(int[] array, int length)
//
////////////////////////////////////////////////////////////////////////
{
    //READ_BIN_ARRAY(l, length, convertInt32Array, int32_t);
    boolean ok = true;                                                         
    if (! skipWhiteSpace())                                                   
        ok = false;                                                           
    else if (fromBuffer()) {                                                  
        if (eof())                                                            
            ok = false;                                                       
        else {                                                                
        	convertInt32Array(curFile.buffer, curFile.curBuf, (int[])array, length);                  
            curFile.curBuf += length * Integer.BYTES;//M_SIZEOF(type);                       
        }                                                                     
    }                                                                         
    else {                                                             
        makeRoomInBuf(length * /*M_SIZEOF(int)*/Integer.BYTES);                               
        int i = FILE.fread(tmpBuffer, /*M_SIZEOF(int)*/Integer.BYTES, length, curFile.fp); 
        if (i != length)                                                      
            return false;                                                     
        convertInt32Array(tmpBuffer, array, length);                    
    }                                                                         
    return ok;
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Converts array of floats in read buffer from network format and
//    puts in array.
//
// Use: private

private static void convertFloatArray(String buf,  int from,
                            float[] to,
                            int len)
//
////////////////////////////////////////////////////////////////////////
{
	int lenBytes = len*Float.BYTES;
	byte[] in = new byte[lenBytes];
	for(int i=0; i< lenBytes;i++) {
		in[i] = (byte)buf.charAt(from+i);
	}
	ByteBuffer bb = ByteBuffer.wrap(in);
	bb.asFloatBuffer().get(to);
}

////////////////////////////////////////////////////////////////////////
//
//Description:
//Converts array of floats in read buffer from network format and
//puts in array.
//
//Use: private

private static void convertInt32Array(String buf,  int from,
int[] to,
int len)
//
////////////////////////////////////////////////////////////////////////
{
	int lenBytes = len*Integer.BYTES;
	byte[] in = new byte[lenBytes];
	for(int i=0; i< lenBytes;i++) {
		in[i] = (byte)buf.charAt(from+i);
	}
	ByteBuffer bb = ByteBuffer.wrap(in);
	bb.asIntBuffer().get(to);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Makes sure temp buffer can contain nBytes more bytes. Returns
//    FALSE if this is not possible.
//
// Use: private

private boolean
makeRoomInBuf(int nBytes)
//
////////////////////////////////////////////////////////////////////////
{
    // If already had problems with buffer, stop
    if (tmpBuffer == null)
        return false;

    // If buffer not big enough, realloc a bigger one
    if (nBytes >= tmpBufSize) {
        // While not enough room, double size of buffer
        while (nBytes >= tmpBufSize)
            tmpBufSize *= 2;

        tmpBuffer = new byte[tmpBufSize];//realloc(tmpBuffer, tmpBufSize);

        // Test for bad reallocation
        if (tmpBuffer == null)
            return false;
    }

    return true;
}


/*!
  Returns the list of directories which'll be searched upon loading
  Coin and VRML format files. Directory searches will be done whenever
  any external references appears in a file, for instance to texture images.
 */
public static SbStringList 
getDirectories()
{
//  if (soinput_tls) { TODO COIN 3D
//    soinput_tls_data  data = (soinput_tls_data )soinput_tls->get();
//    if (data->instancecount) { return *data->searchlist; }
//  }
//
//  return SoInput.dirsearchlist;
    return directories;
}

// internal method used for testing if a file exists
private static boolean
test_filename(final String filename)
{
  FILE fp = FILE.fopen(filename, "rb");
//#if COIN_DEBUG && 0 // flip 1<->0 to turn texture search trace on or off
//  SoDebugError::postInfo("test_filename", "file search: %s (%s)\n",
//                         filename.getString(), fp ? "hit" : "miss");
//#endif // !COIN_DEBUG

  if (fp != null) {
    FILE.fclose(fp);
    return true;
  }
  return false;
}


/*!
  Given a \a basename for a file and an array of \a directories to
  search, returns the full name of the file found.

  In addition to looking at the root of each directory in \a
  directories, all \a subdirectories is also searched for each item in
  \a directories.

  If no file matching \a basename could be found in any of the
  directories, returns an empty string.

  This method is a Coin extension, not part of the original Inventor
  API.
*/
public static String
searchForFile( final String basename,
                       final SbStringList directories,
                       final SbStringList subdirectories)
{
  int i;

  if (test_filename(basename)) return basename;

  String fullname = basename;

  // TODO : to implement with java classes
//  boolean trypath = true;
//  const char * strptr = basename.getString();
//  const char * lastunixdelim = strrchr(strptr, '/');
//  const char * lastdosdelim = strrchr(strptr, '\\');
//  if (!lastdosdelim) {
//    lastdosdelim = strrchr(strptr, ':');
//    if (lastdosdelim) trypath = FALSE;
//  }
//  const char * lastdelim = SbMax(lastunixdelim, lastdosdelim);
//
//  if (lastdelim && trypath) {
//    String tmpstring;
//    for (i = 0; i < directories.getLength(); i++) {
//      SbString dirname(directories[i]->getString());
//      int dirlen = dirname.getLength();
//
//      if (dirlen > 0 &&
//          dirname[dirlen-1] != '/' &&
//          dirname[dirlen-1] != '\\' &&
//          dirname[dirlen-1] != ':') {
//        dirname += "/";
//      }
//
//      tmpstring.sprintf("%s%s", dirname.getString(),
//                        fullname.getString());
//      if (test_filename(tmpstring)) return tmpstring;
//    }
//  }
//
//  const ptrdiff_t offset = lastdelim - strptr;
//  String base = lastdelim ?
//    basename.getSubString((int)(offset + 1), -1) :
//    basename;
//
//  for (i = 0; i < directories.getLength(); i++) {
//    String dirname = (directories.operator_square_bracket(i).getString());
//    int dirlen = dirname.length();
//
//    if (dirlen > 0 &&
//        dirname[dirlen-1] != '/' &&
//        dirname[dirlen-1] != '\\' &&
//        dirname[dirlen-1] != ':') {
//      dirname += "/";
//    }
//    fullname.sprintf("%s%s", dirname.getString(),
//                     base.getString());
//    if (test_filename(fullname)) return fullname;
//    for (int j = 0; j < subdirectories.getLength(); j++) {
//      fullname.sprintf("%s%s/%s", dirname.getString(),
//                       subdirectories[j]->getString(),
//                       base.getString());
//      if (test_filename(fullname)) return fullname;
//    }
//  }
  // none found
  return new String("");
}


}
