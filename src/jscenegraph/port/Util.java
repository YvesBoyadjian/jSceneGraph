/**
 * 
 */
package jscenegraph.port;

import java.nio.ByteBuffer;

import com.jogamp.common.nio.Buffers;

import jscenegraph.database.inventor.SbVec2f;
import jscenegraph.database.inventor.SbVec3f;

/**
 * @author Yves Boyadjian
 *
 */
public class Util {

	/**
	 * Integer conversion
	 * 
	 * @param objArray
	 * @return
	 */
	public static int[] toIntArray(Integer[] objArray) {
		int length = objArray.length;
		int[] retValue = new int[length];
		for(int i=0; i< length;i++) {
			retValue[i] = objArray[i];
		}
		return retValue;
	}
	
	/**
	 * Integer conversion
	 * 
	 * @param objArray
	 * @return
	 */
	public static ByteBuffer toByteBuffer(Integer[] objArray) {
		int arrayLength = objArray.length;
		int nbBytes = arrayLength * Integer.SIZE / Byte.SIZE;
		ByteBuffer retVal = Buffers.newDirectByteBuffer(nbBytes);
		for(int i=0; i< arrayLength;i++) {
			int value = objArray[i];
			retVal.putInt(value);
		}
		retVal.rewind();
		return retVal;
	}

	public static ByteBuffer toByteBuffer(SbVec3f[] objArray) {
		int arrayLength = objArray.length;
		int nbBytes = arrayLength * 3 * Float.SIZE / Byte.SIZE;
		ByteBuffer retVal = Buffers.newDirectByteBuffer(nbBytes);
		for(int i=0; i< arrayLength;i++) {
			float[] value = objArray[i].getValue();
			retVal.putFloat(value[0]);
			retVal.putFloat(value[1]);
			retVal.putFloat(value[2]);
		}
		retVal.rewind();
		return retVal;
	}

	public static ByteBuffer toByteBuffer(SbVec2f[] objArray) {
		int arrayLength = objArray.length;
		int nbBytes = arrayLength * 2 * Float.SIZE / Byte.SIZE;
		ByteBuffer retVal = Buffers.newDirectByteBuffer(nbBytes);
		for(int i=0; i< arrayLength;i++) {
			float[] value = objArray[i].getValue();
			retVal.putFloat(value[0]);
			retVal.putFloat(value[1]);
		}
		retVal.rewind();
		return retVal;
	}

	/**
	 * Memory comparison
	 * 
	 * @param bytes
	 * @param bytes2
	 * @param nbElem
	 * @return
	 */
	public static int memcmp(byte[] bytes, byte[] bytes2, int nbElem) {
		
		for(int i=0; i<nbElem;i++) {
			Byte left = bytes[i];
			Byte right = bytes2[i];
			int compare = left.compareTo(right);
			if(compare != 0) {
				return compare;
			}
		}
		return 0;
	}

	public static void memcpy(byte[] destBytes, byte[] srcBytes, int numBytes) {
		for(int i = 0; i< numBytes; i++) {
			destBytes[i] = srcBytes[i];
		}
	}
}
