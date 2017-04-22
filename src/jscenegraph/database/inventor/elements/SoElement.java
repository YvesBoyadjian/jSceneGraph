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
 |      This file defines the abstract base SoEXTENDER SoElement class.
 |
 |   Classes:
 |      SoElement
 |
 |   Author(s)          : Paul S. Strauss
 |
 ______________  S I L I C O N   G R A P H I C S   I N C .  ____________
 _______________________________________________________________________
 */

package jscenegraph.database.inventor.elements;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import jscenegraph.database.inventor.SbName;
import jscenegraph.database.inventor.SoType;
import jscenegraph.database.inventor.SoType.CreateMethod;
import jscenegraph.database.inventor.SoTypeList;
import jscenegraph.database.inventor.misc.SoState;
import jscenegraph.mevis.inventor.elements.SoGLVBOElement;
import jscenegraph.port.Destroyable;

///////////////////////////////////////////////////////////////////////////////
///
///  \class SoElement
///  \ingroup Elements
///
///  This is the abstract base class for all state elements. This class
///  defines the following features for all of its derived classes:
///
///      Type identifiers. Each class of element has a unique (static)
///      SoType identifier. The correct type id is also stored in each
///      instance for easy access.
///
///      Copying.  Elements are copied into the list of elements used
///      in an SoCache.  Performing any operation other than matches()
///      on a copied element is not guaranteed to work (and will not
///      work for things like Coordinates, if the coordinate they were
///      pointing to has been deleted).
///
///      Stack indices. Since a subclass of an element class needs to
///      be in the same state stack as the class from which it is
///      derived, stack indices are inherited.
///
///      Capturing. Each time an element's value is accessed, that
///      element is added to the elements-used list of all currently
///      open caches.
///
//////////////////////////////////////////////////////////////////////////////

/**
 * @author Yves Boyadjian
 *
 */
public abstract class SoElement implements Destroyable {
	
	   protected static final Map<Class,Integer> classStackIndexMap = new HashMap<Class,Integer>();
	   protected static final Map<Class,SoType> classTypeIdMap = new HashMap<Class,SoType>();
	   
	   // java port
	   private SoState pushedState;
	   
	   public static SoType getClassTypeId(Class klass) {
		   return classTypeIdMap.get(klass);
	   }
	   
	     //! Returns type identifier for element instance
	   public     SoType              getTypeId() { return typeId; }	    
	   	   
	   public static int getClassStackIndex(Class klass) {
		   Integer index = classStackIndexMap.get(klass);
		   return index;
	   }
		  
	   //
	    // Description:
	    //    Creates and returns a new stack index for an element class to
	    //    use within SoState instances. It is passed the type id of the
	    //    element class so we can correlate stack indices to type id's for
	    //    debugging purposes.
	    //
	    // Use: internal protected
	    
	protected static int createStackIndex(SoType id)
	    //
	    {
	        int stackIndex = nextStackIndex++;
	    
//	    #ifdef DEBUG
	        // Store id in list so we can get it from stack index later
	        stackToType.set(stackIndex, id);
//	    #endif
	    
	        return stackIndex;
	    }
	   
private      SoType              typeId;         
		    
private        static int          nextStackIndex; 
private        static SoTypeList   stackToType;   
private        int                 stackIndex;     
		    
		    
		    
private        SoElement           nextInStack;   
private        SoElement           nextFree;      
		    
private        SoElement           next;          
private        int                 depth;


	public SoElement() {
		// SO__ELEMENT_METHODS
	    setTypeId(getClassTypeId(this.getClass()));                                                   
	    setStackIndex(getClassStackIndex(this.getClass()));                                           	  		
	}

    //! Returns an instance of an element from the stack with the given
      //! index in the given state. This instance is writeable. This
      //! returns NULL if no writable instance can be returned.
    public  static SoElement getElement(SoState state, int stackIndex)
          { return state.getElement(stackIndex); }
  
 	
	/**
	 * Initializes element. 
	 * Called for first element of its kind in stack. 
	 * Default method does nothing. 
	 * 
	 * 
	 * @param state
	 */
	public void init(SoState state) {
	}
	
	public void push(SoState state) {
		// java port
		pushedState = state;
	}
	
	public void pop(SoState state, SoElement prevTopElement) {
		
	}
	
	// Returns the stack index for an element instance. 
	public int getStackIndex() {
		 return stackIndex; 
	}
		    	
	// Sets stuff in an element instance. 
	public void setDepth(int dpth) {
		 depth = dpth;
	}
	
	public void setNext(SoElement nxt) {
		 next = nxt; 
	}
	
	public void setNextInStack(SoElement nxt) {
		nextInStack = nxt;
	}
	
	public void setNextFree(SoElement nxt) {
		 nextFree = nxt; 
	}
	
	// Returns stuff from element instance. 
	public int getDepth() {
		 return depth; 
	}
	
	public SoElement getNext() {
		 return next; 
	}

	// Returns the number of stack indices allocated. 
	public static int getNumStackIndices() {
		 return nextStackIndex; 
	}
	
	// Sets typeId in instance. 
	protected final void setTypeId(SoType id) {
		typeId = id;
	}
	
	// Sets stackIndex in instance. 
	protected final void setStackIndex(int index) {
		stackIndex = index; 
	}
	
	// Returns next instance in specific element stack. 
	public SoElement getNextInStack() {
		 return nextInStack; 
	}
	
	// Returns next free element in a specific element stack. 
	public SoElement getNextFree() {
		 return nextFree; 
	}

	 /////////////////////////////////////////////////////////////////////////
	   ///
	   /// Description:
	   ///      Returns a read-only pointer to the top instance in the given
	   ///      element stack.  inline to speed up traversal.
	   ///
	   /// Use: protected
	   
	  public static SoElement getConstElement(SoState state,
	                                                      int stackIndex)
	   //!
	   /////////////////////////////////////////////////////////////////////////
	   {
	       SoElement elt = (state.getConstElement(stackIndex));
	       elt.capture(state);
	       return elt;
	   }	
	
	     //! Does whatever is necessary in state to capture this element for
	       //! caching purposes. Should be called by subclasses whenever
	       //! any value in the element is accessed.
	  public     void                capture(SoState state)
	           { if (state.isCacheOpen()) captureThis(state); }
	  
	  ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    "Captures" this element for caching purposes. The element is
	   //    added to all currently open caches, using the SoCacheElement.
	   //
	   // Use: virtual, protected
	   
	  public void
	   captureThis(SoState state)
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       SoCacheElement.addElement(state, this);
	   }	  
	  
    //! Returns TRUE if the element matches another element (of the
    //! same class, presumably) with respect to cache validity.  If you
    //! write a matches() method, you must also write a copy() method.
    public abstract boolean      matches(final SoElement elt);

	     //! Create a copy that we can put in a cache used list and call
	       //! matches() on later.
	  public abstract SoElement   copyMatchInfo();
	   	  
	   	  
	// Initialize ALL Inventor element classes. 
	public static void initElements() {
		//TODO
		
	     // Initialize base classes first
		        SoElement.initClass(SoElement.class);
		        SoAccumulatedElement.SoAccumulatedElement_initClass(SoAccumulatedElement.class);
		        SoReplacedElement.SoReplacedElement_initClass(SoReplacedElement.class);
		        SoInt32Element.SoInt32Element_initClass(SoInt32Element.class);
		        SoFloatElement.SoFloatElement_initClass(SoFloatElement.class);
		    
		        // Initialize derived classes
		        SoCacheElement.initClass(SoCacheElement.class);
		        SoClipPlaneElement.initClass(SoClipPlaneElement.class);
		        SoComplexityElement.initClass(SoComplexityElement.class);
		        SoComplexityTypeElement.initClass(SoComplexityTypeElement.class);
		        SoCoordinateElement.initClass(SoCoordinateElement.class);
		        SoCreaseAngleElement.initClass(SoCreaseAngleElement.class);
		        SoDrawStyleElement.initClass(SoDrawStyleElement.class);
		        SoFocalDistanceElement.initClass(SoFocalDistanceElement.class);
		        SoFontNameElement.initClass(SoFontNameElement.class);
		        SoFontSizeElement.initClass(SoFontSizeElement.class);
		        // We must put this before Lazy Element:
		        SoShapeStyleElement.initClass(SoShapeStyleElement.class);
		        SoLazyElement.initClass(SoLazyElement.class);
		        SoLightAttenuationElement.initClass(SoLightAttenuationElement.class);
		        SoLinePatternElement.initClass(SoLinePatternElement.class);
		        SoLineWidthElement.initClass(SoLineWidthElement.class);
		        SoMaterialBindingElement.initClass(SoMaterialBindingElement.class);
		        SoModelMatrixElement.initClass(SoModelMatrixElement.class);
		        SoNormalBindingElement.initClass(SoNormalBindingElement.class);
		        SoNormalElement.initClass(SoNormalElement.class);
		        SoOverrideElement.initClass(SoOverrideElement.class);
		        SoPickRayElement.initClass(SoPickRayElement.class);
		        SoPickStyleElement.initClass(SoPickStyleElement.class);
		       SoPointSizeElement.initClass(SoPointSizeElement.class);
		       SoProfileCoordinateElement.initClass(SoProfileCoordinateElement.class);
		       SoProfileElement.initClass(SoProfileElement.class);
		       SoProjectionMatrixElement.initClass(SoProjectionMatrixElement.class);
		       SoShapeHintsElement.initClass(SoShapeHintsElement.class);
		       SoSwitchElement.initClass(SoSwitchElement.class);
		       SoTextureCoordinateBindingElement.initClass(SoTextureCoordinateBindingElement.class);
		       SoTextureCoordinateElement.initClass(SoTextureCoordinateElement.class);
		       SoTextureImageElement.initClass(SoTextureImageElement.class);
		       SoTextureMatrixElement.initClass(SoTextureMatrixElement.class);
		       SoTextureQualityElement.initClass(SoTextureQualityElement.class);
		       SoTextureOverrideElement.initClass(SoTextureOverrideElement.class);
//		       SoUnitsElement.initClass();
		       SoViewVolumeElement.initClass(SoViewVolumeElement.class);
		       SoViewingMatrixElement.initClass(SoViewingMatrixElement.class);
		       SoViewportRegionElement.initClass(SoViewportRegionElement.class);
		   
		       // GL specific elements must be initialized after their more
		       // generic counterparts
		   
		       SoGLCacheContextElement.initClass(SoGLCacheContextElement.class);
		       SoGLClipPlaneElement.initClass(SoGLClipPlaneElement.class);
		       SoGLCoordinateElement.initClass(SoGLCoordinateElement.class);
		       SoGLDrawStyleElement.initClass(SoGLDrawStyleElement.class);
		       SoGLLazyElement.initClass(SoGLLazyElement.class);
		       SoGLLightIdElement.initClass(SoGLLightIdElement.class);
		       SoGLLinePatternElement.initClass(SoGLLinePatternElement.class);
		       SoGLLineWidthElement.initClass(SoGLLineWidthElement.class);
		       SoGLModelMatrixElement.initClass(SoGLModelMatrixElement.class);
		       SoGLNormalElement.initClass(SoGLNormalElement.class);
		       SoGLPointSizeElement.initClass(SoGLPointSizeElement.class);
		       SoGLProjectionMatrixElement.initClass(SoGLProjectionMatrixElement.class);
		       SoGLRenderPassElement.initClass(SoGLRenderPassElement.class);
		       SoGLShapeHintsElement.initClass(SoGLShapeHintsElement.class);
		       SoGLTextureCoordinateElement.initClass(SoGLTextureCoordinateElement.class);
		       SoGLTextureEnabledElement.initClass(SoGLTextureEnabledElement.class);
		       SoGLTextureImageElement.initClass(SoGLTextureImageElement.class);
		       SoGLTextureMatrixElement.initClass(SoGLTextureMatrixElement.class);
		       SoGLUpdateAreaElement.initClass(SoGLUpdateAreaElement.class);
		       SoGLViewingMatrixElement.initClass(SoGLViewingMatrixElement.class);
		       SoGLViewportRegionElement.initClass(SoGLViewportRegionElement.class);
		       // Added by MeVis:
		       SoGLVBOElement.initClass(SoGLVBOElement.class);
		   
		       // Other derived classes 
		       SoBBoxModelMatrixElement.initClass(SoBBoxModelMatrixElement.class);
		       SoLocalBBoxMatrixElement.initClass(SoLocalBBoxMatrixElement.class);
		   
		       SoWindowElement.initClass(SoWindowElement.class);
		  		
	}

	// This is the initial number of slots in the array of types that's
	// indexed by stack index.  There is one slot per element class, so
	// this number should be at least as large as the number of standard
	// element classes in Inventor.
	private final static int NUM_STACK_INDICES     =  100;

	 public static void initClass(final Class<? extends SoElement> javaClass) {
		 {                        
			 if(javaClass.equals(SoElement.class)) {
				 ////////////////////////////////////////////////////////////////////////
				   //
				   // Description:
				   //    Initializes the SoElement class.
				   //
				   // Use: internal
				   
			       nextStackIndex = 0;
				   
			       // Initialize type id and unique id
			       classTypeIdMap.put(SoElement.class, SoType.createType(SoType.badType(), new SbName("Element"), null));
			   
			       // Initialize stack index to a bad value since this is an abstract
			       // class that can't appear in stacks
			       classStackIndexMap.put(SoElement.class, -1);
			   
//			   #ifdef DEBUG
			       // Create list that correlates stack indices to type id's
			       stackToType = new SoTypeList(NUM_STACK_INDICES);
//			   #endif
				 
				   //#undef NUM_STACK_INDICES
			       return;
			 }
			 
				CreateMethod createInstance = new CreateMethod() {

					@Override
					public Object run() {
						Class[] parameterTypes = new Class[0];
						try {
							Constructor<? extends SoElement> constructor =  javaClass.getConstructor(parameterTypes);
							Object[] initargs = new Object[0];
							try {
								return constructor.newInstance(initargs);
							} catch (IllegalArgumentException e) {
								throw new IllegalStateException(e);
							} catch (InstantiationException e) {
								throw new IllegalStateException(e);
							} catch (IllegalAccessException e) {
								throw new IllegalStateException(e);
							} catch (InvocationTargetException e) {
								throw new IllegalStateException(e);
							}
						} catch (SecurityException e) {
							throw new IllegalStateException(e);
						} catch (NoSuchMethodException e) {
							throw new IllegalStateException(e);
						}
					}
					
				};
			    boolean _value_false= false;                                                      
				Class<?> parentClass = javaClass.getSuperclass();
				String className = javaClass.getSimpleName();
			    do {                                                                      
			        classTypeIdMap.put(javaClass, SoType.createType(getClassTypeId(parentClass),       
			                                         new SbName(className),                
			                                         createInstance));         
			        if (classStackIndexMap.get(parentClass) < 0)                                 
			            classStackIndexMap.put(javaClass, createStackIndex(classTypeIdMap.get(javaClass)));                  
			        else                                                                  
			            classStackIndexMap.put(javaClass, classStackIndexMap.get(parentClass));                   
			    } while (_value_false);                                                   
			    }		 
	 }
	 

////////////////////////////////////////////////////////////////////////
//
// Description:
//    Prints element for debugging.
//
// Use: public
//
////////////////////////////////////////////////////////////////////////

//#ifdef DEBUG
public void
print(PrintStream fp) 
{
    fp.print( "Element: type "+typeId.getName().getString()+", depth "+depth+"\n" );
}
//#else  /* DEBUG */
//void
//SoElement::print(FILE *) const
//{
//}
//#endif /* DEBUG */

	 
	 
////////////////////////////////////////////////////////////////////////
//
// Description:
//    Returns the id for the element with the given stack index.
//
// Use: internal static, debug only!

public static SoType
getIdFromStackIndex(int stackIndex)
//
////////////////////////////////////////////////////////////////////////
{
//#ifdef DEBUG
    return stackToType.operator_square_bracket(stackIndex);
//#else
//    return SoType.badType();
//#endif
}

	 
	 public void destructor() {
		 
	 }
	 
	 protected SoState getPushedState() {
		 return pushedState;
	 }
	  }
