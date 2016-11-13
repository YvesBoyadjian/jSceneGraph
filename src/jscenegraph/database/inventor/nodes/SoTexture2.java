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
 |      This file defines the SoTexture node class.
 |
 |   Author(s)          : John Rohlf, Thad Beier, Gavin Bell
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.nodes;

import com.jogamp.opengl.GL2;

import jscenegraph.database.inventor.SbColor;
import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.SoInput;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.actions.SoAction;
import jscenegraph.database.inventor.actions.SoCallbackAction;
import jscenegraph.database.inventor.actions.SoGLRenderAction;
import jscenegraph.database.inventor.elements.SoGLCacheContextElement;
import jscenegraph.database.inventor.elements.SoGLDisplayList;
import jscenegraph.database.inventor.elements.SoGLTextureEnabledElement;
import jscenegraph.database.inventor.elements.SoGLTextureImageElement;
import jscenegraph.database.inventor.elements.SoShapeStyleElement;
import jscenegraph.database.inventor.elements.SoTextureImageElement;
import jscenegraph.database.inventor.elements.SoTextureOverrideElement;
import jscenegraph.database.inventor.elements.SoTextureQualityElement;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.fields.SoFieldData;
import jscenegraph.database.inventor.fields.SoSFColor;
import jscenegraph.database.inventor.fields.SoSFEnum;
import jscenegraph.database.inventor.fields.SoSFImage;
import jscenegraph.database.inventor.fields.SoSFString;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.sensors.SoFieldSensor;
import jscenegraph.database.inventor.sensors.SoSensor;
import jscenegraph.database.inventor.sensors.SoSensorCB;
import jscenegraph.port.FILE;


///////////////////////////////////////////////////////////////////////////////
///
////  \class SoTexture
///
///  Texture node.
///
//////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//! Texture mapping node.
/*!
\class SoTexture2
\ingroup Nodes
This property node defines a texture map and parameters for that map. This
map is used to apply texture to subsequent shapes as they are rendered.


The texture can be read from the file specified by the \b filename 
field. Once the texture has been read, the \b image  field contains
the texture data. However, this field is marked so the image is not
written out when the texture node is written to a file.  To turn off
texturing, set the \b filename  field to an empty string ("").


Textures can also be specified in memory by setting the \b image 
field to contain the texture data. Doing so resets the \b filename  to
the empty string.


If the texture image's width or height is not a power of 2, or the
image's width or height is greater than the maximum supported by
OpenGL, then the image will be automatically scaled up or down to the
next power of 2 or the maximum texture size.  For maximum speed,
point-sampling is used to do the scale; if you want more accurate
resampling, pre-filter images to a power of 2 smaller than the maximum
texture size (use the OpenGL glGetIntegerv(GL_MAX_TEXTURE_SIZE...)
call to determine maximum texture for a specific OpenGL
implementation).


The quality of the texturing is affected by the \b textureQuality 
field of the SoComplexity node.  The \b textureQuality  field
affects what kind of filtering is done to the texture when it must be
minified or magnified.  The mapping of a particular texture quality
value to a particular OpenGL filtering technique is implementation
dependent, and varies based on the texturing performance.
If mipmap filtering is required, mipmaps are automatically created
using the simple box filter.

\par File Format/Default
\par
\code
Texture2 {
  filename 
  image 0 0 0
  wrapS REPEAT
  wrapT REPEAT
  model MODULATE
  blendColor 0 0 0
}
\endcode

\par Action Behavior
\par
SoGLRenderAction, SoCallbackAction
<BR> Sets current texture in state. 

\par See Also
\par
SoComplexity, SoMaterial, SoTexture2Transform, SoTextureCoordinate2, SoTextureCoordinateBinding, SoTextureCoordinateFunction
*/
////////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public class SoTexture2 extends SoNode {

	private final SoSubNode nodeHeader = SoSubNode.SO_NODE_HEADER(SoTexture2.class,this);
	   
	   public                                                                     
	    static SoType       getClassTypeId()        /* Returns class type id */   
	                                    { return SoSubNode.getClassTypeId(SoTexture2.class);  }                   
	  public  SoType      getTypeId()      /* Returns type id      */
	  {
		  return nodeHeader.getClassTypeId();
	  }
	  public                                                                  
	    SoFieldData   getFieldData()  {
		  return nodeHeader.getFieldData();
	  }
	  public  static SoFieldData[] getFieldDataPtr()                              
	        { return SoSubNode.getFieldDataPtr(SoTexture2.class); }    
	
	  
  public
    //! Texture model
    enum Model {
        MODULATE                ( GL2.GL_MODULATE),
        DECAL                   ( GL2.GL_DECAL),
        BLEND                   ( GL2.GL_BLEND);
        
        Model(int value) {
        	this.value = value;
        }
        
        private int value;
        
        public int getValue() {
        	return value;
        }
    };

    //! Texture wrap type
    public enum Wrap {
        REPEAT                  ( GL2.GL_REPEAT),
        CLAMP                   ( GL2.GL_CLAMP);
        
        Wrap(int value) {
        	this.value = value;
        }
        private int value;
        
        public int getValue() {
        	return value;
        }
    };

    //! Fields.
    public final SoSFString          filename = new SoSFString();       //!< file to read texture from
    public final SoSFImage           image = new SoSFImage();          //!< The texture
    public final SoSFEnum            wrapS = new SoSFEnum();
    public final SoSFEnum            wrapT = new SoSFEnum();
    public final SoSFEnum            model = new SoSFEnum();
    public final SoSFColor           blendColor = new SoSFColor();

  private
    //! These keep the image and filename fields in sync.
    SoFieldSensor      imageSensor;
    private final static SoSensorCB         imageChangedCB = new SoSensorCB() {

		@Override
		public void run(Object data, SoSensor sensor) {			
			imageChangedCB(data,sensor);
		}
    	
    };
    private SoFieldSensor      filenameSensor;
    private final static SoSensorCB         filenameChangedCB = new SoSensorCB() {

		@Override
		public void run(Object data, SoSensor sensor) {
			filenameChangedCB(data,sensor);
		}
    	
    };
    
    int                 readStatus;
    

    //! Display list info for this texture:
    private SoGLDisplayList renderList;
    private float       renderListQuality;
	  
	
////////////////////////////////////////////////////////////////////////
//
// Description:
//    This initializes the SoTexture2 class.
//
// Use: internal

public static void
initClass()
//
////////////////////////////////////////////////////////////////////////
{
	SoSubNode.SO__NODE_INIT_CLASS(SoTexture2.class, "Texture2", SoNode.class);

    SO_ENABLE(SoGLRenderAction.class, SoGLTextureImageElement.class);
    SO_ENABLE(SoCallbackAction.class, SoTextureImageElement.class);
    SO_ENABLE(SoGLRenderAction.class, SoTextureOverrideElement.class);
    SO_ENABLE(SoCallbackAction.class, SoTextureOverrideElement.class);
    SO_ENABLE(SoGLRenderAction.class, SoGLTextureEnabledElement.class);
    SO_ENABLE(SoGLRenderAction.class, SoShapeStyleElement.class);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Constructor
//
// Use: public

public SoTexture2()
//
////////////////////////////////////////////////////////////////////////
{
    nodeHeader.SO_NODE_CONSTRUCTOR(/*SoTexture2*/);

    nodeHeader.SO_NODE_ADD_SFIELD(filename,"filename", (""));
    nodeHeader.SO_NODE_ADD_SFIELD(image,"image", /*(new SbVec2s(0, 0), 0, 0)*/null);
    nodeHeader.SO_NODE_ADD_SFIELD(wrapS,"wrapS", (Wrap.REPEAT.getValue()));
    nodeHeader.SO_NODE_ADD_SFIELD(wrapT,"wrapT", (Wrap.REPEAT.getValue()));
    nodeHeader.SO_NODE_ADD_SFIELD(model,"model", (Model.MODULATE.getValue()));
    nodeHeader.SO_NODE_ADD_SFIELD(blendColor,"blendColor", (new SbColor(0,0,0)));

    // Set up enumerations for texture model
    nodeHeader.SO_NODE_DEFINE_ENUM_VALUE(Model.MODULATE);
    nodeHeader.SO_NODE_DEFINE_ENUM_VALUE(Model.DECAL);
    nodeHeader.SO_NODE_DEFINE_ENUM_VALUE(Model.BLEND);

    nodeHeader.SO_NODE_DEFINE_ENUM_VALUE(Wrap.REPEAT);
    nodeHeader.SO_NODE_DEFINE_ENUM_VALUE(Wrap.CLAMP);
    
    // Set up info in enumerated type field
    nodeHeader.SO_NODE_SET_SF_ENUM_TYPE(model,"model", "Model");
    nodeHeader.SO_NODE_SET_SF_ENUM_TYPE(wrapS,"wrapS", "Wrap");
    nodeHeader.SO_NODE_SET_SF_ENUM_TYPE(wrapT,"wrapT", "Wrap");

    // Set up sensors to keep the image/filename fields agreeing.
    // Sensors are used instead of field to field connections or raw
    // notification so that the fields can still be attached to/from
    // other fields.
    imageSensor = new SoFieldSensor(imageChangedCB, this);
    imageSensor.setPriority(0);
    imageSensor.attach(image);
    filenameSensor = new SoFieldSensor(filenameChangedCB, this);
    filenameSensor.setPriority(0);
    filenameSensor.attach(filename);

    renderList = null;  // Display list used for rendering

    isBuiltIn = true;
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Destructor
//
// Use: private

public void destructor()
//
////////////////////////////////////////////////////////////////////////
{
    if (renderList != null) {
        renderList.unref();
        renderList = null;
    }
    imageSensor.destructor();
    filenameSensor.destructor();
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Callback that sets the filename to "" whenever the image field
//    is changed.
//
// Use: static, internal

private static void imageChangedCB(Object data, SoSensor sensor)
//
////////////////////////////////////////////////////////////////////////
{
    SoTexture2 tex = (SoTexture2 )data;

    if (tex.image.isIgnored()) return;

    tex.filenameSensor.detach();
    tex.filename.setValue("");
    tex.filename.setDefault(true);
    tex.filenameSensor.attach(tex.filename);

    if (tex.renderList != null) {
        tex.renderList.unref();
        tex.renderList = null;
    }
}

    protected int             getReadStatus()             { return readStatus; }
    protected void            setReadStatus(int s)        { readStatus = s; }


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Callback that reads in the given file and stuffs it into the
//    image field whenever the filename field changes.
//
// Use: static, internal

private static void
filenameChangedCB(Object data, SoSensor sensor)
//
////////////////////////////////////////////////////////////////////////
{
    SoTexture2 tex = (SoTexture2 )data;

    if (tex.filename.isIgnored()) {
        tex.setReadStatus(/*false*/0);
        return;
    }

    // Read in image file right away...
    final int[] nx = new int[1], ny = new int[1], nc = new int[1];
    final byte[][] bytes = new byte[1][];
    boolean result = readImage(tex.filename.getValue(), nx, ny, nc, bytes);
    if (!result) {
        // Read error is taken care of by readImage() call
        nx[0] = ny[0] = nc[0] = 0;
        bytes[0] = null;
    }
    // Detach the image sensor temporarily...
    tex.imageSensor.detach();

    // Set the image to the right value:
    tex.image.setValue(new SbVec2s((short)nx[0], (short)ny[0]), nc[0], bytes[0]);

    // And set its default bit so it isn't written out
    tex.image.setDefault(true);

    if (bytes[0] != null) { /*delete [] bytes;*/bytes[0] = null;} //java port

    if (tex.renderList != null) {
        tex.renderList.unref();
        tex.renderList = null;
    }
    tex.imageSensor.attach(tex.image);

    tex.setReadStatus(result ? 1 :0);
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Performs typical action
//
// Use: extender

private void
SoTexture2_doAction(SoAction action)
//
////////////////////////////////////////////////////////////////////////
{
    SoState    state = action.getState();

    if (image.isIgnored() ||
        SoTextureOverrideElement.getImageOverride(state))
        return; // Texture being overriden or this node ignored
    if (isOverride()) {
        SoTextureOverrideElement.setImageOverride(state, true);
    }

    final SbVec2s size = new SbVec2s();
    final int[] nc = new int[1];
    byte[] bytes = image.getValue(size, nc);
    
    SoTextureImageElement.set(state, this, size, nc[0], bytes,
                               wrapS.getValue(), wrapT.getValue(),
                               model.getValue(), blendColor.getValue());
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Performs callback action
//
// Use: extender

public void
callback(SoCallbackAction action)
//
////////////////////////////////////////////////////////////////////////
{
    SoTexture2_doAction(action);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Performs GL rendering on a texture node.
//
// Use: extender

public void
GLRender(SoGLRenderAction action)
//
////////////////////////////////////////////////////////////////////////
{
    SoState    state = action.getState();

    if (image.isIgnored() ||
        SoTextureOverrideElement.getImageOverride(state))
        return; // Texture being overriden or this node ignored
    if (isOverride()) {
        SoTextureOverrideElement.setImageOverride(state, true);
    }

    final SbVec2s size = new SbVec2s();
    final int[] nc = new int[1];
    byte[] bytes = image.getValue(size, nc);
    int numBytes = size.getValue()[0]*size.getValue()[1]*nc[0];

    float texQuality = SoTextureQualityElement.get(state);
    if (texQuality == 0 || numBytes == 0 || image.isIgnored()) {
        SoGLTextureEnabledElement.set(state, false);
        return;
    }
    else {
        SoGLTextureEnabledElement.set(state, true);
    }   

    // Check for special cases of 1/2 component texture and model
    // DECAL or 3/4 component texture and model BLEND; print out
    // errors in these cases:
        
    int m = model.getValue();
    if (nc[0] < 3 && m == Model.DECAL.getValue()) {
//#ifdef DEBUG
        SoDebugError.post("SoTexture2.GLRender",
                           "Texture model is DECAL, but texture image"+
                           " has only "+nc+" components (must be 3 or 4).  "+
                           "Use imgcopy to convert the image.");
//#endif      
        SoGLTextureEnabledElement.set(state, false);
    }
    else if (nc[0] > 2 && m == Model.BLEND.getValue()) {
//#ifdef DEBUG
        SoDebugError.post("SoTexture2.GLRender",
                           "Texture model is BLEND, but texture image"+
                           " has "+nc+" components (must be 1 or 2).  "+
                           "Use imgcopy to convert the image.");
//#endif      
        SoGLTextureEnabledElement.set(state, false);
    } else {
        // This is kind of weird-- the element builds and uses the
        // display list (which is why we pass it in and assign
        // it) because it sends the GL calls, and needs to know
        // the list if the state is popped.  But this node must
        // manage storage and deletion of the display list, since
        // the list must go away if the node is deleted or the
        // image is changed.

        // See if renderList is valid (in the right context, with
        // the right texture quality):
        GL2 context = SoGLCacheContextElement.get(state);
        if (renderList != null && renderList.getContext() == context &&
            texQuality == renderListQuality) {
            SoGLTextureImageElement.set(
                state, this, size, nc[0], bytes, texQuality,
                wrapS.getValue(), wrapT.getValue(),
                m, blendColor.getValue(), renderList);
        }  // Not valid, try to build
        else {
            // Free up old list, if necessary:
            if (renderList != null) {
                renderList.unref(state);
                renderList = null;
            }
            renderList = SoGLTextureImageElement.set(
                state, this, size, nc[0], bytes, texQuality,
                wrapS.getValue(), wrapT.getValue(),
                m, blendColor.getValue(), null);
            if (renderList != null)
                renderList.ref();
            renderListQuality = texQuality;
        }
    }
}


////////////////////////////////////////////////////////////////////////
//
// Description:
//    read passed image file
//
// Use: static, protected

static boolean readImage(final String fname, final int[] w, final int[] h, final int[] nc, 
                      final byte[][] bytes)
//
////////////////////////////////////////////////////////////////////////
{
    w[0] = h[0] = nc[0] = 0;
    bytes[0] = null;
    
    // Empty file means an empty image...
    if (fname.isEmpty()) // java port
        return true;

    final SoInput in = new SoInput();
    if (!in.openFile(fname, true)) {
        return false;
    }

//#ifdef DEBUG
    SoDebugError.postInfo("SoTexture2::readImage",
                           "Reading texture image "+ fname);
//#endif

/* Florian Link: Disabled RGB image loading, because the libimage uses close
   on the fileno of the fopen stream, which crashes in fclose of SoInput

    if (ReadSGIImage(in, w, h, nc, bytes))
        return TRUE;

    // fiopen() closes the file even if it can't read the data, so 
    // reopen it
    in.closeFile();
    if (!in.openFile(fname.getString(), TRUE))
        return FALSE;
*/
    if (ReadGIFImage(in, w, h, nc, bytes))
        return true;

    if (ReadJPEGImage(in.getCurFile(), w, h, nc, bytes)!=0)
        return true;

    return false;
}

private static boolean ReadGIFImage(final SoInput in, final int[] w, final int[] h, final int[] nc,  
        byte[][] bytes) {
	
	//TODO
	return false;
}
private static int ReadJPEGImage(FILE infile, final int[] w, final int[] h, final int[] nc,  
        byte[][] bytes) {
	
	//TODO
	return 0;
}
}
