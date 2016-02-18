/**
 * 
 */
package jscenegraph.interaction.inventor.draggers;

import jscenegraph.database.inventor.misc.SoCallbackList;
import jscenegraph.database.inventor.misc.SoCallbackListCB;
import jscenegraph.interaction.inventor.nodekits.SoInteractionKit;

/**
 * @author Yves Boyadjian
 *
 */
public class SoDragger extends SoInteractionKit {
	
	  private	    SoCallbackList startCallbacks;
	  private	    SoCallbackList motionCallbacks;
	  private	    SoCallbackList finishCallbacks;
	  private	    SoCallbackList valueChangedCallbacks;
		   
	  private	    SoCallbackList otherEventCallbacks;
		   	
	  private   boolean    valueChangedCallbacksEnabled;

	// Motion callbacks are called after each movement of the mouse during dragging.  
	public void addMotionCallback(SoDraggerCB f, Object userData) {
		 motionCallbacks.addCallback( (SoCallbackListCB )f, userData ); 
	}
	
	// Motion callbacks are called after each movement of the mouse during dragging. 
	public void removeMotionCallback(SoDraggerCB f, Object userData) {
		  motionCallbacks.removeCallback( (SoCallbackListCB )f, userData ); 
	}

	// Finish callbacks are made after dragging ends and the dragger has stopped grabbing events. 
	public void addFinishCallback(SoDraggerCB f, Object userData) {
		 finishCallbacks.addCallback( (SoCallbackListCB )f, userData ); 
	}
	
	/**
	 * You can temporarily disable a dragger's valueChangedCallbacks. 
	 * The method returns a value that tells you if callbacks were already enabled. 
	 * Use this method if you write a valueChanged callback of your own 
	 * and you change one of the dragger's fields within the callback. 
	 * (For example, when writing a callback to constrain your dragger). 
	 * Disable first, then change the field, then re-enable the callbacks 
	 * (if they were enabled to start with). 
	 * All this prevents you from entering an infinite loop of changing values, 
	 * calling callbacks which change values, etc. 
	 * 
	 * @param newVal
	 * @return
	 */
	public final boolean enableValueChangedCallbacks(boolean newVal) {
		
	     boolean oldVal = valueChangedCallbacksEnabled;
	          valueChangedCallbacksEnabled = newVal;
	          return oldVal;
	     }
	
	 ////////////////////////////////////////////////////////////////////////
	   //
	   // Description:
	   //    This initializes ALL Inventor dragger classes.
	   //
	   // Use: internal
	   
	   public static void
	   initClasses()
	   //
	   ////////////////////////////////////////////////////////////////////////
	   {
	       SoDragger.initClass();
	       // simple scale draggers
	       //SoScale1Dragger.initClass();
	       //SoScale2Dragger.initClass();
	       //SoScale2UniformDragger.initClass();
	       //SoScaleUniformDragger.initClass();
	       // simple translate draggers
	       SoTranslate1Dragger.initClass();
	       SoTranslate2Dragger.initClass();
	       // simple rotation draggers
	       //SoRotateSphericalDragger.initClass();
	       //SoRotateCylindricalDragger.initClass();
	       //SoRotateDiscDragger.initClass();
	       // coord draggers
	       //SoDragPointDragger.initClass();
	       // transform draggers
	       //SoJackDragger.initClass();
	       //SoHandleBoxDragger.initClass();
	       //SoCenterballDragger.initClass();
	       //SoTabPlaneDragger.initClass();
	       SoTabBoxDragger.initClass();
	       //SoTrackballDragger.initClass();
	       // composite transform draggers
	       // init these after all the canonical draggers
	       //SoPointLightDragger.initClass();
	       //SoTransformBoxDragger.initClass();
	       //SoTransformerDragger.initClass();
	       // lightDraggers
	       //SoDirectionalLightDragger.initClass();
	       //SoSpotLightDragger.initClass();
	   }
	   	
	   ////////////////////////////////////////////////////////////////////////
	    //
	    // Description:
	    //    Initialize the dragger
	    //
	    // Use: public, internal
	    //
	   public static void
	    initClass()
	    //
	    ////////////////////////////////////////////////////////////////////////
	    {
	        SO__KIT_INIT_CLASS(SoDragger.class, "Dragger", SoInteractionKit.class);
	    }
	    }
