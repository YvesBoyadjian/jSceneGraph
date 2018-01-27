/**
 * 
 */
package jscenegraph.port;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import jscenegraph.database.inventor.errors.SoError;

/**
 * @author Yves Boyadjian
 *
 */
public class Array<T extends Mutable> {
	
	private Mutable[] array;
	private Map<Mutable,Integer> indices = new HashMap<>();

	public Array(Class<T>klass, int nb) {
		array = new Mutable[nb];
		for(int i=0;i<nb;i++) {
			try {
				array[i] = klass.getConstructor().newInstance();
				indices.put(array[i], i);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				SoError.post(e.getMessage());
			}
		}
	}
	
	/**
	 * Constructor with values
	 * @param klass
	 * @param values
	 */
	public Array(Class<T>klass, T... values) {
		int nb = values.length;
		
		array = new Mutable[nb];
		for(int i=0;i<nb;i++) {
			array[i] = values[i];
			indices.put(array[i], i);
		}
		
	}

	/**
	 * length of array
	 * @return
	 */
	public int length() {
		return array.length;
	}

	/**
	 * get ith element
	 * @param i
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T get(int i) {
		return (T)array[i];
	}

	/**
	 * sets the ith element
	 * @param i
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void set(int i, T value) {
		((T)array[i]).copyFrom(value);
	}

	public static void destructor(Array<? extends Mutable> array) {
		if(array == null) {
			return;
		}
		int nb = array.length();
		for(int i=0;i<nb;i++) {
			//array.get(i).destructor(); TODO
		}
	}

	public static int minus(Mutable object, Array<? extends Mutable> array) {
		return array.indices.get(object);
	}
}
