/**
 * 
 */
package jscenegraph.nodekits.inventor.nodekits;

import java.util.HashMap;
import java.util.Map;

import jscenegraph.database.inventor.nodes.SoNode;
import jscenegraph.database.inventor.nodes.SoSubNode;
import jscenegraph.interaction.inventor.nodekits.SoInteractionKit;

/**
 * @author Yves Boyadjian
 *
 */
public class SoSubKit extends SoSubNode {

	  private  static final Map<Class,SoNodekitCatalog[]> nodekitCatalog = new HashMap<Class,SoNodekitCatalog[]>(); /* design of this class */                                   
	  private  static final Map<Class, SoNodekitCatalog[][]>    parentNodekitCatalogPtr = new HashMap<Class,SoNodekitCatalog[][]>(); /* parent design */
	
	
	SoSubKit(Class<? extends SoNode> class1, SoNode parent) {
		super(class1, parent);
		
	}

	public static SoSubKit SO_KIT_HEADER(Class<SoInteractionKit> class1,
			SoBaseKit parent) {
		SoSubKit soSubKit = new SoSubKit(class1,parent);
		  
		  if( !nodekitCatalog.containsKey(class1)) {
			  throw new IllegalStateException("Class "+ class1 + " not initialized");
		  }
		
		return soSubKit;
	}

	public static SoNodekitCatalog[] getClassNodekitCatalogPtr(Class klass)
		            { return nodekitCatalog.get(klass); }
		   	
}
