/**
 * 
 */
package jscenegraph.port;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.jogamp.common.nio.Buffers;

import jscenegraph.database.inventor.SbVec2f;
import jscenegraph.database.inventor.SbVec3f;
import jscenegraph.database.inventor.SbVec4f;

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

	public static ByteBuffer toByteBuffer(SbVec4f[] objArray) {
		int arrayLength = objArray.length;
		int nbBytes = arrayLength * 4 * Float.SIZE / Byte.SIZE;
		ByteBuffer retVal = Buffers.newDirectByteBuffer(nbBytes);
		for(int i=0; i< arrayLength;i++) {
			float[] value = objArray[i].getValue();
			retVal.putFloat(value[0]);
			retVal.putFloat(value[1]);
			retVal.putFloat(value[2]);
			retVal.putFloat(value[3]);
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

	// java port
	public static void memcpy(byte[] destBytes, int destIndex, byte[] srcBytes, int numBytes) {
		for(int i = 0; i< numBytes; i++) {
			destBytes[i+destIndex] = srcBytes[i];
		}
	}

	public static int strncmp(String str1, String str2, int n) {
		int index=0;
		while(index<str1.length() && index< str2.length() && index <n) {
			char c1 = str1.charAt(index);
			char c2 = str2.charAt(index);
			if( c1 != c2) {
				return c1 - c2;
			}
			index++;
		}
		if(index < n) {
			if(index == str1.length()) {
				return -1;
			}
			else if(index == str2.length()) {
				return 1;
			}
		}
		return 0;
	}

	public static int strstr(String str1, String str2) {
		return str1.indexOf(str2);
	}

	public static int atoi(String str) {
		return Integer.valueOf(str);
	}

	/**
	 * Transform the array to a String, eliminating zero ending character
	 * @param str
	 * @return
	 */
	public static String toString(char[] str) {
		int len = str.length;
		int endIndex = len;
		for(int i=0;i<len;i++) {
			if(str[i] == 0) {
				endIndex = i;
				break;
			}
		}
		return new String(Arrays.copyOfRange(str,0,endIndex));
	}
}
