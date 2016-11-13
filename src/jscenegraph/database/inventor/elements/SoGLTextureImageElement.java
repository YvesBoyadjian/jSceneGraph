/**
 * 
 */
package jscenegraph.database.inventor.elements;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import jscenegraph.database.inventor.SbColor;
import jscenegraph.database.inventor.SbVec2s;
import jscenegraph.database.inventor.errors.SoDebugError;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.database.inventor.nodes.SoNode;

/**
 * @author Yves Boyadjian
 *
 */
public class SoGLTextureImageElement extends SoTextureImageElement {

    private SoGLDisplayList     list;
    private float               quality;

// Formats for 1-4 component textures
static int formats[] = {
    GL2.GL_LUMINANCE,
    GL2.GL_LUMINANCE_ALPHA,
    GL2.GL_RGB,
    GL2.GL_RGBA
};

	
    
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Initializes element.
//
// Use: public

public void init(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
    // Initialize base class stuff
    super.init(state);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Pops element, causing side effects in GL.
//
// Use: public

public void pop(SoState state, SoElement element)
//
////////////////////////////////////////////////////////////////////////
{
    // Empty texture, don't need to do anything-- the
    // GLTextureEnabledElement will turn off texturing.
    if (size.getValue()[0] == 0 || size.getValue()[1] == 0 || numComponents == 0) {
        return;
    }
    // Since popping this element has GL side effects, make sure any
    // open caches capture it
    capture(state);

    // Restore previous texture image (if any)
    sendTexEnv(state);
    sendTex(state);
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sets image.
//
// Use: protected, virtual

protected void setElt(final SbVec2s _size, 
        int _numComponents,
        byte[] _bytes,
        int _wrapS, int _wrapT, int _model,
        final SbColor _blendColor)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    SoDebugError.post("SoGLTextureImageElement.setElt",
               "Nodes must call SoGLTextureImageElement.set"+
               " for GLRender, not SoTextureImageElement.set");
//#endif
}

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Does the right GL stuff.  This takes a GL display list that can
//    be used to render the texture; if -1 is passed in as the display
//    list, this will try to build a display list (if there are none
//    already open) and returns the display list, which must be freed
//    by the node that sets this element.
//
// Use: public, static

public static SoGLDisplayList set(SoState state, SoNode node,
                             final SbVec2s _size, int _numComponents,
                             byte[] _bytes,
                             float _quality, int _wrapS, int _wrapT,
                             int _model, final SbColor _blendColor,
                             SoGLDisplayList _list)
//
////////////////////////////////////////////////////////////////////////
{
    SoGLTextureImageElement     elt;

    // Get an instance we can change (pushing if necessary)
    elt = (SoGLTextureImageElement ) getElement(state, classStackIndexMap.get(SoGLTextureImageElement.class), node);

    if (elt != null) {
        elt.SoTextureImageElement_setElt(_size, _numComponents,
                                           _bytes, _wrapS, _wrapT,
                                           _model, _blendColor);

        elt.list = _list;
        elt.quality = _quality;
        elt.sendTexEnv(state);
        elt.sendTex(state);
        return elt.list;
    }
    return null;
}

// Helper table; for integers 1-15, returns the high-bit (0-3):
static int[] powTable = {
    -1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3 };

//
// Helper routine; given an integer, return next higher power of 2.
// This is convoluted, but pretty fast (about 1.6 times faster than
// a naive bit-shifting algorithm).
//
  static int nextPowerOf2(int num)
{
//#ifdef DEBUG
    if (num <= 0) {
        SoDebugError.post("SoGLTextureImageElement.nextPowerOf2",
                           "size <= 0");
        return 0;
    }
//#endif
    int t, bits = 0;
    int mask = 0xF;
    // Find closest to 4-bits:
    for (t = num-1; (t & (~mask)) != 0; bits += 4, t = t>>4) ;

    // Find high-bit:
    bits += powTable[t];
    return bits + 1;
}

//
// Helper routine stolen from the gluBuild2DMipMaps code:
//
// Compute the nearest power of 2 number.  This algorithm is a little 
// strange, but it works quite well.
//
  static int nearestPowerOf2(int value)
{
    int i = 0;

//#ifdef DEBUG
    if (value <= 0) {
        SoDebugError.post("SoGLTextureImageElement.nextPowerOf2",
                           "size <= 0");
        return 0;
    }
//#endif

    for (;;) {
        if (value == 1) {
            return i;
        } else if (value == 3) {
            return i+2;
        }
        value = value >> 1;
        i++;
    }
}

//
// Helper table; mapping from textureQuality to OpenGL filter type:
//
static class qualityFilterTable {
    float quality;
    int filter;
    boolean needMipMaps;
    
    public qualityFilterTable(    float quality,
    int filter,
    boolean needMipMaps
) {
        this.quality = quality;
        this.filter = filter;
        this.needMipMaps = needMipMaps;    	
    }
};

//
// Filter modes depending on quality settings:
//
static qualityFilterTable mipmap_minQFTable[] = {
    new qualityFilterTable( 0.1f, GL2.GL_NEAREST, false),
    new qualityFilterTable( 0.5f, GL2.GL_LINEAR, false),
    new qualityFilterTable( 0.7f, GL2.GL_NEAREST_MIPMAP_NEAREST, true),
    new qualityFilterTable( 0.8f, GL2.GL_NEAREST_MIPMAP_LINEAR, true),
    new qualityFilterTable( 0.9f, GL2.GL_LINEAR_MIPMAP_NEAREST, true),
    new qualityFilterTable( Float.MAX_VALUE, GL2.GL_LINEAR_MIPMAP_LINEAR, true)
};


////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sends texture environment.  This is separated from send()
//    because texture objects do NOT store the texture environment
//    state.
//
// Use: private

private void sendTexEnv(SoState state)
//
////////////////////////////////////////////////////////////////////////
{
	GL2 gl2 = state.getGL2();
	
    // This state isn't stored in a texture object:
    gl2.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, model);
    if (model == GL2.GL_BLEND) {
        gl2.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR,
                   blendColor.getValue(),0);
    }
}

// Helper macro for indexing:
private final int I(int w,int h,int b, SbVec2s prevSize) {
	return (b + (w + (h)*prevSize.getValue()[0])*numComponents);
}

	static boolean useNonPowTwoTextures;

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Sends down a 2D texture.  Builds or uses a display list, if it
//    can.
//
// Use: private

static {
	useNonPowTwoTextures = (System.getenv("IV_NO_NPOT")==null) /*&& 
(GLEW_VERSION_2_0 || GLEW_ARB_texture_non_power_of_two) &&
// only allow non-pow-two when glGenerateMipmap is available:
(GLEW_VERSION_3_0 || GLEW_ARB_framebuffer_object)*/;
}

private void sendTex(SoState state)
//
////////////////////////////////////////////////////////////////////////
{

    if (list != null) {
        // use display list
        list.call(state);
        return;
    }
    
    GL2 gl2 = state.getGL2();

    // Scale the image to closest power of 2 smaller than maximum
    // texture size:
    final int maxsize[] = new int[1];
    gl2.glGetIntegerv(GL2.GL_MAX_TEXTURE_SIZE, maxsize,0);
    final SbVec2s newSize = new SbVec2s();
    if (useNonPowTwoTextures) {
      // make sure to only use GL_MAX_TEXTURE_SIZE
      for (int i = 0; i < 2; i++) {
        if (size.getValue()[i] > maxsize[0]) {
          newSize.getValue()[i] = (short)maxsize[0];
        } else {
          newSize.getValue()[i] = size.getValue()[i];
        }
      }
    } else {
      // Use nearest power of 2 for big textures, use next higher
      // power of 2 for small textures:
      for (int i = 0; i < 2; i++) {
        if (size.getValue()[i] > 8) {
          newSize.getValue()[i] = (short)(size.getValue()[i] > maxsize[0] ?
                                 maxsize[0] : 1 << nearestPowerOf2(size.getValue()[i]));
        } else {
          newSize.getValue()[i] = (short)(1 << nextPowerOf2(size.getValue()[i]));
        }
      }
    }


    int minFilter, magFilter;
    boolean needMipMaps = false;
    qualityFilterTable[] tbl = mipmap_minQFTable;

    int i;
    for (i = 0; quality > tbl[i].quality; i++) /* Do nothing */;
    minFilter = tbl[i].filter;
    needMipMaps = tbl[i].needMipMaps;
    magFilter = (quality < 0.5 ? GL2.GL_NEAREST : GL2.GL_LINEAR);
    
    gl2.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);  // Not default
    
    // Format in memory
    int format = formats[numComponents-1];
    int internalFormat = formats[numComponents-1];
    
    boolean buildList = !SoCacheElement.anyOpen(state);
    if (buildList) {
        list = new SoGLDisplayList(state,
                                   SoGLDisplayList.Type.TEXTURE_OBJECT);
        list.open(state);
    }

    // If we aren't creating a texture object, then we need to 
    // unbind the current texture object so we don't overwrite it's state.
    if (!buildList) {
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }

    // These need to go inside the display list or texture object
    gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, magFilter);
    gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, minFilter);
    gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, wrapS);
    gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, wrapT);
    
    byte[] level0 = null;
    // scale the image if the new size is different from the original size:
    if (newSize != size) {
        level0 = (byte [])
            new byte[newSize.getValue()[0]*newSize.getValue()[1]*numComponents/**sizeof(GLubyte)*/];

        // Use gluScaleImage (which does linear interpolation or box
        // filtering) if using a linear interpolation magnification
        // filter:
        GLU glu = GLU.createGLU(gl2);
        glu.gluScaleImage(
            format, size.getValue()[0], size.getValue()[1], GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes),
            newSize.getValue()[0], newSize.getValue()[1], GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(level0));
    }
    
    // Send level-0 mipmap:
    gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, internalFormat, newSize.getValue()[0], newSize.getValue()[1],
                 0, format, GL2.GL_UNSIGNED_BYTE,
                 level0 == null ? ByteBuffer.wrap(bytes) : ByteBuffer.wrap(level0));
    
    // If necessary, send other mipmaps:
    if (needMipMaps) {
      if (useNonPowTwoTextures) {
        // use the GL driver to generate the mip maps,
        // this supports non-power-of-two as well:
        gl2.glGenerateMipmap(GL2.GL_TEXTURE_2D);
      } else {
        // create mip maps manually, this code can only handle
        // power-of-two images:
        byte[] prevLevel = null;
        if (level0 == null) {
          level0 = (byte[] )
            new byte[newSize.getValue()[0]*newSize.getValue()[1]*numComponents/**sizeof(byte)*/];
          prevLevel = bytes;
        } else {
          prevLevel = level0;
        }

        int level = 0;
        final SbVec2s curSize = new SbVec2s(newSize);
        while (curSize.getValue()[0] > 1 || curSize.getValue()[1] > 1) {
          ++level;
          final SbVec2s prevSize = new SbVec2s(curSize);

          // When we're box-filtering, we average the 4 pixels
          // [(curSize),(curSize+deltas)].  If mipmaps have already
          // bottomed out for a dimension, the delta will be 0,
          // otherwise the delta will be 1.
          final SbVec2s deltas = new SbVec2s();
          if (curSize.getValue()[0] > 1) {
            curSize.getValue()[0] = (short)(curSize.getValue()[0] >> 1);
            deltas.getValue()[0] = 1;
          } else {
            deltas.getValue()[0] = 0;
          }
          if (curSize.getValue()[1] > 1) {
            curSize.getValue()[1] = (short)(curSize.getValue()[1] >> 1);
            deltas.getValue()[1] = 1;
          } else {
            deltas.getValue()[1] = 0;
          }

          int bytes = 0;
          for (int h = 0; h < prevSize.getValue()[1]; h += (deltas.getValue()[1]+1)) {
            for (int w = 0; w < prevSize.getValue()[0]; w += (deltas.getValue()[0]+1)) {
              for (int b = 0; b < numComponents; b++) {


                level0[bytes] = (byte)(
                  (prevLevel[I(w,h,b,prevSize)] +
                  prevLevel[I(w,h+deltas.getValue()[1],b,prevSize)] +
                  prevLevel[I(w+deltas.getValue()[0],h,b,prevSize)] +
                  prevLevel[I(w+deltas.getValue()[0],h+deltas.getValue()[1],b,prevSize)]) / 4);
//#undef I
                bytes++;
              }
            }
          }
          // Send level-N mipmap:
          gl2.glTexImage2D(GL2.GL_TEXTURE_2D, level, internalFormat,
            curSize.getValue()[0], curSize.getValue()[1],
            0, format, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(level0));
          prevLevel = level0;
        }
      }
    }

    if (buildList) {
        list.close(state);
    }

    if (level0 != null) {
      //free(level0); java port
    }

    gl2.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 4);  // Reset to default
}
}
